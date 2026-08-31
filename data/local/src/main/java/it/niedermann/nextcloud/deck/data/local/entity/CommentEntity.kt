package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "Comment",
    indices = [
        Index("accountId"),
        Index("remoteId"),
        Index("lastModifiedLocal"),
        Index("accountId", "remoteId", unique = true),
        Index("cardId"),
        Index("parentRemoteId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["localId"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val accountId: Long,
    val remoteId: Long?,
    val status: Int,
    val lastModified: OffsetDateTime?,
    val lastModifiedLocal: OffsetDateTime?,
    val etag: String?,

    val cardId: Long,
    val actorType: String?,
    val actorId: String?,
    val actorDisplayName: String?,
    val message: String,
    val parentRemoteId: Long?,
    val createdAt: OffsetDateTime?,

    val conflictWithId: Long? = null
)
