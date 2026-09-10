package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.AccountListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.AvatarView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class AccountSwitcherFeature extends AbstractFeature {

    private static final Logger logger = Logger.getLogger(AccountSwitcherFeature.class.getName());

    @FXML
    AvatarView bigAvatar;
    @FXML
    Label displayName;
    @FXML
    Label accountName;
    @FXML
    Button syncBtn;
    @FXML
    Button addAccountBtn;
    @FXML
    ListView<Account> accounts;
    @FXML
    Button deleteAccountBtn;

    private final AccountListItemCellFactory listItemCellFactory;
    private final ViewModel viewModel;

    @AssistedInject
    public AccountSwitcherFeature(
            Inflater inflater,
            AccountListItemCellFactory listItemCellFactory,
            @Assisted ViewModel viewModel
    ) {
        super(inflater);

        this.listItemCellFactory = listItemCellFactory;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        AccountSwitcherFeature create(ViewModel viewModel);
    }

    public interface ViewModel {
        Flowable<Account.ID> getAccountId();

        Flowable<Account> getAccount();

        Flowable<Iterable<Account>> getAccounts();

        void onAccountSelected(Account.ID accountId);

        void onScheduleSync();

        void onAddAccount();

        void onDeleteAccount(Account account);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        accounts.setCellFactory(listItemCellFactory);
        accounts.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                viewModel.onAccountSelected(newValue.id());
            }
        });

        final var accountDisposable = viewModel.getAccount()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(account -> {
                    bigAvatar.setAvatar(account);
                    displayName.setText(account.displayName());
                    accountName.setText(account.username());
                    deleteAccountBtn.setText(MessageFormat.format(resources.getString("account.delete"), account.username()));
                    deleteAccountBtn.setOnAction(_ -> viewModel.onDeleteAccount(account));
                });

        final var accountsDisposable = viewModel.getAccounts()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(accounts -> this.accounts.getItems().setAll((Collection<Account>) accounts));

        syncBtn.setOnAction(_ -> viewModel.onScheduleSync());
        addAccountBtn.setOnAction(_ -> viewModel.onAddAccount());

        addDisposable(accountDisposable, accountsDisposable);
    }
}
