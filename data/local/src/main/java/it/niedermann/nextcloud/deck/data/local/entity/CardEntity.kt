package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "Card",
    indices = [
        Index("accountId"),
        Index("remoteId"),
        Index("lastModifiedLocal"),
        Index("accountId", "remoteId", unique = true),
        Index("columnId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = ColumnEntity::class,
            parentColumns = ["localId"],
            childColumns = ["columnId"],
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
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val accountId: Long,
    val remoteId: Long?,
    val status: Int,
    val lastModified: OffsetDateTime?,
    val lastModifiedLocal: OffsetDateTime?,
    val etag: String?,

    val title: String,
    val description: String?,
    val columnId: Long,
    val type: String?,
    val createdAt: OffsetDateTime?,
    val deletedAt: OffsetDateTime?,
    val done: OffsetDateTime?,
    val attachmentCount: Int,
    val userId: Long?,
    val order: Int,
    val archived: Boolean,
    val dueDate: OffsetDateTime?,
    val notified: Boolean,
    val overdue: Int,
    val commentsUnread: Int,

    val conflictWithId: Long? = null
)
