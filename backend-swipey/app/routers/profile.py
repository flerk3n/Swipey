"""Profile + settings endpoints (single demo user, keyed by X-User-Id)."""
from fastapi import APIRouter, Depends

from .. import repository
from ..deps import current_user
from ..models import (
    ProfileResponse,
    ProfileUpdateRequest,
    SettingsModel,
)

router = APIRouter(tags=["profile"])


def _to_response(row: dict) -> ProfileResponse:
    return ProfileResponse(
        user_id=row["user_id"],
        display_name=row.get("display_name", "Editor"),
        niche_id=row.get("niche_id"),
        goal_id=row.get("goal_id"),
        onboarded=bool(row.get("onboarded", False)),
        settings=SettingsModel(**row.get("settings", {})),
    )


@router.get("/profile", response_model=ProfileResponse)
def get_profile(user_id: str = Depends(current_user)) -> ProfileResponse:
    return _to_response(repository.get_profile(user_id))


@router.put("/profile", response_model=ProfileResponse)
def update_profile(
    body: ProfileUpdateRequest,
    user_id: str = Depends(current_user),
) -> ProfileResponse:
    row = repository.update_profile(
        user_id,
        display_name=body.display_name,
        niche_id=body.niche_id,
        goal_id=body.goal_id,
        onboarded=body.onboarded,
    )
    return _to_response(row)


@router.get("/profile/settings", response_model=SettingsModel)
def get_settings(user_id: str = Depends(current_user)) -> SettingsModel:
    return SettingsModel(**repository.get_profile(user_id)["settings"])


@router.put("/profile/settings", response_model=SettingsModel)
def update_settings(
    body: SettingsModel,
    user_id: str = Depends(current_user),
) -> SettingsModel:
    row = repository.update_settings(user_id, body.model_dump())
    return SettingsModel(**row["settings"])
