package com.harsh.swipey.data.model

/**
 * Hardcoded seed deck for the Flashcard screen (Phase 5). ~20 realistic content ideas
 * across platforms and niches. No networking — replaced by generated content later.
 */
object SeedIdeas {

    val deck: List<IdeaModel> = listOf(
        IdeaModel(
            id = "seed_01",
            category = "COPYWRITING",
            title = "How to write hooks that stick",
            description = "The secret to an unbreakable hook isn't just curiosity—it's " +
                "establishing an immediate, visceral stake. If the reader doesn't feel a " +
                "sense of loss by scrolling away, you haven't earned the next line.",
            tags = listOf("#Strategy", "#Writing", "#Conversion"),
        ),
        IdeaModel(
            id = "seed_02",
            category = "YOUTUBE",
            title = "5 thumbnail styles that get clicks",
            description = "Break down the psychology behind high-CTR thumbnails: contrast, " +
                "a single focal face, and a 3-word promise. Show 5 before/after redesigns.",
            tags = listOf("#Thumbnails", "#Growth", "#CTR"),
        ),
        IdeaModel(
            id = "seed_03",
            category = "TWITTER / X",
            title = "Why your CTA is killing reach",
            description = "A thread on how external links and hard CTAs tank distribution. " +
                "Offer 4 native-first alternatives that keep people on-platform.",
            tags = listOf("#Thread", "#Reach", "#HotTake"),
        ),
        IdeaModel(
            id = "seed_04",
            category = "INSTAGRAM",
            title = "The 3-slide carousel formula",
            description = "Slide 1 promises, slide 2 pays off, slide 3 asks for the save. " +
                "Show why most carousels lose people on slide 2 and how to fix it.",
            tags = listOf("#Carousel", "#Saves", "#Design"),
        ),
        IdeaModel(
            id = "seed_05",
            category = "TIKTOK",
            title = "Turn one long video into 7 clips",
            description = "A repurposing workflow: find the spikes in retention, cut around " +
                "them, and reframe each clip with a new opening line.",
            tags = listOf("#Repurpose", "#Shorts", "#Workflow"),
        ),
        IdeaModel(
            id = "seed_06",
            category = "NEWSLETTER",
            title = "Subject lines people actually open",
            description = "Specificity beats cleverness. Compare 10 vague vs. concrete " +
                "subject lines and the open-rate lift you'd expect from each.",
            tags = listOf("#Email", "#OpenRate", "#Copy"),
        ),
        IdeaModel(
            id = "seed_07",
            category = "PODCAST",
            title = "The cold open that hooks listeners",
            description = "Open mid-story, not with an intro. Script a 20-second cold open " +
                "that drops the listener into the most surprising moment of the episode.",
            tags = listOf("#Audio", "#Retention", "#Storytelling"),
        ),
        IdeaModel(
            id = "seed_08",
            category = "LINKEDIN",
            title = "A career story worth reposting",
            description = "Frame a single hard lesson as a short narrative with a clear " +
                "turning point. End with the one principle you'd tell your younger self.",
            tags = listOf("#Storytelling", "#Career", "#Engagement"),
        ),
        IdeaModel(
            id = "seed_09",
            category = "EDUCATION",
            title = "Explain it like they're new",
            description = "Take a jargon-heavy concept in your niche and rebuild it with one " +
                "everyday analogy. Bonus: show the analogy breaking, then refine it.",
            tags = listOf("#Explainer", "#Teaching", "#Clarity"),
        ),
        IdeaModel(
            id = "seed_10",
            category = "PERSONAL FINANCE",
            title = "The 'boring' budget that works",
            description = "Walk through a percentage-based budget with real numbers. Show " +
                "why automation beats willpower for sticking to it.",
            tags = listOf("#Budgeting", "#Money", "#Habits"),
        ),
        IdeaModel(
            id = "seed_11",
            category = "FITNESS",
            title = "A 20-minute home workout series",
            description = "Design a 5-part beginner series with no equipment. Each video " +
                "ends with the exact next session so viewers binge the playlist.",
            tags = listOf("#HomeWorkout", "#Series", "#Beginners"),
        ),
        IdeaModel(
            id = "seed_12",
            category = "FOOD",
            title = "One pan, five weeknight dinners",
            description = "Batch-film five quick dinners that share a base technique. Lean " +
                "into the satisfying sizzle b-roll for the first three seconds.",
            tags = listOf("#MealPrep", "#Recipes", "#Quick"),
        ),
        IdeaModel(
            id = "seed_13",
            category = "TECH",
            title = "Ship a side project in a weekend",
            description = "Document a 48-hour build from idea to deploy. Show the cuts you " +
                "made to scope and why shipping beats polishing.",
            tags = listOf("#BuildInPublic", "#SideProject", "#Indie"),
        ),
        IdeaModel(
            id = "seed_14",
            category = "DESIGN",
            title = "Before & after: ugly to clean UI",
            description = "Redesign one cluttered screen live. Narrate each decision around " +
                "spacing, hierarchy, and contrast so beginners can copy the moves.",
            tags = listOf("#UIDesign", "#Redesign", "#Process"),
        ),
        IdeaModel(
            id = "seed_15",
            category = "BUSINESS",
            title = "Pricing that doesn't scare buyers",
            description = "Anchor high, then frame the mid-tier as the obvious choice. Show " +
                "three pricing-page layouts and which one converts.",
            tags = listOf("#Pricing", "#SaaS", "#Conversion"),
        ),
        IdeaModel(
            id = "seed_16",
            category = "MENTAL HEALTH",
            title = "A 2-minute reset for busy days",
            description = "Teach a simple box-breathing routine and the science behind why " +
                "it calms the nervous system. Keep it shame-free and practical.",
            tags = listOf("#Mindfulness", "#Wellness", "#Routine"),
        ),
        IdeaModel(
            id = "seed_17",
            category = "TRAVEL",
            title = "A city in 48 hours, on a budget",
            description = "Build a tight itinerary around free landmarks and one splurge " +
                "meal. Show the map route so it's instantly saveable.",
            tags = listOf("#CityGuide", "#Budget", "#Itinerary"),
        ),
        IdeaModel(
            id = "seed_18",
            category = "GAMING",
            title = "The mechanic everyone gets wrong",
            description = "Pick one misunderstood game system and prove the optimal play " +
                "with side-by-side clips. End with a quick drill viewers can try.",
            tags = listOf("#Guide", "#Meta", "#Tips"),
        ),
        IdeaModel(
            id = "seed_19",
            category = "BEAUTY",
            title = "Drugstore dupes that actually hold up",
            description = "Test three budget products against premium picks over a week. " +
                "Rate them on the one metric that matters for each category.",
            tags = listOf("#Dupes", "#Review", "#Budget"),
        ),
        IdeaModel(
            id = "seed_20",
            category = "DIY & HOME",
            title = "A weekend room refresh under $50",
            description = "Three reversible changes—paint, light, and layout—that transform " +
                "a room. Film the reveal as a single satisfying pan.",
            tags = listOf("#RoomMakeover", "#Budget", "#DIY"),
        ),
    )
}
