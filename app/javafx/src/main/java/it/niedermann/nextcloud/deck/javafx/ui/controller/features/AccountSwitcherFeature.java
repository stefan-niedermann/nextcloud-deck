package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.AccountListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class AccountSwitcherFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(AccountSwitcherFeature.class.getName());

    @FXML
    ListView<Account> accounts;

    private final AccountListItemCellFactory listItemCellFactory;
    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountsUseCase getAccountsUseCase;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final RemoveAccountUseCase removeAccountUseCase;

    @AssistedInject
    public AccountSwitcherFeature(
            AccountListItemCellFactory listItemCellFactory,
            GetAccountUseCase getAccountUseCase,
            GetAccountsUseCase getAccountsUseCase,
            ScheduleSyncUseCase scheduleSyncUseCase,
            RemoveAccountUseCase removeAccountUseCase
    ) {
        this.listItemCellFactory = listItemCellFactory;
        this.getAccountUseCase = getAccountUseCase;
        this.getAccountsUseCase = getAccountsUseCase;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.removeAccountUseCase = removeAccountUseCase;
    }

    @AssistedFactory
    public interface Factory {
        AccountSwitcherFeature create();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        accounts.setCellFactory(listItemCellFactory);

        final var disposable = Flowable.fromPublisher(getAccountsUseCase.execute())
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(accounts -> this.accounts.getItems().setAll(accounts));

        addDisposable(disposable);
    }
}
