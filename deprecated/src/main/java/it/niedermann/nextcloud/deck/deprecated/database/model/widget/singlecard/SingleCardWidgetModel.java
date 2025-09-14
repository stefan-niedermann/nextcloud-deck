package it.niedermann.nextcloud.deck.database.entity.widget.singlecard;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import it.niedermann.nextcloud.deck.database.entity.Account;
import it.niedermann.nextcloud.deck.database.entity.Board;
import it.niedermann.nextcloud.deck.database.entity.Card;

@Entity(
        indices = {
                @Index(value = "cardId", name = "index_SingleCardWidgetModel_cardId"),
                @Index(value = "accountId", name = "idx_cardWidgetModel_accountId"),
                @Index(value = "boardId", name = "idx_cardWidgetModel_boardId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class SingleCardWidgetModel {
    @Ignore
    private static final long serialVersionUID = 0;

    @PrimaryKey()
    private Integer widgetId;
    private Long accountId;
    private Long boardId;
    private Long cardId;

    public Integer getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(Integer widgetId) {
        this.widgetId = widgetId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    @Override
    public String toString() {
        return "SingleCardWidget{" +
                "widgetId=" + widgetId +
                ", accountId=" + accountId +
                ", boardId=" + boardId +
                ", cardId=" + cardId +
                '}';
    }
}
