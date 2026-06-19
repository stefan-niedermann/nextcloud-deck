package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Single;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Callback;

public class AccountSwitcherFeature extends DisposableController {

    @FXML
    ComboBox<AccountItem> comboBox;
    @FXML
    Button scheduleSyncBtn;
    @FXML
    ProgressIndicator progress;
    @FXML
    Button removeAccountBtn;

    private final GetAccountsUseCase getAccountsUseCase;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final RemoveAccountUseCase removeAccountUseCase;

    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);

    private final Callback<ListView<AccountItem>, ListCell<AccountItem>> factory = _ -> new ListCell<>() {
        @Override
        protected void updateItem(AccountItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                return;
            }

            setText((item.isCurrentAccount() ? "☒ " : "☐ ") + item.account().username());
        }
    };

    @Inject
    public AccountSwitcherFeature(
            GetAccountsUseCase getAccountsUseCase,
            SetCurrentAccountUseCase setCurrentAccountUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            ScheduleSyncUseCase scheduleSyncUseCase,
            RemoveAccountUseCase removeAccountUseCase
    ) {
        this.getAccountsUseCase = getAccountsUseCase;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.removeAccountUseCase = removeAccountUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var accountsPublisher = getAccountsUseCase.execute();
        final var currentAccountPublisher = getCurrentAccountUseCase.execute("AccountSwitcherController#initialize");

        final var disposableAccountList = Flowable.combineLatest(
                        Flowable.fromPublisher(accountsPublisher),
                        Flowable.fromPublisher(currentAccountPublisher),
                        AccountsAndCurrentAccount::new)
                .map(accountsAndCurrentAccount -> accountsAndCurrentAccount.accounts().stream().map(account -> new AccountItem(account, Objects.equals(account.id(), accountsAndCurrentAccount.currentAccount().id()))).toList())
                .subscribe(comboBox.getItems()::setAll);

        addDisposable(disposableAccountList);

        final var disposableCurrentAccount = Flowable.fromPublisher(currentAccountPublisher)
                .map(currentAccount -> new AccountItem(currentAccount, true))
                .subscribe(comboBox::setValue);

        addDisposable(disposableCurrentAccount);

        comboBox.setButtonCell(factory.call(null));
        comboBox.setCellFactory(factory);
        comboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
                setCurrentAccountUseCase.execute(newValue.account().id()));

        scheduleSyncBtn.setOnAction(_ -> this.scheduleSync());
        removeAccountBtn.setOnAction(_ -> this.removeAccount());
    }

    public void scheduleSync() {
        if (!this.syncInProgress.getAndSet(true)) {

            this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            this.progress.setVisible(true);
            this.progress.setDisable(false);

            var disposable = Flowable.fromPublisher(this.getCurrentAccountUseCase.execute("AccountSwitcherController#scheduleSync"))
                    .firstElement()
                    .map(Account::id)
                    .flatMapPublisher(this.scheduleSyncUseCase::execute)
                    .observeOn(JavaFxScheduler.platform())
                    .doOnNext(syncStatus -> {
                        if (syncStatus.boardsFinishedCount() > 0) {
                            this.progress.setProgress(Math.min(1, (double) syncStatus.boardsFinishedCount() / syncStatus.boardsTotalCount()));
                        }
                    })
                    .onErrorComplete()
                    .ignoreElements()
                    .subscribe(() -> {
                        this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                        this.progress.setVisible(false);
                        this.progress.setDisable(true);
                        this.syncInProgress.set(false);
                    });

            addDisposable(disposable);
        }
    }

    public void removeAccount() {
        var disposable = Single.fromPublisher(this.getCurrentAccountUseCase.execute("AccountSwitcherController#removeAccount"))
                .map(Account::id)
                .subscribe(this.removeAccountUseCase::execute);

        addDisposable(disposable);
    }

    record AccountItem(Account account, boolean isCurrentAccount) {
    }

    record AccountsAndCurrentAccount(Collection<Account> accounts,
                                     Account currentAccount) {
    }
}
