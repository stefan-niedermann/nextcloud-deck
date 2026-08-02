package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import it.niedermann.nextcloud.deck.domain.model.AttachmentType
import java.time.OffsetDateTime

@Entity(
    tableName = "Attachment",
    indices = [
        Index("accountId"),
        Index("remoteId"),
        Index("lastModifiedLocal"),
        Index("accountId", "remoteId", unique = true),
        Index("cardId")
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
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val accountId: Long,
    val remoteId: Long?,
    val status: Int,
    val lastModified: OffsetDateTime?,
    val lastModifiedLocal: OffsetDateTime?,
    val etag: String?,

    val cardId: Long,
    val type: AttachmentType,
    val data: String?,
    val createdAt: OffsetDateTime?,
    val createdBy: String?,
    val deletedAt: OffsetDateTime?,
    val filesize: Long,
    val mimetype: String?,
    val dirname: String?,
    val basename: String?,
    val extension: String?,
    val filename: String?,
    val localPath: String?,
    val fileId: Long?
)
