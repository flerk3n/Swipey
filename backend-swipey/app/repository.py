"""Data-access layer over SQLite. No business logic beyond persistence.

Each function opens its own short-lived connection via ``get_connection``.
Idea rows are returned as plain dicts; conversion to the API `IdeaModel`
shape happens in the router via ``to_idea_model``.
"""
import json
import sqlite3
import uuid
from datetime import datetime, timezone
from typing import Dict, List, Optional

from .database import get_connection
from .models import IdeaModel

# Maps internal content_type -> the friendly eyebrow label the card shows.
CONTENT_TYPE_LABELS = {
    "tweet": "TWITTER / X",
    "short_form": "SHORT FORM",
    "long_form": "LONG FORM",
    "post": "POST",
    "live_stream": "LIVE STREAM",
    "unboxing": "UNBOXING",
    "review": "REVIEW",
    "grwm": "GRWM",
    "haul": "HAUL",
    "lookbook": "LOOKBOOK",
    "guide": "GUIDE",
}


def _now() -> str:
    return datetime.now(timezone.utc).isoformat()


def _new_id() -> str:
    return uuid.uuid4().hex


def _row_to_dict(row: sqlite3.Row) -> Dict:
    data = dict(row)
    try:
        data["tags"] = json.loads(data.get("tags") or "[]")
    except (json.JSONDecodeError, TypeError):
        data["tags"] = []
    return data


def to_idea_model(row: Dict) -> IdeaModel:
    """Convert a stored idea row into the app-facing IdeaModel shape."""
    content_type = row.get("content_type", "")
    category = CONTENT_TYPE_LABELS.get(content_type, content_type.replace("_", " ").upper())
    return IdeaModel(
        id=row["id"],
        category=category,
        title=row["title"],
        description=row.get("description", ""),
        tags=row.get("tags", []),
    )


# --- Ideas -------------------------------------------------------------------

def insert_idea(
    user_id: str,
    niche: str,
    content_type: str,
    title: str,
    description: str,
    summary_tokens: str,
    tags: List[str],
    full_text: str = "",
    status: str = "pending",
    source: str = "seed",
) -> Optional[Dict]:
    """Insert an idea. Returns the row, or None if it violated UNIQUE(title)."""
    idea_id = _new_id()
    now = _now()
    try:
        with get_connection() as conn:
            conn.execute(
                """
                INSERT INTO ideas (
                    id, user_id, niche, content_type, title, description,
                    full_text, summary_tokens, tags, status, source,
                    created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                (
                    idea_id, user_id, niche, content_type, title, description,
                    full_text, summary_tokens, json.dumps(tags), status, source,
                    now, now,
                ),
            )
    except sqlite3.IntegrityError:
        return None
    return get_idea(idea_id)


def get_idea(idea_id: str) -> Optional[Dict]:
    with get_connection() as conn:
        row = conn.execute("SELECT * FROM ideas WHERE id = ?", (idea_id,)).fetchone()
    return _row_to_dict(row) if row else None


def list_deck(user_id: str, limit: int = 20, niche: Optional[str] = None) -> List[Dict]:
    query = "SELECT * FROM ideas WHERE user_id = ? AND status = 'pending'"
    params: List = [user_id]
    if niche:
        query += " AND niche = ?"
        params.append(niche)
    query += " ORDER BY created_at ASC LIMIT ?"
    params.append(limit)
    with get_connection() as conn:
        rows = conn.execute(query, tuple(params)).fetchall()
    return [_row_to_dict(r) for r in rows]


def next_idea(user_id: str) -> Optional[Dict]:
    deck = list_deck(user_id, limit=1)
    return deck[0] if deck else None


def list_existing_tokens(user_id: str, niche: Optional[str] = None) -> List[str]:
    """All summary_tokens strings for dedup comparison."""
    query = "SELECT summary_tokens FROM ideas WHERE user_id = ?"
    params: List = [user_id]
    if niche:
        query += " AND niche = ?"
        params.append(niche)
    with get_connection() as conn:
        rows = conn.execute(query, tuple(params)).fetchall()
    return [r["summary_tokens"] for r in rows if r["summary_tokens"]]


def set_status(idea_id: str, status: str) -> None:
    with get_connection() as conn:
        conn.execute(
            "UPDATE ideas SET status = ?, updated_at = ? WHERE id = ?",
            (status, _now(), idea_id),
        )


def set_full_text(idea_id: str, full_text: str) -> None:
    with get_connection() as conn:
        conn.execute(
            "UPDATE ideas SET full_text = ?, updated_at = ? WHERE id = ?",
            (full_text, _now(), idea_id),
        )


def set_tags(idea_id: str, tags: List[str]) -> None:
    with get_connection() as conn:
        conn.execute(
            "UPDATE ideas SET tags = ?, updated_at = ? WHERE id = ?",
            (json.dumps(tags), _now(), idea_id),
        )


# --- Saved -------------------------------------------------------------------

def add_saved(user_id: str, idea_id: str) -> None:
    with get_connection() as conn:
        conn.execute(
            """
            INSERT OR IGNORE INTO saved_ideas (user_id, idea_id, created_at)
            VALUES (?, ?, ?)
            """,
            (user_id, idea_id, _now()),
        )


def remove_saved(user_id: str, idea_id: str) -> None:
    with get_connection() as conn:
        conn.execute(
            "DELETE FROM saved_ideas WHERE user_id = ? AND idea_id = ?",
            (user_id, idea_id),
        )


def list_saved(user_id: str) -> List[Dict]:
    with get_connection() as conn:
        rows = conn.execute(
            """
            SELECT i.* FROM ideas i
            JOIN saved_ideas s ON s.idea_id = i.id
            WHERE s.user_id = ?
            ORDER BY s.created_at DESC
            """,
            (user_id,),
        ).fetchall()
    return [_row_to_dict(r) for r in rows]


def is_saved(user_id: str, idea_id: str) -> bool:
    with get_connection() as conn:
        row = conn.execute(
            "SELECT 1 FROM saved_ideas WHERE user_id = ? AND idea_id = ?",
            (user_id, idea_id),
        ).fetchone()
    return row is not None


# --- Swipes ------------------------------------------------------------------

def record_swipe(user_id: str, idea_id: str, direction: str) -> None:
    with get_connection() as conn:
        conn.execute(
            """
            INSERT INTO swipes (id, user_id, idea_id, direction, created_at)
            VALUES (?, ?, ?, ?, ?)
            """,
            (_new_id(), user_id, idea_id, direction, _now()),
        )


# --- Feedback markers --------------------------------------------------------

def add_markers(user_id: str, markers: List[str], weight: float = 1.0) -> None:
    if not markers:
        return
    now = _now()
    with get_connection() as conn:
        conn.executemany(
            """
            INSERT INTO feedback_markers (id, user_id, marker, weight, created_at)
            VALUES (?, ?, ?, ?, ?)
            """,
            [(_new_id(), user_id, m, weight, now) for m in markers],
        )


def list_markers(user_id: str, limit: int = 20) -> List[str]:
    with get_connection() as conn:
        rows = conn.execute(
            """
            SELECT marker FROM feedback_markers
            WHERE user_id = ?
            ORDER BY created_at DESC
            LIMIT ?
            """,
            (user_id, limit),
        ).fetchall()
    return [r["marker"] for r in rows]


def count_ideas() -> int:
    with get_connection() as conn:
        row = conn.execute("SELECT COUNT(*) AS c FROM ideas").fetchone()
    return int(row["c"]) if row else 0


# --- Profile -----------------------------------------------------------------

DEFAULT_SETTINGS = {
    "push_notifications": True,
    "daily_reminder": True,
    "reduced_motion": False,
    "notion_connected": False,
    "calendar_connected": False,
}


def get_profile(user_id: str) -> Dict:
    """Return the user's profile, creating a default row if it doesn't exist."""
    with get_connection() as conn:
        row = conn.execute(
            "SELECT * FROM profiles WHERE user_id = ?", (user_id,)
        ).fetchone()
        if row is None:
            now = _now()
            conn.execute(
                """
                INSERT INTO profiles (user_id, display_name, niche_id, goal_id,
                    onboarded, settings, created_at, updated_at)
                VALUES (?, 'Editor', NULL, NULL, 0, ?, ?, ?)
                """,
                (user_id, json.dumps(DEFAULT_SETTINGS), now, now),
            )
            row = conn.execute(
                "SELECT * FROM profiles WHERE user_id = ?", (user_id,)
            ).fetchone()

    data = dict(row)
    try:
        settings = json.loads(data.get("settings") or "{}")
    except (json.JSONDecodeError, TypeError):
        settings = {}
    # Merge over defaults so newly added keys always have a value.
    merged = dict(DEFAULT_SETTINGS)
    merged.update({k: v for k, v in settings.items() if k in DEFAULT_SETTINGS})
    data["settings"] = merged
    data["onboarded"] = bool(data.get("onboarded", 0))
    return data


def update_profile(
    user_id: str,
    display_name: Optional[str] = None,
    niche_id: Optional[str] = None,
    goal_id: Optional[str] = None,
    onboarded: Optional[bool] = None,
) -> Dict:
    """Patch the supplied profile fields (others untouched). Ensures the row exists."""
    get_profile(user_id)  # ensure row
    sets: List[str] = []
    params: List = []
    if display_name is not None:
        sets.append("display_name = ?")
        params.append(display_name)
    if niche_id is not None:
        sets.append("niche_id = ?")
        params.append(niche_id)
    if goal_id is not None:
        sets.append("goal_id = ?")
        params.append(goal_id)
    if onboarded is not None:
        sets.append("onboarded = ?")
        params.append(1 if onboarded else 0)

    if sets:
        sets.append("updated_at = ?")
        params.append(_now())
        params.append(user_id)
        with get_connection() as conn:
            conn.execute(
                f"UPDATE profiles SET {', '.join(sets)} WHERE user_id = ?",
                tuple(params),
            )
    return get_profile(user_id)


def update_settings(user_id: str, settings: Dict) -> Dict:
    """Replace the settings JSON (validated against known keys). Ensures the row exists."""
    get_profile(user_id)
    clean = dict(DEFAULT_SETTINGS)
    clean.update({k: bool(v) for k, v in settings.items() if k in DEFAULT_SETTINGS})
    with get_connection() as conn:
        conn.execute(
            "UPDATE profiles SET settings = ?, updated_at = ? WHERE user_id = ?",
            (json.dumps(clean), _now(), user_id),
        )
    return get_profile(user_id)
