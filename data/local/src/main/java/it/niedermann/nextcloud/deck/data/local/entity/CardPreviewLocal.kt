package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation

data class CardPreviewLocal(
    @Embedded
    val card: CardEntity,

    val commentCount: Int,

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

    @Relation(
        associateBy = Junction(
            value = JoinCardWithUserEntity::class,
            parentColumns = ["cardId"],
            entityColumns = ["userId"]
        ),
        parentColumns = ["localId"],
        entityColumns = ["localId"]
    )
    val assignees: List<UserEntity>
)
