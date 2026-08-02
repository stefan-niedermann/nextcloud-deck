package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "JoinCardWithUser",
    primaryKeys = ["cardId", "userId"],
    indices = [
        Index("cardId"),
        Index("userId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["localId"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["localId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JoinCardWithUserEntity(
    val cardId: Long,
    val userId: Long,
    val status: Int
)
