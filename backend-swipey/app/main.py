"""Swipey backend — FastAPI application entry point.

Run from backend-swipey/:  ./run.sh   (or)  uvicorn app.main:app --reload
Swagger UI: http://localhost:8000/docs
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .config import settings
from .database import init_db
from .routers import health, ideas, profile, notion

API_PREFIX = "/api/v1"

app = FastAPI(
    title="Swipey Backend",
    version="1.0.0",
    description="Local-first, Gemini-powered content-idea feed for the Swipey app.",
)

# Allow the Android app (any LAN origin) to call the API during local dev.
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.on_event("startup")
def _startup() -> None:
    init_db()
    # Add Notion columns to the database schema if configured.
    from .services.notion import ensure_columns
    ensure_columns()


@app.get("/", tags=["health"])
def root() -> dict:
    return {
        "name": "Swipey Backend",
        "docs": "/docs",
        "api": API_PREFIX,
        "gemini_enabled": settings.gemini_enabled,
    }


app.include_router(health.router, prefix=API_PREFIX)
app.include_router(ideas.router, prefix=API_PREFIX)
app.include_router(profile.router, prefix=API_PREFIX)
app.include_router(notion.router, prefix=API_PREFIX)
