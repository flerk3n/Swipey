"""Notion integration — push saved ideas to a Notion database.

Each idea becomes one Notion page with:
- Title       : the idea title
- Niche       : select property (tech_reviews / gaming / fashion)
- Category    : the content-type label (UNBOXING, REVIEW, GRWM, …)
- Tags        : multi-select from the idea's hashtag list
- Content     : the full generated script (paragraph block), or the description
- Source      : seed | gemini
- Idea ID     : the backend id for de-duplication

Uses the Notion REST API directly — no SDK needed.
"""
import json
from typing import Dict, List
from urllib.error import HTTPError
from urllib.request import Request, urlopen

from ..config import settings

NOTION_API = "https://api.notion.com/v1"
NOTION_VERSION = "2022-06-28"


class NotionUnavailable(RuntimeError):
    """Raised when Notion credentials are not configured."""


def _headers() -> Dict[str, str]:
    if not settings.notion_enabled:
        raise NotionUnavailable(
            "NOTION_TOKEN or NOTION_DATABASE_ID is not set. "
            "Add them to .env to enable Notion sync."
        )
    return {
        "Authorization": f"Bearer {settings.notion_token}",
        "Notion-Version": NOTION_VERSION,
        "Content-Type": "application/json",
    }


def _request(method: str, path: str, body: Dict) -> Dict:
    url = f"{NOTION_API}{path}"
    data = json.dumps(body).encode()
    req = Request(url, data=data, headers=_headers(), method=method)
    try:
        with urlopen(req, timeout=15) as resp:
            return json.loads(resp.read())
    except HTTPError as exc:
        msg = exc.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"Notion API error {exc.code}: {msg}") from exc


def _clean_tags(tags: List[str]) -> List[str]:
    """Strip leading # and return unique non-empty tags."""
    seen: List[str] = []
    for t in tags:
        clean = t.lstrip("#").strip()
        if clean and clean not in seen:
            seen.append(clean)
    return seen


def push_idea(idea: Dict) -> str:
    """Push one idea to the Notion database. Returns the created page URL."""
    tags = _clean_tags(idea.get("tags") or [])
    content = (idea.get("full_text") or "").strip() or idea.get("description", "")
    title = idea.get("title", "Untitled")
    niche = idea.get("niche", "")
    category = idea.get("content_type", "").replace("_", " ").upper()
    source = idea.get("source", "seed")
    idea_id = idea.get("id", "")

    properties: Dict = {
        "title": {
            "title": [{"text": {"content": title}}]
        },
        "Niche": {
            "select": {"name": niche} if niche else None
        },
        "Category": {
            "select": {"name": category} if category else None
        },
        "Tags": {
            "multi_select": [{"name": t} for t in tags]
        },
        "Source": {
            "select": {"name": source}
        },
        "Idea ID": {
            "rich_text": [{"text": {"content": idea_id}}]
        },
    }

    # Remove None-valued properties (Notion rejects null selects).
    properties = {k: v for k, v in properties.items() if v is not None}

    # Content goes into the page body as a paragraph block.
    children = []
    if content:
        # Split into ≤2000-char chunks (Notion's rich_text limit per block).
        for chunk in _chunk(content, 2000):
            children.append({
                "object": "block",
                "type": "paragraph",
                "paragraph": {
                    "rich_text": [{"type": "text", "text": {"content": chunk}}]
                },
            })

    body: Dict = {
        "parent": {"database_id": settings.notion_database_id},
        "properties": properties,
    }
    if children:
        body["children"] = children

    result = _request("POST", "/pages", body)
    return result.get("url", "")


def _chunk(text: str, size: int) -> List[str]:
    return [text[i: i + size] for i in range(0, len(text), size)]


def ensure_columns() -> None:
    """Add missing columns to the database schema (idempotent).

    Notion auto-creates select/multi-select options on first use, but we need
    the columns themselves to exist. Safe to call on every startup.
    """
    if not settings.notion_enabled:
        return
    try:
        _request(
            "PATCH",
            f"/databases/{settings.notion_database_id}",
            {
                "properties": {
                    "Niche":    {"select": {}},
                    "Category": {"select": {}},
                    "Tags":     {"multi_select": {}},
                    "Source":   {"select": {}},
                    "Idea ID":  {"rich_text": {}},
                }
            },
        )
    except Exception:
        # Non-fatal — columns may already exist.
        pass
