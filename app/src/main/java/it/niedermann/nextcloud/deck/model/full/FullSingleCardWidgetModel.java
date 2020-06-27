package it.niedermann.nextcloud.deck.model.full;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.widget.singlecard.SingleCardWidgetModel;

public class FullSingleCardWidgetModel {

    @Embedded
    private SingleCardWidgetModel model;

    @Relation(parentColumn = "accountId", entityColumn = "id")
    private Account account;

    @Ignore
    private FullCard fullCard;

    public SingleCardWidgetModel getModel() {
        return model;
    }

    public void setModel(SingleCardWidgetModel model) {
        this.model = model;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public FullCard getFullCard() {
        return fullCard;
    }

    public void setFullCard(FullCard fullCard) {
        this.fullCard = fullCard;
    }
}
