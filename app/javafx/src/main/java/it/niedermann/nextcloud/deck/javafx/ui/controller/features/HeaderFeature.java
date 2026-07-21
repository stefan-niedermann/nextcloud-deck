package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import com.dlsc.gemsfx.PopOver;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AvatarProgressView;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.PopupWindow;

public class HeaderFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(HeaderFeature.class.getName());

    @FXML
    Circle circle;
    @FXML
    Label boardTitle;
    @FXML
    Button scheduleSyncBtn;
    @FXML
    AvatarProgressView avatar;
    @FXML
    Button removeAccountBtn;

    private final Inflater inflater;
    private final GetAccountUseCase getAccountUseCase;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final RemoveAccountUseCase removeAccountUseCase;
    private final AccountSwitcherFeature.Factory accountSwitcherFactory;
    private final ViewModel viewModel;

    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);

    @AssistedInject
    public HeaderFeature(
            Inflater inflater,
            GetAccountUseCase getAccountUseCase,
            ScheduleSyncUseCase scheduleSyncUseCase,
            RemoveAccountUseCase removeAccountUseCase,
            AccountSwitcherFeature.Factory accountSwitcherFactory,
            @Assisted ViewModel viewModel
    ) {
        this.inflater = inflater;
        this.getAccountUseCase = getAccountUseCase;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.removeAccountUseCase = removeAccountUseCase;
        this.accountSwitcherFactory = accountSwitcherFactory;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        HeaderFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var currentAccount = viewModel.getAccountId()
                .observeOn(Schedulers.virtual())
                .switchMap(getAccountUseCase::execute)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(avatar::setAvatar);

        addDisposable(currentAccount);

        final var currentBoardDisposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    boardTitle.setText(board.title());
                    boardTitle.setTooltip(new Tooltip(String.format("Last edited at %1$s by %2$s",
                            board.editedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                            "John Doe")));
                    circle.setFill(Color.rgb(board.color().getRed(), board.color().getGreen(), board.color().getBlue()));
                });

        addDisposable(currentBoardDisposable);

        scheduleSyncBtn.setOnAction(_ -> this.scheduleSync());
        removeAccountBtn.setOnAction(_ -> this.removeAccount());

        avatar.setOnMouseClicked(event -> {
            final var accountSwitcher = inflater.inflate(accountSwitcherFactory.create());
            final var popover = new PopOver(accountSwitcher.view());
            popover.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
            popover.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
            popover.show(avatar);
            event.consume();
        });
    }

    public void scheduleSync() {
        if (!this.syncInProgress.getAndSet(true)) {

            var disposable = viewModel.getAccountId()
                    .firstElement()
                    .flatMapPublisher(this.scheduleSyncUseCase::execute)
                    .observeOn(JavaFxScheduler.platform())
                    .doOnNext(avatar::setSyncStatus)
                    .onErrorComplete()
                    .ignoreElements()
                    .subscribe(() -> this.syncInProgress.set(false));

            addDisposable(disposable);
        }
    }

    public void removeAccount() {
        var disposable = viewModel.getAccountId()
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(accountId -> {
                    this.removeAccountUseCase.execute(accountId);
                    this.viewModel.onAccountRemoved();
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
