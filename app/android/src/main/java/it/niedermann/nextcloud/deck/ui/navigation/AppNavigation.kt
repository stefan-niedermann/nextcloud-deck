package it.niedermann.nextcloud.deck.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object BoardListRoute

@Serializable
data class BoardViewRoute(val boardId: Long)

@Serializable
data class CardDetailsRoute(val cardId: Long)
