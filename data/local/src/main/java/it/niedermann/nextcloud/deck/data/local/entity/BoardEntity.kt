package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import it.niedermann.nextcloud.deck.domain.model.Color
import java.time.OffsetDateTime

@Entity(
    tableName = "Board",
    indices = [
        Index("accountId"),
        Index("remoteId"),
        Index("lastModifiedLocal"),
        Index("accountId", "remoteId", unique = true),
        Index("ownerId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["localId"],
            childColumns = ["ownerId"],
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
data class BoardEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    val accountId: Long,
    val remoteId: Long?,
    val status: Int,
    val lastModified: OffsetDateTime?,
    val lastModifiedLocal: OffsetDateTime?,
    val etag: String?,

    val title: String,
    val ownerId: Long?, // Typed ID allows null in constructor, keeping Long for Room
    val color: Color?,
    val archived: Boolean,
    val shared: Int,
    val deletedAt: OffsetDateTime?,
    val permissionRead: Boolean,
    val permissionEdit: Boolean,
    val permissionManage: Boolean,
    val permissionShare: Boolean,

    val conflictWithId: Long? = null
)
