# Swipey — PM Deliverables Master Doc
> Product Manager brain dump. Everything needed before a single screen is coded.

---

## Decision Log

### Auth: Email + Google OAuth ✅

Use **Supabase Auth** for both. One SDK, zero extra backend.

**How Email/Password works:**
```kotlin
// Sign up
supabase.auth.signUpWith(Email) {
    email = "user@email.com"
    password = "password123"
}

// Sign in
supabase.auth.signInWith(Email) {
    email = "user@email.com"
    password = "password123"
}
```

**How Google OAuth works on Android (two-step):**

Step 1 — Google Cloud Console:
- Create project → Enable "Google Sign-In" API
- Create OAuth 2.0 credentials → Android client ID
- Add your app's SHA-1 fingerprint
- Note the `Web client ID` (not Android — Supabase needs the web one)

Step 2 — Supabase Dashboard:
- Auth → Providers → Google → Paste Web client ID + Secret

Step 3 — In Android app:
```kotlin
// Use Google Identity Services (One Tap — most native feel)
// build.gradle
implementation("com.google.android.gms:play-services-auth:21.x.x")

// Get Google ID Token, then pass to Supabase:
supabase.auth.signInWith(IDToken) {
    idToken = googleIdToken   // from Google One Tap result
    provider = Google
}
```

Supabase then creates/links the user automatically. One `users` record regardless of how they signed in.

**Auth screens needed:**
- Splash (auto-login check)
- Welcome (not logged in)
- Sign Up (email/password)
- Sign In (email/password)
- Google One Tap sheet (triggered from both above)
- Forgot Password (email link)
- Email Verification prompt


**Verdict:** Supabase for everything. Add a simple local cache layer later if offline is needed (v2 problem).

**Supabase Android SDK:**
```kotlin
// build.gradle.kts
implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.x")
implementation("io.github.jan-tennert.supabase:auth-kt:2.5.x")
implementation("io.github.jan-tennert.supabase:realtime-kt:2.5.x")
implementation("io.ktor:ktor-client-android:2.3.x")

// Initialize once in Application class:
val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Realtime)
}
```

---

## Full Content Taxonomy

> This is your entire seed dataset. Goes into the DB as static tables. Ship with it pre-populated.

---

### Creator Platforms (9 total)

| ID | Platform | Icon slug | Active in v1? |
|---|---|---|---|
| 1 | YouTube | `youtube` | ✅ |
| 2 | Podcast | `podcast` | ✅ |
| 3 | Instagram | `instagram` | ✅ |
| 4 | TikTok | `tiktok` | ✅ |
| 5 | Twitter / X | `twitter` | ✅ |
| 6 | LinkedIn | `linkedin` | ✅ |
| 7 | Newsletter / Substack | `newsletter` | ✅ |
| 8 | Threads | `threads` | ✅ |
| 9 | Pinterest | `pinterest` | ✅ |

---

### Content Formats per Platform

> These are the idea *types* a creator can select. Stored in `content_formats` table with a `platform_id` FK.

**YouTube (18 formats)**
```
1.  Long-form video (10–20 min)
2.  Short-form / YouTube Shorts (< 60s)
3.  Tutorial / How-to
4.  Vlog
5.  Documentary-style
6.  Reaction video
7.  Review / Unboxing
8.  Challenge video
9.  Q&A / FAQ
10. Day in the life
11. Storytime
12. Educational explainer
13. Top 10 / List video
14. Commentary / Opinion
15. Interview
16. Behind the scenes
17. Case study deep dive
18. Compilation / Best-of
```

**Podcast (14 formats)**
```
1.  Solo monologue
2.  Guest interview (1 guest)
3.  Panel discussion (3+ people)
4.  Co-hosted banter (2 hosts, no guest)
5.  Storytelling / Narrative episode
6.  Educational / Informational
7.  True crime / Investigation
8.  Comedy
9.  News commentary
10. Q&A episode (listener questions)
11. Debate format
12. Case study breakdown
13. Mini-episode (5–10 min)
14. Weekly recap / Digest
```

**Instagram (13 formats)**
```
1.  Single photo post
2.  Carousel (2–10 slides)
3.  Reel (short video)
4.  Story (photo/video, 24hr)
5.  Story poll
6.  Story Q&A sticker
7.  Story quiz
8.  Collab post (with another creator)
9.  Quote / text graphic
10. Product / brand showcase
11. Tutorial reel
12. Trending audio reel
13. GRWM / Day in the life reel
```

**TikTok (16 formats)**
```
1.  Duet
2.  Stitch
3.  Trend / challenge
4.  POV video
5.  Tutorial (60s)
6.  Storytime
7.  Day in the life
8.  Reaction video
9.  Comedy sketch
10. Educational explainer
11. Product review / haul
12. GRWM
13. Cooking / recipe
14. Life hack
15. Workout / fitness
16. BookTok / review
```

**Twitter / X (10 formats)**
```
1.  Single tweet (text)
2.  Thread (multi-tweet)
3.  Quote tweet with commentary
4.  Poll
5.  Twitter Space (audio)
6.  Long-form article (X Articles)
7.  Hot take / opinion
8.  Educational thread
9.  Storytelling thread
10. Meme / humor post
```

**LinkedIn (10 formats)**
```
1.  Short text post (< 300 chars)
2.  Long text post (storytelling)
3.  Document / carousel PDF
4.  Video post
5.  Poll
6.  Newsletter edition
7.  Thought leadership article
8.  Case study
9.  Industry news commentary
10. Personal career story
```

**Newsletter / Substack (10 formats)**
```
1.  Weekly roundup / digest
2.  Deep-dive essay
3.  Tutorial / how-to
4.  Opinion piece
5.  Interview (written)
6.  Listicle
7.  Case study
8.  Resource roundup
9.  Personal story / letter
10. Template or framework share
```

**Threads (6 formats)**
```
1.  Single thought (short)
2.  Opinion / hot take
3.  Day in the life
4.  Behind the scenes
5.  Question to followers
6.  Thread (multi-post)
```

**Pinterest (7 formats)**
```
1.  Single pin (image)
2.  Idea pin (multi-frame story)
3.  Video pin
4.  Tutorial / step-by-step pin
5.  Infographic pin
6.  Product / shopping pin
7.  Recipe pin
```

---

### Creator Niches (20 niches, each with sub-topics)

> Stored in `niches` table. Sub-topics in `niche_subtopics` table with `niche_id` FK.

---

**1. Tech & Software**
```
AI & Machine Learning       Coding tutorials
Startups & SaaS             Cybersecurity
Web development             Mobile app development
Data science                Open source
Gaming hardware             Smart home & IoT
Wearables & gadgets         Product reviews (tech)
```

**2. Personal Finance & Money**
```
Stock market investing       ETFs & index funds
Cryptocurrency              Budgeting & saving
Side hustles                Passive income
Debt payoff strategies      Credit score improvement
Retirement planning         Tax tips
FIRE movement               Real estate investing
```

**3. Fitness & Wellness**
```
Weight loss                 Muscle building
Home workouts               Gym workouts
Running & cardio            Yoga & flexibility
Nutrition & meal planning   Mental wellness
Sleep optimization          Supplements & recovery
Mobility & stretching       Sports-specific training
```

**4. Food & Cooking**
```
Quick weeknight meals       Baking & pastry
Vegan & plant-based         Keto & low-carb
Meal prep & batch cooking   World cuisines
Restaurant reviews          Food photography
Kitchen hacks               Grocery budgeting
Food science                Cocktails & drinks
```

**5. Fashion & Style**
```
Outfit ideas (OOTD)         Trend reports
Thrift & secondhand fashion Capsule wardrobe
Streetwear                  Luxury fashion
Sustainable fashion         Men's fashion
Plus-size fashion           Seasonal styling
Accessories & shoes         Brand reviews
```

**6. Travel**
```
Budget travel               Luxury travel
Solo travel                 Family travel
Van life & nomadic living   City guides
Hidden gems & off-beat      Travel hacks & tips
Travel photography          Flight deals
Visa & documentation        Travel gear reviews
```

**7. Gaming**
```
Let's plays                 Game reviews
Speedrunning                Esports & competitive
Mobile gaming               Retro gaming
Indie games                 Game development
Gaming setup tours          Gaming news
Walkthroughs & guides       Streaming tips (Twitch/YT)
```

**8. Beauty & Skincare**
```
Skincare routines           Makeup tutorials
Product reviews             Anti-aging
Acne & skin concerns        Natural & clean beauty
Haircare                    Nail art
Men's grooming              Fragrance
Drugstore vs luxury         Ingredient breakdowns
```

**9. Education & Learning**
```
Study tips & techniques     Language learning
Online courses & platforms  Academic subjects (math, science)
Career advice               Productivity systems
Note-taking methods         Memory & learning science
College & university tips   Homeschooling
Speed reading               Critical thinking
```

**10. Business & Entrepreneurship**
```
Starting a business         Marketing & growth
E-commerce & dropshipping   Freelancing
Agency building             Product-led growth
Leadership & management     Sales & negotiation
Branding                    Business finance
Solopreneur life            Scaling & operations
```

**11. Mental Health & Self-help**
```
Anxiety management          Depression & recovery
Therapy & counseling        Mindfulness & meditation
Self-esteem & confidence    Trauma healing
Habit building              Toxic relationship recovery
Grief & loss                ADHD & neurodivergence
Burnout & stress            Journaling
```

**12. Parenting & Family**
```
Newborn & baby care         Toddler parenting
Teen parenting              Single parenting
Co-parenting                Homeschooling & education
Family finance              Work-life balance
Child development           Discipline strategies
Montessori & gentle parenting Screen time management
```

**13. Sports**
```
Football / Soccer           Basketball
Cricket                     Tennis
Swimming                    Athletics / Track
Combat sports (MMA, boxing) Cycling
Golf                        Extreme sports
Fantasy sports              Sports analysis & stats
```

**14. Music & Arts**
```
Music production            Songwriting
Instrument tutorials        Music theory
DJ & mixing                 Painting & illustration
Graphic design              Photography
Film & cinematography       Street art
Digital art & NFTs          Art history
```

**15. Books & Literature**
```
Book reviews                BookTok / reading vlogs
Genre deep-dives (fantasy, sci-fi, etc.) Non-fiction summaries
Classic literature          Author spotlights
Reading challenges          Book club discussions
Writing craft               Publishing & self-publishing
Audiobooks                  Poetry
```

**16. Relationships & Dating**
```
Dating advice               Relationship communication
Breakup & healing           Marriage & long-term partnership
Attachment styles           Online dating tips
Friendships & social life   Setting boundaries
Toxic relationship patterns Self-love
Cultural dating differences Queer relationships
```

**17. Comedy & Entertainment**
```
Stand-up style sketches     Commentary & roast culture
Reaction content            Pop culture takes
Movie & TV reviews          Celebrity gossip commentary
Parody content              Impressions
Dark humor                  Memes & internet culture
Nostalgia content           Award show recaps
```

**18. Sustainability & Environment**
```
Zero waste living           Sustainable fashion
Climate news                Vegan lifestyle
Green home & energy         Ethical consumption
Eco travel                  Environmental activism
Upcycling & DIY             Urban gardening
Corporate greenwashing      Sustainable finance
```

**19. DIY & Home**
```
Home renovation             Interior design
DIY crafts                  Woodworking
Knitting & sewing           3D printing
Candle & soap making        Room makeovers
Budget decorating           Gardening & plants
Home organization           Thrift flips
```

**20. Cars & Automotive**
```
Car reviews                 Modified builds & custom cars
EV & electric vehicles      Off-roading
Classic & vintage cars      Motorcycle content
Car buying tips             Car maintenance & repair
Supercar content            Racing & motorsport
Car tech & features         Road trip content
```

---

