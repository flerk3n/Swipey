"""Extract negative feedback markers from rejected ideas.

When a user rejects an idea we capture lightweight 'markers' (topics / patterns
they disliked) and persist them. Future generation prompts inject these as
'do not' guidance — a RAG-lite behavioural filter.

Uses Gemini when available for nuanced markers; otherwise falls back to a
heuristic that mines the idea's tags and summary tokens.
"""
from typing import Dict, List

from .. import repository

# Generic tokens that carry no signal as a 'dislike' marker.
_STOPWORDS = {
    "the", "a", "an", "and", "or", "to", "of", "in", "on", "for", "with",
    "review", "video", "phone", "game", "live", "first", "look", "best",
    "this", "that", "your", "you", "it", "is", "are", "how", "why", "what",
}


def heuristic_markers(idea: Dict, max_markers: int = 3) -> List[str]:
    """Derive markers from an idea's tags first, then summary tokens."""
    markers: List[str] = []

    for tag in idea.get("tags", []):
        cleaned = tag.lstrip("#").strip().lower()
        if cleaned and cleaned not in _STOPWORDS and cleaned not in markers:
            markers.append(cleaned)
        if len(markers) >= max_markers:
            return markers

    tokens = (idea.get("summary_tokens") or "").replace(",", " ").split()
    for tok in tokens:
        cleaned = tok.strip().lower()
        if cleaned and cleaned not in _STOPWORDS and cleaned not in markers:
            markers.append(cleaned)
        if len(markers) >= max_markers:
            break

    return markers


def record_rejection(user_id: str, idea: Dict) -> List[str]:
    """Extract markers for a rejected idea and persist them. Returns markers."""
    markers: List[str] = []

    # Prefer Gemini-derived markers when a key is configured.
    try:
        from .gemini import extract_markers as gemini_markers

        markers = gemini_markers(idea)
    except Exception:
        markers = []

    if not markers:
        markers = heuristic_markers(idea)

    repository.add_markers(user_id, markers)
    return markers


def negative_prompt_block(user_id: str) -> str:
    """Build the 'avoid these' instruction block from stored markers."""
    markers = repository.list_markers(user_id)
    if not markers:
        return ""
    unique = sorted(set(markers))
    joined = ", ".join(unique)
    return (
        "The user has previously rejected ideas about these patterns/topics. "
        "AVOID them: " + joined + "."
    )
