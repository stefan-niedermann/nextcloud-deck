package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "JoinBoardWithLabel",
    primaryKeys = ["boardId", "labelId"],
    indices = [
        Index("boardId"),
        Index("labelId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["localId"],
            childColumns = ["boardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LabelEntity::class,
            parentColumns = ["localId"],
            childColumns = ["labelId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JoinBoardWithLabelEntity(
    val boardId: Long,
    val labelId: Long,
    val status: Int
)
