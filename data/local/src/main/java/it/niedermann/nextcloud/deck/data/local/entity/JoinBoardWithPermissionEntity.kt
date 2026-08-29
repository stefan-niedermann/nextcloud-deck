package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "JoinBoardWithPermission",
    primaryKeys = ["boardId", "permissionId"],
    indices = [
        Index("boardId"),
        Index("permissionId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["localId"],
            childColumns = ["boardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PermissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["permissionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JoinBoardWithPermissionEntity(
    val boardId: Long,
    val permissionId: Long,
    override val status: Int
) : JoinEntity
