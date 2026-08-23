package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import it.niedermann.nextcloud.deck.domain.model.JoinEntity

@Entity(
    tableName = "JoinCardWithDependentCard",
    primaryKeys = ["cardId", "dependentRemoteId"],
    indices = [
        Index("cardId"),
        Index("dependentRemoteId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["localId"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JoinCardWithDependentCardEntity(
    val cardId: Long,
    val dependentRemoteId: Long,
    override val status: Int
) : JoinEntity
