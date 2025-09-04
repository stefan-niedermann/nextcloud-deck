package it.niedermann.nextcloud.deck.database.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.shared.model.Account;


@Entity(
        tableName = "Account",
        inheritSuperIndices = true,
        indices = {
                @Index("cardId"),
                @Index("eTag"),
                @Index("currentBoard")
        },
        foreignKeys = {
                @ForeignKey(entity = Board.class, parentColumns = "currentBoardId", childColumns = "id", onDelete = ForeignKey.SET_NULL),
        }

)
public class AccountEntity extends Account {
}
