package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Embedded
import androidx.room3.Relation

/**
 * Modern Room implementation of the old `FullDeckComment`.
 */
data class FullComment(
    @Embedded
    val comment: CommentEntity,

    /**
     * Many-to-one relationship: The parent comment if this is a reply.
     */
    @Relation(
        parentColumns = ["parentId"],
        entityColumns = ["localId"]
    )
    val parent: CommentEntity?
)
