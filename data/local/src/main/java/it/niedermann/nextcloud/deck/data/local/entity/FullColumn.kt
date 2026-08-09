package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Embedded
import androidx.room3.Relation

/**
 * Modern Room implementation of the old `FullStack`.
 * 
 * Renamed to `FullColumn` to match the new naming convention.
 */
data class FullColumn(
    @Embedded
    val column: ColumnEntity,

    /**
     * One-to-many relationship: A column has multiple cards.
     */
    @Relation(
        parentColumns = ["localId"],
        entityColumns = ["columnId"]
    )
    val cards: List<CardEntity>
)
