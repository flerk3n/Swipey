package com.harsh.swipey.data.model

/**
 * A single content idea shown on a swipe card / saved list.
 * Pure data — no networking. Seeded with hardcoded values during the build phases.
 */
data class IdeaModel(
    val id: String,
    /** Short category eyebrow, e.g. "COPYWRITING". Rendered in violet accent. */
    val category: String,
    /** Headline shown in the serif display style. */
    val title: String,
    /** Supporting body copy. */
    val description: String,
    /** Hashtag-style tags, e.g. ["#Strategy", "#Writing"]. */
    val tags: List<String> = emptyList(),
)

/** Hardcoded sample ideas for previews and early phases. */
object SampleIdeas {
    val hooks = IdeaModel(
        id = "idea_hooks",
        category = "COPYWRITING",
        title = "How to write hooks that stick",
        description = "The secret to an unbreakable hook isn't just curiosity—it's " +
            "establishing an immediate, visceral stake. If the reader doesn't feel a " +
            "sense of loss by scrolling away, you haven't earned the next line.",
        tags = listOf("#Strategy", "#Writing", "#Conversion"),
    )

    val sketch = IdeaModel(
        id = "idea_sketch",
        category = "YOUTUBE",
        title = "Sketch on Modi and Meloni",
        description = "A satirical short imagining a behind-the-scenes diplomatic dinner. " +
            "Lean into the absurd small-talk and the awkward translator beats.",
        tags = listOf("#Comedy", "#Shorts", "#Political"),
    )

    val aiTweet = IdeaModel(
        id = "idea_ai_tweet",
        category = "TWITTER / X",
        title = "Tweet on self-iterating AI models",
        description = "Frame the thread around one provocative claim: the first model that " +
            "improves its own training loop ends the release-cycle era overnight.",
        tags = listOf("#AI", "#Thread", "#HotTake"),
    )

    val all = listOf(hooks, sketch, aiTweet)
}
