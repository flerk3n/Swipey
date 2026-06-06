"""Liveness + configuration introspection."""
from fastapi import APIRouter, Depends

from ..config import settings
from ..deps import current_user
from ..models import HealthResponse

router = APIRouter(tags=["health"])


@router.get("/health", response_model=HealthResponse)
def health(user_id: str = Depends(current_user)) -> HealthResponse:
    return HealthResponse(
        status="ok",
        gemini_enabled=settings.gemini_enabled,
        model=settings.gemini_model,
        user_id=user_id,
    )
