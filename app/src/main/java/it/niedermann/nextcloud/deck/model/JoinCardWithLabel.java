package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinCardWithLabel {
    @Id
    private Long id;
    private Long labelId;
    private Long cardId;

    @Generated(hash = 1565938244)
    public JoinCardWithLabel(Long id, Long labelId, Long cardId) {
        this.id = id;
        this.labelId = labelId;
        this.cardId = cardId;
    }

    @Generated(hash = 1821090814)
    public JoinCardWithLabel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
