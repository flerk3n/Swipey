"""Core idea endpoints: deck, next, swipe, saved, echo, generate, catalog."""
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query

from .. import repository
from ..deps import current_user
from ..models import (
    EchoResponse,
    GenerateRequest,
    GenerateResponse,
    IdeaModel,
    NicheInfo,
    SwipeRequest,
    SwipeResponse,
    TagsUpdateRequest,
)
from ..seed import NICHES, TRENDS
from ..services import dedup, feedback
from ..services.gemini import GeminiUnavailable, expand_echo, generate_ideas
from ..config import settings

router = APIRouter(tags=["ideas"])

_VALID_ACTIONS = {"approve", "reject", "save"}


@router.get("/ideas/deck", response_model=List[IdeaModel])
def get_deck(
    limit: int = Query(20, ge=1, le=100),
    niche: Optional[str] = Query(None, description="Filter the deck to one niche"),
    user_id: str = Depends(current_user),
) -> List[IdeaModel]:
    rows = repository.list_deck(user_id, limit=limit, niche=niche)
    return [repository.to_idea_model(r) for r in rows]


@router.get("/ideas/next", response_model=IdeaModel)
def get_next(user_id: str = Depends(current_user)) -> IdeaModel:
    row = repository.next_idea(user_id)
    if not row:
        raise HTTPException(status_code=404, detail="No pending ideas. Deck is empty.")
    return repository.to_idea_model(row)


@router.get("/ideas/saved", response_model=List[IdeaModel])
def get_saved(user_id: str = Depends(current_user)) -> List[IdeaModel]:
    rows = repository.list_saved(user_id)
    return [repository.to_idea_model(r) for r in rows]


@router.post("/ideas/{idea_id}/swipe", response_model=SwipeResponse)
def swipe(
    idea_id: str,
    body: SwipeRequest,
    user_id: str = Depends(current_user),
) -> SwipeResponse:
    action = body.action.strip().lower()
    if action not in _VALID_ACTIONS:
        raise HTTPException(
            status_code=422,
            detail="action must be one of: approve, reject, save",
        )

    idea = repository.get_idea(idea_id)
    if not idea:
        raise HTTPException(status_code=404, detail="Idea not found.")

    saved = False
    if action == "approve":
        repository.set_status(idea_id, "approved")
        repository.record_swipe(user_id, idea_id, "right")
        repository.add_saved(user_id, idea_id)
        saved = True
        status = "approved"
    elif action == "reject":
        repository.set_status(idea_id, "rejected")
        repository.record_swipe(user_id, idea_id, "left")
        feedback.record_rejection(user_id, idea)
        status = "rejected"
    else:  # save
        repository.record_swipe(user_id, idea_id, "up")
        repository.add_saved(user_id, idea_id)
        saved = True
        status = idea.get("status", "pending")

    return SwipeResponse(id=idea_id, action=action, status=status, saved=saved)


@router.delete("/ideas/{idea_id}/saved", response_model=SwipeResponse)
def unsave(idea_id: str, user_id: str = Depends(current_user)) -> SwipeResponse:
    idea = repository.get_idea(idea_id)
    if not idea:
        raise HTTPException(status_code=404, detail="Idea not found.")
    repository.remove_saved(user_id, idea_id)
    return SwipeResponse(
        id=idea_id, action="unsave", status=idea.get("status", "pending"), saved=False
    )


@router.patch("/ideas/{idea_id}/tags", response_model=IdeaModel)
def update_tags(
    idea_id: str,
    body: TagsUpdateRequest,
    user_id: str = Depends(current_user),
) -> IdeaModel:
    idea = repository.get_idea(idea_id)
    if not idea:
        raise HTTPException(status_code=404, detail="Idea not found.")
    cleaned = [t.strip() for t in body.tags if t and t.strip()]
    repository.set_tags(idea_id, cleaned)
    updated = repository.get_idea(idea_id)
    return repository.to_idea_model(updated)


@router.post("/ideas/{idea_id}/echo", response_model=EchoResponse)
def echo(idea_id: str, user_id: str = Depends(current_user)) -> EchoResponse:
    idea = repository.get_idea(idea_id)
    if not idea:
        raise HTTPException(status_code=404, detail="Idea not found.")

    # Return cached full_text if it already exists.
    existing = (idea.get("full_text") or "").strip()
    if existing:
        return EchoResponse(id=idea_id, full_text=existing, generated=False)

    try:
        full_text = expand_echo(idea)
    except GeminiUnavailable as exc:
        raise HTTPException(status_code=503, detail=str(exc))

    if not full_text:
        raise HTTPException(status_code=502, detail="Gemini returned an empty expansion.")

    repository.set_full_text(idea_id, full_text)
    return EchoResponse(id=idea_id, full_text=full_text, generated=True)


@router.post("/generate", response_model=GenerateResponse)
def generate(body: GenerateRequest, user_id: str = Depends(current_user)) -> GenerateResponse:
    niche = body.niche.strip()
    if niche not in TRENDS:
        valid = ", ".join(TRENDS.keys())
        raise HTTPException(status_code=422, detail="niche must be one of: " + valid)

    avoid_block = feedback.negative_prompt_block(user_id)
    trends = TRENDS.get(niche, [])

    try:
        candidates = generate_ideas(niche, body.count, trends, avoid_block)
    except GeminiUnavailable as exc:
        raise HTTPException(status_code=503, detail=str(exc))

    if not candidates:
        return GenerateResponse(
            niche=niche, requested=body.count, generated=0, deduped=0, ideas=[]
        )

    # Dedup against everything already stored for this user in the niche.
    existing_tokens = repository.list_existing_tokens(user_id, niche=niche)
    candidate_tokens = [c["summary_tokens"] for c in candidates]
    keep_indices = dedup.filter_unique(
        candidate_tokens, existing_tokens, settings.dedup_threshold
    )
    deduped = len(candidates) - len(keep_indices)

    stored: List[IdeaModel] = []
    for idx in keep_indices:
        c = candidates[idx]
        row = repository.insert_idea(
            user_id=user_id,
            niche=niche,
            content_type=c["content_type"],
            title=c["title"],
            description=c["description"],
            summary_tokens=c["summary_tokens"],
            tags=c["tags"],
            full_text=c["full_text"],
            status="pending",
            source="gemini",
        )
        if row:  # None when UNIQUE(title) collided
            stored.append(repository.to_idea_model(row))

    return GenerateResponse(
        niche=niche,
        requested=body.count,
        generated=len(stored),
        deduped=deduped,
        ideas=stored,
    )


@router.get("/catalog/niches", response_model=List[NicheInfo])
def catalog_niches() -> List[NicheInfo]:
    return [
        NicheInfo(id=n["id"], label=n["label"], description=n["description"])
        for n in NICHES
    ]
