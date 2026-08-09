package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "AccessControl",
    indices = [
        Index("accountId"),
        Index("remoteId"),
        Index("lastModifiedLocal"),
        Index("accountId", "remoteId", unique = true),
        Index("boardId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["localId"],
            childColumns = ["boardId"],
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
data class AccessControlEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val accountId: Long,
    val remoteId: Long?,
    val status: Int,
    val lastModified: OffsetDateTime?,
    val lastModifiedLocal: OffsetDateTime?,
    val etag: String?,

    val type: Long?,
    val boardId: Long,
    val owner: Boolean,
    val permissionEdit: Boolean,
    val permissionShare: Boolean,
    val permissionManage: Boolean,
    val userId: Long?,

    val conflictWithId: Long? = null
)
