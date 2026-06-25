package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.AccountListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AccountListItemView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Callback;
import javafx.util.Pair;

public class AccountSwitcherFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(AccountSwitcherFeature.class.getName());

    @FXML
    ComboBox<Account> accountList;
    @FXML
    Button scheduleSyncBtn;
    @FXML
    ProgressIndicator progress;
    @FXML
    Button removeAccountBtn;

    private final StageContext stageContext;
    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountsUseCase getAccountsUseCase;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final RemoveAccountUseCase removeAccountUseCase;

    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);

    private final Callback<ListView<Account>, ListCell<Account>> factory = new AccountListItemCellFactory();

    @Inject
    public AccountSwitcherFeature(
            StageContext stageContext,
            GetAccountUseCase getAccountUseCase,
            GetAccountsUseCase getAccountsUseCase,
            ScheduleSyncUseCase scheduleSyncUseCase,
            RemoveAccountUseCase removeAccountUseCase
    ) {
        this.stageContext = stageContext;
        this.getAccountUseCase = getAccountUseCase;
        this.getAccountsUseCase = getAccountsUseCase;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.removeAccountUseCase = removeAccountUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var buttonCell = new ListCell<Account>() {

            final AccountListItemView view = new AccountListItemView();

            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                setText(null);

                if (empty || account == null) {
                    setGraphic(null);
                    return;
                }

                final boolean isCurrent = account.equals(accountList.getValue());

                view.bind(account, isCurrent);
                setGraphic(view);
            }
        };

        accountList.setCellFactory(factory);
        accountList.setButtonCell(buttonCell);

        final ChangeListener<Account> accountChangeListener = (_, _, newValue) -> stageContext.dispatch(new StageContext.SwitchAccountAction(newValue.id()));

        final var listAccounts = Flowable.fromPublisher(getAccountsUseCase.execute());

        final var currentAccount = Flowable.fromPublisher(this.stageContext.getState())
                .map(StageContext.State::accountId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .switchMap(getAccountUseCase::execute);

        final var disposable = Flowable.combineLatest(listAccounts, currentAccount, Pair::new)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(args -> {
                    accountList.getSelectionModel().selectedItemProperty().removeListener(accountChangeListener);
                    accountList.getItems().setAll(args.getKey());
                    accountList.getSelectionModel().select(args.getValue());
                    accountList.getSelectionModel().selectedItemProperty().addListener(accountChangeListener);
                });

        addDisposable(disposable);

        scheduleSyncBtn.setOnAction(_ -> this.scheduleSync());
        removeAccountBtn.setOnAction(_ -> this.removeAccount());
    }

    public void scheduleSync() {
        if (!this.syncInProgress.getAndSet(true)) {

            this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            this.progress.setVisible(true);
            this.progress.setDisable(false);

            var disposable = Flowable.fromPublisher(stageContext.getState())
                    .firstElement()
                    .map(StageContext.State::accountId)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
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
        var disposable = Flowable.fromPublisher(stageContext.getState())
                .firstElement()
                .map(StageContext.State::accountId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(this.removeAccountUseCase::execute);

        addDisposable(disposable);
    }
}
