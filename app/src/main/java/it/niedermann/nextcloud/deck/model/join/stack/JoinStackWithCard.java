package it.niedermann.nextcloud.deck.model.join.stack;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinStackWithCard {
    @Id
    private Long id;
    private Long stackId;
    private Long cardId;

    @Generated(hash = 1901103993)
    public JoinStackWithCard(Long id, Long stackId, Long cardId) {
        this.id = id;
        this.stackId = stackId;
        this.cardId = cardId;
    }

    @Generated(hash = 792660904)
    public JoinStackWithCard() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStackId() {
        return stackId;
    }

    public void setStackId(Long stackId) {
        this.stackId = stackId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
