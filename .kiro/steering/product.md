# Product — Swipey

## What it is
Swipey is a content-idea discovery app for creators. Users swipe through a stack of
AI-style "idea cards" (Tinder-style gestures), save the ones they like, expand a saved
idea into fully generated content, tag it, and track their activity on a dashboard.

The interaction model is the product: a swipeable card stack is the core loop. Everything
else (onboarding, dashboard, profile, settings) is supporting UI around that loop.

## Core loop
1. Onboarding picks the creator's niche, posting frequency, and goal.
2. The Flashcard screen serves idea cards from a queue.
   - Swipe left / ✕ → dismiss
   - Swipe right / ♡ → like (opens Expanded Echo)
   - Swipe up / ↑ → save (shows a toast)
3. Expanded Echo turns a liked/saved idea into generated content with a typewriter reveal
   and lets the user tag it.
4. Dashboard shows saved ideas, a goal card, stats, and a streak.
5. Profile + Settings manage account info and integrations (Notion, Google Calendar).

## Screens (technical names)
| Screen | Purpose |
| :-- | :-- |
| `WelcomeSplash` | First-run brand entry + onboarding start |
| `NicheSelection` | Step 1 — broad category (list) |
| `TopicSelection` | Step 2 — sub-niche tags (chip cloud) |
| `GoalSetting` | Step 3 — frequency & volume commitment |
| `FlashcardScreen` | Core swipe loop |
| `ExpandedEchoScreen` | Generate + tag a single idea |
| `Dashboard` | Saved ideas, goals, stats, streak |
| `Profile` | Account overview |
| `Settings` | Integration toggles (Notion, Google Calendar) |

## Content domain
The seed dataset (ships static, pre-populated — see `masterdoc.md` for the full taxonomy):
- **9 creator platforms** (YouTube, Podcast, Instagram, TikTok, Twitter/X, LinkedIn,
  Newsletter/Substack, Threads, Pinterest).
- **Content formats per platform** (e.g. YouTube has 18, TikTok 16, Podcast 14).
- **20 creator niches**, each with ~12 sub-topics (Tech, Personal Finance, Fitness,
  Food, Fashion, Travel, Gaming, Beauty, Education, Business, Mental Health, Parenting,
  Sports, Music & Arts, Books, Relationships, Comedy, Sustainability, DIY & Home, Cars).

## Build philosophy (read this before adding scope)
- Build **depth-first**: Theme → Atoms → Swipe Core → one screen fully complete → next.
  Never start a new screen until the current one has working state, real navigation, and
  edge cases handled. See `build-roadmap.md`.
- **MVP is Phases 4–5** (onboarding + flashcard). Shippable after Phase 6 (Expanded Echo).
- **No networking during the build phases.** Auth and Supabase (documented in
  `masterdoc.md`) are a later milestone. Use hardcoded / in-memory seed data until then.
