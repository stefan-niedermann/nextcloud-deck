package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation

/**
 * Modern Room implementation of the old `FullCard`.
 */
data class FullCard(
    @Embedded
    val card: CardEntity,

    /**
     * Many-to-many relationship: Labels assigned to this card.
     */
    @Relation(
        associateBy = Junction(
            value = JoinCardWithLabelEntity::class,
            parentColumns = ["cardId"],
            entityColumns = ["labelId"]
        ),
        parentColumns = ["localId"],
        entityColumns = ["localId"]
    )
    val labels: List<LabelEntity>,

    /**
     * Many-to-many relationship: Users assigned to this card.
     */
    @Relation(
        associateBy = Junction(
            value = JoinCardWithUserEntity::class,
            parentColumns = ["cardId"],
            entityColumns = ["userId"]
        ),
        parentColumns = ["localId"],
        entityColumns = ["localId"]
    )
    val assignedUsers: List<UserEntity>,

    /**
     * Many-to-one relationship: The user who owns/created the card.
     */
    @Relation(
        parentColumns = ["userId"],
        entityColumns = ["localId"]
    )
    val owner: UserEntity?,

    /**
     * One-to-many relationship: Attachments belonging to this card.
     */
    @Relation(
        parentColumns = ["localId"],
        entityColumns = ["cardId"]
    )
    val attachments: List<AttachmentEntity>,

    /**
     * One-to-many relationship: Comments belonging to this card.
     */
    @Relation(
        parentColumns = ["localId"],
        entityColumns = ["cardId"]
    )
    val comments: List<CommentEntity>
)
