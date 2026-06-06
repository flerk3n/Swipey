"""Notion sync endpoints."""
from typing import Optional

from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel

from .. import repository
from ..config import settings
from ..deps import current_user
from ..services.notion import NotionUnavailable, push_idea

router = APIRouter(tags=["notion"])


class NotionPushResponse(BaseModel):
    idea_id: str
    notion_url: str
    title: str


class NotionStatusResponse(BaseModel):
    enabled: bool
    database_id: Optional[str] = None


@router.get("/notion/status", response_model=NotionStatusResponse)
def notion_status() -> NotionStatusResponse:
    """Check whether Notion is configured."""
    return NotionStatusResponse(
        enabled=settings.notion_enabled,
        database_id=settings.notion_database_id if settings.notion_enabled else None,
    )


@router.post("/ideas/{idea_id}/notion", response_model=NotionPushResponse)
def push_to_notion(
    idea_id: str,
    user_id: str = Depends(current_user),
) -> NotionPushResponse:
    """Push a saved idea to the Notion database."""
    if not settings.notion_enabled:
        raise HTTPException(
            status_code=503,
            detail="Notion is not configured. Add NOTION_TOKEN and NOTION_DATABASE_ID to .env.",
        )

    idea = repository.get_idea(idea_id)
    if not idea:
        raise HTTPException(status_code=404, detail="Idea not found.")

    try:
        notion_url = push_idea(idea)
    except NotionUnavailable as exc:
        raise HTTPException(status_code=503, detail=str(exc))
    except Exception as exc:
        raise HTTPException(status_code=502, detail=f"Notion push failed: {exc}")

    return NotionPushResponse(
        idea_id=idea_id,
        notion_url=notion_url,
        title=idea.get("title", ""),
    )


@router.post("/saved/notion", response_model=list)
def push_all_saved_to_notion(
    user_id: str = Depends(current_user),
) -> list:
    """Push ALL saved ideas to Notion in one call."""
    if not settings.notion_enabled:
        raise HTTPException(status_code=503, detail="Notion is not configured.")

    saved = repository.list_saved(user_id)
    if not saved:
        return []

    results = []
    for idea in saved:
        try:
            url = push_idea(idea)
            results.append({"idea_id": idea["id"], "title": idea["title"], "notion_url": url, "ok": True})
        except Exception as exc:
            results.append({"idea_id": idea["id"], "title": idea["title"], "error": str(exc), "ok": False})

    return results
