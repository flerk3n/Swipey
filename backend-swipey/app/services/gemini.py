"""Gemini integration: idea generation, echo expansion, marker extraction.

All functions degrade gracefully:
- ``generate_ideas`` / ``expand_echo`` raise ``GeminiUnavailable`` when no key
  is configured; routers translate that into a clean HTTP 503.
- ``extract_markers`` returns an empty list on any failure so callers can fall
  back to the heuristic.

The SDK is imported lazily so the app boots (and the seed loop works) even if
``google-generativeai`` is not installed.
"""
import json
import re
from typing import Dict, List, Optional

from ..config import settings


class GeminiUnavailable(RuntimeError):
    """Raised when a generation call is attempted without a working Gemini setup."""


def _get_model():
    """Configure and return a GenerativeModel, or raise GeminiUnavailable."""
    if not settings.gemini_enabled:
        raise GeminiUnavailable(
            "GEMINI_API_KEY is not set. Add it to .env to enable generation; "
            "the seed deck still works without it."
        )
    try:
        import google.generativeai as genai
    except ImportError as exc:  # pragma: no cover - import-time environment issue
        raise GeminiUnavailable(
            "google-generativeai is not installed. Run pip install -r requirements.txt."
        ) from exc

    genai.configure(api_key=settings.gemini_api_key)
    return genai.GenerativeModel(settings.gemini_model)


def _extract_json(text: str) -> Optional[object]:
    """Pull a JSON array/object out of a model response (handles code fences)."""
    if not text:
        return None
    fenced = re.search(r"```(?:json)?\s*(.*?)```", text, re.DOTALL)
    candidate = fenced.group(1) if fenced else text
    candidate = candidate.strip()
    # Narrow to the outermost array if there is leading/trailing prose.
    start = candidate.find("[")
    end = candidate.rfind("]")
    if start != -1 and end != -1 and end > start:
        candidate = candidate[start:end + 1]
    try:
        return json.loads(candidate)
    except json.JSONDecodeError:
        return None


# --- Idea generation ---------------------------------------------------------

_NICHE_PERSONA = {
    "tech_reviews": "a popular smartphone reviewer on YouTube",
    "gaming": "a popular gaming streamer and reviewer",
    "fashion": "a popular fashion influencer on Instagram and TikTok",
}


def generate_ideas(
    niche: str,
    count: int,
    trends: List[str],
    avoid_block: str = "",
) -> List[Dict]:
    """Ask Gemini for ``count`` structured content ideas for a niche.

    Returns a list of dicts with keys: title, description, full_text,
    content_type, tags (list), summary_tokens (str).
    """
    model = _get_model()
    persona = _NICHE_PERSONA.get(niche, "a content creator")
    trend_lines = "\n".join("- " + t for t in trends) if trends else "- (general evergreen topics)"

    from ..seed import device_facts_block_for
    facts_block = device_facts_block_for(" ".join(trends) + " " + niche)
    facts_section = (
        facts_block
        + "\n\nSTRICT RULE: If an idea mentions a device named in the VERIFIED DEVICE FACTS, "
        "every specification you state about it MUST come from those facts verbatim in meaning. "
        "Never invent or guess specs, prices, benchmarks, or features not listed there. "
        "If a detail is not in the facts, do not state it.\n"
        if facts_block
        else ""
    )

    prompt = f"""You are a content strategist for {persona} (niche: "{niche}").
Generate exactly {count} fresh, specific content ideas.

CURRENT TRENDS (use these as the hook for the trending ideas):
{trend_lines}

{facts_section}
REQUIREMENTS:
- Roughly half of the ideas (about 4-5 out of every 10) MUST tie directly to one of the
  CURRENT TRENDS above — name the specific trend/product in the title or description.
- The remaining ideas can be strong evergreen staples for this niche.
- Make every idea concrete and distinct; no near-duplicates.

{avoid_block}

Return ONLY a JSON array. Each element must be an object with these keys:
- "title": a punchy hook (max ~9 words)
- "description": 1-2 sentence teaser describing the angle
- "full_text": a short script/outline (3-5 sentences) the creator could film/post
- "content_type": one of "tweet","short_form","long_form","post","live_stream","unboxing","review","grwm","haul","lookbook","guide"
- "tags": array of 2-3 hashtag strings like "#Camera"
- "summary_tokens": a single comma-separated string of EXACTLY 10 lowercase nouns/verbs
  capturing the idea (used for dedup; no stopwords)

No prose outside the JSON array."""

    response = model.generate_content(prompt)
    parsed = _extract_json(getattr(response, "text", "") or "")
    if not isinstance(parsed, list):
        return []

    ideas: List[Dict] = []
    for item in parsed:
        if not isinstance(item, dict):
            continue
        tags = item.get("tags") or []
        if not isinstance(tags, list):
            tags = [str(tags)]
        ideas.append(
            {
                "title": str(item.get("title", "")).strip(),
                "description": str(item.get("description", "")).strip(),
                "full_text": str(item.get("full_text", "")).strip(),
                "content_type": str(item.get("content_type", "short_form")).strip() or "short_form",
                "tags": [str(t).strip() for t in tags if str(t).strip()],
                "summary_tokens": str(item.get("summary_tokens", "")).strip(),
            }
        )
    return [i for i in ideas if i["title"]]


# --- Echo expansion ----------------------------------------------------------

def expand_echo(idea: Dict) -> str:
    """Expand a single idea into a full script/post for the typewriter reveal."""
    model = _get_model()
    tags = ", ".join(idea.get("tags", []))

    from ..seed import device_facts_block_for
    facts_block = device_facts_block_for(
        " ".join([idea.get("title", ""), idea.get("description", ""), tags]),
    )
    facts_section = (
        "\n\n"
        + facts_block
        + "\n\nSTRICT RULE: If you mention a device named above, only state specs that "
        "appear in the VERIFIED DEVICE FACTS. Never invent specs, prices, or features.\n"
        if facts_block
        else ""
    )

    prompt = f"""Expand this content idea into a ready-to-use script/post.
Write in an engaging, first-person creator voice. 150-220 words. Plain text only,
no markdown headers.

Title: {idea.get('title', '')}
Angle: {idea.get('description', '')}
Format: {idea.get('content_type', '')}
Tags: {tags}
{facts_section}"""
    response = model.generate_content(prompt)
    return (getattr(response, "text", "") or "").strip()


# --- Marker extraction (best-effort, never raises) ---------------------------

def extract_markers(idea: Dict) -> List[str]:
    """Return up to 3 dislike markers for a rejected idea. [] on any failure."""
    if not settings.gemini_enabled:
        return []
    try:
        model = _get_model()
        prompt = f"""A user rejected this content idea. In a JSON array of up to 3 short
lowercase strings, name the topics or patterns they likely disliked (1-2 words each).

Title: {idea.get('title', '')}
Description: {idea.get('description', '')}
Tags: {", ".join(idea.get('tags', []))}

Return ONLY the JSON array."""
        response = model.generate_content(prompt)
        parsed = _extract_json(getattr(response, "text", "") or "")
        if isinstance(parsed, list):
            return [str(m).strip().lower() for m in parsed if str(m).strip()][:3]
    except Exception:
        return []
    return []
