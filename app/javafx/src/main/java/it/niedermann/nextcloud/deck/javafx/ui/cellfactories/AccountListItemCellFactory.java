package it.niedermann.nextcloud.deck.javafx.ui.cellfactories;

import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AccountListItemView;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class AccountListItemCellFactory implements Callback<ListView<Account>, ListCell<Account>> {

    @Override
    public ListCell<Account> call(ListView<Account> listView) {
        return new ListCell<>() {

            final AccountListItemView view = new AccountListItemView();

            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                setText(null);

                if (empty || account == null) {
                    setGraphic(null);
                    return;
                }

                final boolean isCurrent = Optional.ofNullable(listView.getSelectionModel().getSelectedItem())
                        .map(Account::id)
                        .map(accountId -> Objects.equals(accountId, account.id()))
                        .orElse(false);

                view.bind(account, isCurrent);
                setGraphic(view);
            }
        };
    }
}
