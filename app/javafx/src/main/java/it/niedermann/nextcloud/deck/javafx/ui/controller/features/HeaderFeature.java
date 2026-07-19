package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.AccountListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AccountListItemView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.Pair;

public class HeaderFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(HeaderFeature.class.getName());

    @FXML
    Circle circle;
    @FXML
    Label boardTitle;
    @FXML
    ComboBox<Account> accountList;
    @FXML
    Button scheduleSyncBtn;
    @FXML
    ProgressIndicator progress;
    @FXML
    Button removeAccountBtn;

    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountsUseCase getAccountsUseCase;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final RemoveAccountUseCase removeAccountUseCase;
    private final ViewModel viewModel;

    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);

    private final Callback<ListView<Account>, ListCell<Account>> factory = new AccountListItemCellFactory();

    @AssistedInject
    public HeaderFeature(
            GetAccountUseCase getAccountUseCase,
            GetAccountsUseCase getAccountsUseCase,
            ScheduleSyncUseCase scheduleSyncUseCase,
            RemoveAccountUseCase removeAccountUseCase,
            @Assisted ViewModel viewModel
    ) {
        this.getAccountUseCase = getAccountUseCase;
        this.getAccountsUseCase = getAccountsUseCase;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.removeAccountUseCase = removeAccountUseCase;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        HeaderFeature create(ViewModel viewModel);
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

        final ChangeListener<Account> accountChangeListener = (_, _, newValue) -> viewModel.onAccountSelected(newValue.id());

        final var listAccounts = Flowable.fromPublisher(getAccountsUseCase.execute());
        final var currentAccount = viewModel.getAccountId().switchMap(getAccountUseCase::execute);

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

        final var currentBoardDisposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    boardTitle.setText(board.title());
                    circle.setFill(Color.rgb(board.color().getRed(), board.color().getGreen(), board.color().getBlue()));
                });

        addDisposable(currentBoardDisposable);

        scheduleSyncBtn.setOnAction(_ -> this.scheduleSync());
        removeAccountBtn.setOnAction(_ -> this.removeAccount());
    }

    public void scheduleSync() {
        if (!this.syncInProgress.getAndSet(true)) {

            this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            this.progress.setVisible(true);
            this.progress.setDisable(false);

            var disposable = viewModel.getAccountId()
                    .firstElement()
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
        var disposable = viewModel.getAccountId()
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(accountId -> {
                    // FIXME fix pipe
                    this.removeAccountUseCase.execute(accountId);
                    viewModel.onAccountRemoved();
                });

        addDisposable(disposable);
    }

    public interface ViewModel {
        void onAccountSelected(Account.ID accountId);

        Flowable<Account.ID> getAccountId();

        Flowable<Board> getBoard();

        void onAccountRemoved();
    }
}
