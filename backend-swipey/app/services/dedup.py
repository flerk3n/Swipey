"""Near-duplicate detection via Jaccard overlap on summary tokens.

An idea is a duplicate of an existing one when the proportion of shared tokens
(intersection / union) meets or exceeds the configured threshold.
"""
from typing import Iterable, List, Set


def _tokenize(summary_tokens: str) -> Set[str]:
    """Split a comma/space separated token string into a normalised set."""
    if not summary_tokens:
        return set()
    raw = summary_tokens.replace(",", " ").split()
    return {t.strip().lower() for t in raw if t.strip()}


def jaccard(a: str, b: str) -> float:
    """Jaccard similarity between two token strings (0.0 – 1.0)."""
    set_a, set_b = _tokenize(a), _tokenize(b)
    if not set_a or not set_b:
        return 0.0
    intersection = set_a & set_b
    union = set_a | set_b
    return len(intersection) / len(union)


def is_duplicate(candidate: str, existing: Iterable[str], threshold: float) -> bool:
    """True if ``candidate`` overlaps any existing token string >= threshold."""
    for other in existing:
        if jaccard(candidate, other) >= threshold:
            return True
    return False


def filter_unique(
    candidates: List[str], existing: List[str], threshold: float
) -> List[int]:
    """Return indices of candidates that are unique against existing + earlier
    survivors in the same batch (so a batch can't dupe itself)."""
    survivors_tokens: List[str] = list(existing)
    kept: List[int] = []
    for idx, cand in enumerate(candidates):
        if is_duplicate(cand, survivors_tokens, threshold):
            continue
        kept.append(idx)
        survivors_tokens.append(cand)
    return kept
