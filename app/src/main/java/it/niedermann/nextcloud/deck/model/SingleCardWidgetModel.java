package it.niedermann.nextcloud.deck.model;

import it.niedermann.nextcloud.deck.model.full.FullCard;

public class SingleCardWidgetModel {

    private Long id;
    private Account account;
    private Long boardLocalId;
    private FullCard fullCard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getBoardLocalId() {
        return boardLocalId;
    }

    public void setBoardLocalId(Long boardLocalId) {
        this.boardLocalId = boardLocalId;
    }

    public FullCard getFullCard() {
        return fullCard;
    }

    public void setCardLocalId(FullCard cardLocalId) {
        this.fullCard = cardLocalId;
    }

}
