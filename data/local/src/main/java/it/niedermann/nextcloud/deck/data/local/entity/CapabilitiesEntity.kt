package it.niedermann.nextcloud.deck.data.local.entity

import it.niedermann.nextcloud.deck.domain.model.Color
import it.niedermann.nextcloud.deck.domain.model.Version

data class CapabilitiesEntity(
    val serverVersion: Version?,
    val themingColor: Color?,
    val commentsEnabled: Boolean,
    val activityEnabled: Boolean
)
