-- Swipey backend schema. Idempotent: safe to run on every startup.

CREATE TABLE IF NOT EXISTS ideas (
    id              TEXT PRIMARY KEY,
    user_id         TEXT NOT NULL,
    niche           TEXT NOT NULL,
    content_type    TEXT NOT NULL,
    title           TEXT NOT NULL,
    description     TEXT NOT NULL DEFAULT '',
    full_text       TEXT NOT NULL DEFAULT '',
    summary_tokens  TEXT NOT NULL DEFAULT '',
    tags            TEXT NOT NULL DEFAULT '[]',
    status          TEXT NOT NULL DEFAULT 'pending',
    source          TEXT NOT NULL DEFAULT 'seed',
    created_at      TEXT NOT NULL,
    updated_at      TEXT NOT NULL,
    UNIQUE(user_id, title)
);

CREATE INDEX IF NOT EXISTS idx_ideas_feed
    ON ideas (user_id, status, created_at);

CREATE TABLE IF NOT EXISTS saved_ideas (
    user_id     TEXT NOT NULL,
    idea_id     TEXT NOT NULL,
    created_at  TEXT NOT NULL,
    PRIMARY KEY (user_id, idea_id),
    FOREIGN KEY (idea_id) REFERENCES ideas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS swipes (
    id          TEXT PRIMARY KEY,
    user_id     TEXT NOT NULL,
    idea_id     TEXT NOT NULL,
    direction   TEXT NOT NULL,
    created_at  TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS feedback_markers (
    id          TEXT PRIMARY KEY,
    user_id     TEXT NOT NULL,
    marker      TEXT NOT NULL,
    weight      REAL NOT NULL DEFAULT 1.0,
    created_at  TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_markers_user
    ON feedback_markers (user_id, created_at);

CREATE TABLE IF NOT EXISTS profiles (
    user_id       TEXT PRIMARY KEY,
    display_name  TEXT NOT NULL DEFAULT 'Editor',
    niche_id      TEXT,
    goal_id       TEXT,
    onboarded     INTEGER NOT NULL DEFAULT 0,
    settings      TEXT NOT NULL DEFAULT '{}',
    created_at    TEXT NOT NULL,
    updated_at    TEXT NOT NULL
);
