package it.niedermann.nextcloud.deck.model.join.card;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinCardWithUser {
    @Id
    private Long id;
    private Long userId;
    private Long cardId;

    @Generated(hash = 709699766)
    public JoinCardWithUser(Long id, Long userId, Long cardId) {
        this.id = id;
        this.userId = userId;
        this.cardId = cardId;
    }

    @Generated(hash = 72483313)
    public JoinCardWithUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
