package it.niedermann.nextcloud.deck.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.shared.model.Board;

@Entity(
        tableName = "Board",
        inheritSuperIndices = true,
        indices = {@Index("ownerId")},
        foreignKeys = {
                @ForeignKey(
                        entity = AccountEntity.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class BoardEntity extends Board {
}
