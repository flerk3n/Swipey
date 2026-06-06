"""Shared FastAPI dependencies."""
from typing import Optional

from fastapi import Header

from .config import settings


def current_user(x_user_id: Optional[str] = Header(default=None)) -> str:
    """Resolve the acting user from the X-User-Id header, defaulting to demo."""
    if x_user_id and x_user_id.strip():
        return x_user_id.strip()
    return settings.default_user_id
