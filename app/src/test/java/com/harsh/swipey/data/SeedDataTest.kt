package com.harsh.swipey.data

import com.harsh.swipey.data.model.OnboardingData
import com.harsh.swipey.data.model.SeedIdeas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedDataTest {

    @Test
    fun `seed deck has 20 ideas`() {
        assertEquals(20, SeedIdeas.deck.size)
    }

    @Test
    fun `seed idea ids are unique`() {
        val ids = SeedIdeas.deck.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `every seed idea has title, description and tags`() {
        SeedIdeas.deck.forEach { idea ->
            assertTrue("blank title for ${idea.id}", idea.title.isNotBlank())
            assertTrue("blank description for ${idea.id}", idea.description.isNotBlank())
            assertTrue("no tags for ${idea.id}", idea.tags.isNotEmpty())
        }
    }

    @Test
    fun `onboarding has 20 niches`() {
        assertEquals(20, OnboardingData.niches.size)
    }

    @Test
    fun `niche ids are unique and each has topics`() {
        val ids = OnboardingData.niches.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        OnboardingData.niches.forEach { niche ->
            assertTrue("no topics for ${niche.id}", niche.topics.isNotEmpty())
        }
    }

    @Test
    fun `nicheById resolves known and unknown ids`() {
        assertEquals("tech", OnboardingData.nicheById("tech")?.id)
        assertEquals(null, OnboardingData.nicheById("does-not-exist"))
        assertEquals(null, OnboardingData.nicheById(null))
    }

    @Test
    fun `goals are present`() {
        assertTrue(OnboardingData.goals.isNotEmpty())
    }
}
