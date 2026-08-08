package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation

/**
 * Modern Room implementation of the old `FullBoard`.
 * 
 * In the new structure, we use a Kotlin `data class` with [Relation] annotations.
 * This is the preferred way in Room to fetch a complex object with all its dependencies
 * in a single transaction.
 */
data class FullBoard(
    @Embedded
    val board: BoardEntity,

    /**
     * One-to-many relationship: A board has multiple columns (formerly stacks).
     */
    @Relation(
        parentColumns = ["localId"],
        entityColumns = ["boardId"]
    )
    val columns: List<ColumnEntity>,

    /**
     * One-to-many relationship: A board has multiple labels.
     */
    @Relation(
        parentColumns = ["localId"],
        entityColumns = ["boardId"]
    )
    val labels: List<LabelEntity>,

    /**
     * Many-to-one relationship: A board has one owner.
     */
    @Relation(
        parentColumns = ["ownerId"],
        entityColumns = ["localId"]
    )
    val owner: UserEntity?,

    /**
     * One-to-many relationship: A board has multiple participants (AccessControl).
     */
    @Relation(
        parentColumns = ["localId"],
        entityColumns = ["boardId"]
    )
    val participants: List<AccessControlEntity>,

    /**
     * Many-to-many relationship: Users associated with the board.
     * Unlike the old implementation where this was @Ignore, Room can now handle
     * this automatically using a [Junction] via the [JoinBoardWithUserEntity] table.
     */
    @Relation(
        associateBy = Junction(
            value = JoinBoardWithUserEntity::class,
            parentColumns = ["boardId"],
            entityColumns = ["userId"]
        ),
        parentColumns = ["localId"],
        entityColumns = ["localId"]
    )
    val users: List<UserEntity>
)
