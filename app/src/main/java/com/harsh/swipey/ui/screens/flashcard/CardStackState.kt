package com.harsh.swipey.ui.screens.flashcard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.harsh.swipey.data.model.IdeaModel

/**
 * Plain state holder (NOT a ViewModel — that arrives in Phase 5) backing the swipe deck.
 * Holds the queue as a [androidx.compose.runtime.snapshots.SnapshotStateList] so Compose
 * recomposes as cards are dismissed.
 */
class CardStackState(initial: List<IdeaModel>) {

    private val _cards = mutableStateListOf<IdeaModel>().apply { addAll(initial) }

    /** Remaining cards, front-to-back. The first item is the top (interactive) card. */
    val cards: List<IdeaModel> get() = _cards

    val topCard: IdeaModel? get() = _cards.firstOrNull()

    val isEmpty: Boolean get() = _cards.isEmpty()

    /** The most recently dismissed card and how it was swiped (for undo / side effects). */
    var lastSwiped: Pair<IdeaModel, SwipeDirection>? by mutableStateOf(null)
        private set

    fun onSwipeLeft() = dismissTop(SwipeDirection.Left)

    fun onSwipeRight() = dismissTop(SwipeDirection.Right)

    fun onSwipeUp() = dismissTop(SwipeDirection.Up)

    /** Routes a [SwipeDirection] reported by a gesture to the matching dismissal. */
    fun dismiss(direction: SwipeDirection) = dismissTop(direction)

    private fun dismissTop(direction: SwipeDirection) {
        val removed = _cards.removeFirstOrNull() ?: return
        lastSwiped = removed to direction
    }

    /** Appends a card to the back of the queue (used to keep an endless feed full). */
    fun append(idea: IdeaModel) {
        _cards.add(idea)
    }

    /** Resets the deck to a new list of cards. */
    fun reset(cards: List<IdeaModel>) {
        _cards.clear()
        _cards.addAll(cards)
        lastSwiped = null
    }
}

/** Remembers a [CardStackState] across recompositions. */
@Composable
fun rememberCardStackState(initial: List<IdeaModel>): CardStackState =
    remember { CardStackState(initial) }
