package com.harsh.swipey.data.model

/** A broad creator niche (onboarding step 1) with its sub-topics (step 2). */
data class Niche(
    val id: String,
    val emoji: String,
    val name: String,
    val topics: List<String>,
)

/** A posting-volume commitment (onboarding step 3). */
data class Goal(
    val id: String,
    val title: String,
    val subtitle: String,
)

/**
 * Static onboarding catalog — the 20 creator niches and their sub-topics from the
 * content taxonomy (masterdoc.md). Ships pre-populated; no networking.
 */
object OnboardingData {

    val goals = listOf(
        Goal("casual", "Casual", "A few ideas each week"),
        Goal("consistent", "Consistent", "One fresh idea every day"),
        Goal("ambitious", "Ambitious", "Three ideas every day"),
        Goal("pro", "Pro", "Ten ideas a day"),
    )

    val niches = listOf(
        Niche("tech", "💻", "Tech & Software", listOf(
            "AI & Machine Learning", "Coding tutorials", "Startups & SaaS", "Cybersecurity",
            "Web development", "Mobile app development", "Data science", "Open source",
            "Gaming hardware", "Smart home & IoT", "Wearables & gadgets", "Product reviews (tech)",
        )),
        Niche("gaming", "🎮", "Gaming", listOf(
            "Let's plays", "Game reviews", "Speedrunning", "Esports & competitive",
            "Mobile gaming", "Retro gaming", "Indie games", "Game development",
            "Gaming setup tours", "Gaming news", "Walkthroughs & guides", "Streaming tips",
        )),
        Niche("fashion", "👗", "Fashion & Style", listOf(
            "Outfit ideas (OOTD)", "Trend reports", "Thrift & secondhand fashion", "Capsule wardrobe",
            "Streetwear", "Luxury fashion", "Sustainable fashion", "Men's fashion",
            "Plus-size fashion", "Seasonal styling", "Accessories & shoes", "Brand reviews",
        )),
        Niche("finance", "💰", "Personal Finance & Money", listOf(
            "Stock market investing", "ETFs & index funds", "Cryptocurrency", "Budgeting & saving",
            "Side hustles", "Passive income", "Debt payoff strategies", "Credit score improvement",
            "Retirement planning", "Tax tips", "FIRE movement", "Real estate investing",
        )),
        Niche("fitness", "🏋️", "Fitness & Wellness", listOf(
            "Weight loss", "Muscle building", "Home workouts", "Gym workouts",
            "Running & cardio", "Yoga & flexibility", "Nutrition & meal planning", "Mental wellness",
            "Sleep optimization", "Supplements & recovery", "Mobility & stretching", "Sports-specific training",
        )),
        Niche("food", "🍳", "Food & Cooking", listOf(
            "Quick weeknight meals", "Baking & pastry", "Vegan & plant-based", "Keto & low-carb",
            "Meal prep & batch cooking", "World cuisines", "Restaurant reviews", "Food photography",
            "Kitchen hacks", "Grocery budgeting", "Food science", "Cocktails & drinks",
        )),
        Niche("travel", "✈️", "Travel", listOf(
            "Budget travel", "Luxury travel", "Solo travel", "Family travel",
            "Van life & nomadic living", "City guides", "Hidden gems & off-beat", "Travel hacks & tips",
            "Travel photography", "Flight deals", "Visa & documentation", "Travel gear reviews",
        )),
        Niche("beauty", "💄", "Beauty & Skincare", listOf(
            "Skincare routines", "Makeup tutorials", "Product reviews", "Anti-aging",
            "Acne & skin concerns", "Natural & clean beauty", "Haircare", "Nail art",
            "Men's grooming", "Fragrance", "Drugstore vs luxury", "Ingredient breakdowns",
        )),
        Niche("education", "📚", "Education & Learning", listOf(
            "Study tips & techniques", "Language learning", "Online courses", "Academic subjects",
            "Career advice", "Productivity systems", "Note-taking methods", "Memory & learning science",
            "College & university tips", "Homeschooling", "Speed reading", "Critical thinking",
        )),
        Niche("business", "📈", "Business & Entrepreneurship", listOf(
            "Starting a business", "Marketing & growth", "E-commerce & dropshipping", "Freelancing",
            "Agency building", "Product-led growth", "Leadership & management", "Sales & negotiation",
            "Branding", "Business finance", "Solopreneur life", "Scaling & operations",
        )),
        Niche("mentalhealth", "🧠", "Mental Health & Self-help", listOf(
            "Anxiety management", "Depression & recovery", "Therapy & counseling", "Mindfulness & meditation",
            "Self-esteem & confidence", "Trauma healing", "Habit building", "Toxic relationship recovery",
            "Grief & loss", "ADHD & neurodivergence", "Burnout & stress", "Journaling",
        )),
        Niche("parenting", "👶", "Parenting & Family", listOf(
            "Newborn & baby care", "Toddler parenting", "Teen parenting", "Single parenting",
            "Co-parenting", "Homeschooling & education", "Family finance", "Work-life balance",
            "Child development", "Discipline strategies", "Gentle parenting", "Screen time management",
        )),
        Niche("sports", "⚽", "Sports", listOf(
            "Football / Soccer", "Basketball", "Cricket", "Tennis",
            "Swimming", "Athletics / Track", "Combat sports", "Cycling",
            "Golf", "Extreme sports", "Fantasy sports", "Sports analysis & stats",
        )),
        Niche("music", "🎵", "Music & Arts", listOf(
            "Music production", "Songwriting", "Instrument tutorials", "Music theory",
            "DJ & mixing", "Painting & illustration", "Graphic design", "Photography",
            "Film & cinematography", "Street art", "Digital art & NFTs", "Art history",
        )),
        Niche("books", "📖", "Books & Literature", listOf(
            "Book reviews", "BookTok / reading vlogs", "Genre deep-dives", "Non-fiction summaries",
            "Classic literature", "Author spotlights", "Reading challenges", "Book club discussions",
            "Writing craft", "Publishing & self-publishing", "Audiobooks", "Poetry",
        )),
        Niche("relationships", "💞", "Relationships & Dating", listOf(
            "Dating advice", "Relationship communication", "Breakup & healing", "Marriage & partnership",
            "Attachment styles", "Online dating tips", "Friendships & social life", "Setting boundaries",
            "Toxic relationship patterns", "Self-love", "Cultural dating differences", "Queer relationships",
        )),
        Niche("comedy", "😂", "Comedy & Entertainment", listOf(
            "Stand-up style sketches", "Commentary & roast culture", "Reaction content", "Pop culture takes",
            "Movie & TV reviews", "Celebrity gossip commentary", "Parody content", "Impressions",
            "Dark humor", "Memes & internet culture", "Nostalgia content", "Award show recaps",
        )),
        Niche("sustainability", "🌱", "Sustainability & Environment", listOf(
            "Zero waste living", "Sustainable fashion", "Climate news", "Vegan lifestyle",
            "Green home & energy", "Ethical consumption", "Eco travel", "Environmental activism",
            "Upcycling & DIY", "Urban gardening", "Corporate greenwashing", "Sustainable finance",
        )),
        Niche("diy", "🛠️", "DIY & Home", listOf(
            "Home renovation", "Interior design", "DIY crafts", "Woodworking",
            "Knitting & sewing", "3D printing", "Candle & soap making", "Room makeovers",
            "Budget decorating", "Gardening & plants", "Home organization", "Thrift flips",
        )),
        Niche("cars", "🚗", "Cars & Automotive", listOf(
            "Car reviews", "Modified builds", "EV & electric vehicles", "Off-roading",
            "Classic & vintage cars", "Motorcycle content", "Car buying tips", "Car maintenance & repair",
            "Supercar content", "Racing & motorsport", "Car tech & features", "Road trip content",
        )),
    )

    fun nicheById(id: String?): Niche? = niches.firstOrNull { it.id == id }
}
