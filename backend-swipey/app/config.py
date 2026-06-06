"""Environment-backed configuration for the Swipey backend.

Loads from a local .env file (via python-dotenv) and exposes a single
``settings`` object. Kept dependency-light and Python 3.9 compatible.
"""
import os
from pathlib import Path
from typing import Optional

from dotenv import load_dotenv

# Project root = backend-swipey/ (this file lives in backend-swipey/app/).
BASE_DIR = Path(__file__).resolve().parent.parent

# Load .env if present; real environment variables still take precedence.
load_dotenv(BASE_DIR / ".env")


def _as_float(value: Optional[str], default: float) -> float:
    try:
        return float(value) if value is not None and value != "" else default
    except ValueError:
        return default


class Settings:
    """Resolved runtime configuration."""

    def __init__(self) -> None:
        self.gemini_api_key: str = os.getenv("GEMINI_API_KEY", "").strip()
        self.gemini_model: str = os.getenv("GEMINI_MODEL", "gemini-3.1-flash-lite").strip()

        db_path = os.getenv("DATABASE_PATH", "data/swipey.db").strip()
        db = Path(db_path)
        self.database_path: Path = db if db.is_absolute() else (BASE_DIR / db)

        self.default_user_id: str = os.getenv("DEFAULT_USER_ID", "demo").strip() or "demo"
        self.dedup_threshold: float = _as_float(os.getenv("DEDUP_THRESHOLD"), 0.6)

        self.notion_token: str = os.getenv("NOTION_TOKEN", "").strip()
        self.notion_database_id: str = os.getenv("NOTION_DATABASE_ID", "").strip()

    @property
    def gemini_enabled(self) -> bool:
        return bool(self.gemini_api_key)

    @property
    def notion_enabled(self) -> bool:
        return bool(self.notion_token) and bool(self.notion_database_id)


settings = Settings()
