package it.niedermann.nextcloud.deck.javafx.services.stage;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardListFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.ColumnFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.HeaderFeature;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class MainStageContext extends Store<MainStageContext.State, MainStageContext.Action> implements
        HeaderFeature.ViewModel,
        BoardFeature.ViewModel,
        BoardListFeature.ViewModel,
        ColumnFeature.ViewModel {

    private static final Logger logger = Logger.getLogger(MainStageContext.class.getName());

    private final ThemeService themeService;
    private final ApplicationRouter applicationRouter;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final SetCurrentBoardUseCase setCurrentBoardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;

    private final GetBoardUseCase getBoardUseCase;

    @AssistedInject
    public MainStageContext(
            StoreLogger storeLogger,
            ThemeService themeService,
            ApplicationRouter applicationRouter,
            SetCurrentAccountUseCase setCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase,
            SetCurrentBoardUseCase setCurrentBoardUseCase,
            DeleteCardUseCase deleteCardUseCase,
            GetBoardUseCase getBoardUseCase,
            @Assisted State initialState
    ) {
        this.themeService = themeService;
        this.applicationRouter = applicationRouter;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.setCurrentBoardUseCase = setCurrentBoardUseCase;
        this.getBoardUseCase = getBoardUseCase;
        this.deleteCardUseCase = deleteCardUseCase;

        super(storeLogger, initialState);

        on(Action.Initialize.class, (_, action) -> action.initialState());
        on(Action.SwitchAccountAction.class, (state, action) -> state.withAccountId(action.accountId()));
        on(Action.DisplayBoardAction.class, (state, action) -> state.withBoardId(action.boardId()));
        on(Action.EditCardAction.class, (state, action) -> state.withCardId(action.cardId()));
        on(Action.EditBoardAction.class, (state, _) -> state);
        on(Action.CloseCardAction.class, (state, _) -> state.withCardId(null));

        effect(Action.SwitchAccountAction.class, (state, action) -> {
            final var accountId = state.accountId();
            if (accountId.isEmpty()) {
                return CompletableFuture.failedFuture(new IllegalStateException());
            }
            return setCurrentAccountUseCase.execute(accountId.get())
                    .thenComposeAsync(_ -> this.getCurrentBoardUseCase.execute(accountId.get()))
                    .thenApplyAsync(Optional::ofNullable)
                    .exceptionallyAsync(_ -> Optional.empty())
                    .thenApplyAsync(boardId -> boardId.map(Action.DisplayBoardAction::new));
        });

        effect(Action.DisplayBoardAction.class, (state, action) -> {
            final var accountId = state.accountId();
            final var boardId = state.boardId();
            if (accountId.isEmpty() || boardId.isEmpty()) {
                return CompletableFuture.failedFuture(new IllegalStateException());
            }
            return setCurrentBoardUseCase.execute(accountId.get(), boardId.get())
                    .thenApplyAsync(_ -> Optional.empty());
        });

        effect(Action.DeleteCardAction.class, (state, action) -> deleteCardUseCase.execute(action.cardId())
                .thenComposeAsync(_ -> {
                    if (Objects.equals(action.cardId(), state.cardId().orElse(null))) {
                        return CompletableFuture.completedFuture(Optional.of(new Action.CloseCardAction()));
                    } else {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                }));

        effect(Action.EditBoardAction.class, (state, action) -> {
            state.accountId().ifPresent(accountId -> applicationRouter.launchEditBoardStage(accountId, action.board().id()));
            return CompletableFuture.completedFuture(Optional.empty());
        });
    }

    @AssistedFactory
    public interface Factory {
        MainStageContext createStageContext(State initialState);
    }

    @Override
    public void onAccountSelected(Account.ID accountId) {
        dispatch(new MainStageContext.Action.SwitchAccountAction(accountId));
    }

    @Override
    public Flowable<Account.ID> getAccountId() {
        return Flowable.fromPublisher(getState())
                .map(State::accountId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinctUntilChanged(Account.ID::equals);
    }

    @Override
    public Flowable<Board.ID> getBoardId() {
        return Flowable.fromPublisher(getState())
                .map(State::boardId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinctUntilChanged(Board.ID::equals);
    }

    public Flowable<Board> getBoard() {
        return Flowable.fromPublisher(getBoardId())
                .switchMap(getBoardUseCase::execute)
                .distinctUntilChanged(Board::equals);
    }

    @Override
    public void onEditBoard(Board board) {
        dispatch(new Action.EditBoardAction(board));
    }

    @Override
    public void onLaunchPreferences() {
        applicationRouter.launchPreferencesStage();
    }

    @Override
    public void onAccountRemoved() {
        // TODO Select any account and set as current OR fallback to login scene
    }

    @Override
    public void onBoardSelected(Board.ID boardId) {
        System.out.println("onBoardSelected: " + boardId);
        dispatch(new MainStageContext.Action.DisplayBoardAction(boardId));
    }

    @Override
    public void onOpenCard(Card.ID cardId) {
        dispatch(new MainStageContext.Action.EditCardAction(cardId));
    }

    @Override
    public void onAssignCard(Card.ID cardId) {
        System.out.println("[Mock] onAssignCard " + cardId);
    }

    @Override
    public void onUnassignCard(Card.ID cardId) {
        System.out.println("[Mock] onUnassignCard " + cardId);
    }

    @Override
    public void onMoveCard(Card.ID cardId) {
        System.out.println("[Mock] onMoveCard " + cardId);
    }

    @Override
    public void onCopyCard(Card.ID cardId) {
        System.out.println("[Mock] onCopyCard " + cardId);
    }

    @Override
    public void onDeleteCard(Card.ID cardId) {
        final var alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete the card permanently? This operation can not be undone.", ButtonType.CANCEL, ButtonType.YES);
        alert.setTitle("Delete");
        alert.setHeaderText("Delete card?");
        themeService.bind(alert);
        alert.showAndWait()
                .map(ButtonType::getButtonData)
                .map(ButtonBar.ButtonData::isDefaultButton)
                .filter(Boolean.TRUE::equals).ifPresent(_ -> dispatch(new MainStageContext.Action.DeleteCardAction(cardId)));
    }

    public record State(
            Optional<Account.ID> accountId,
            Optional<Board.ID> boardId,
            Optional<Card.ID> cardId
    ) {

        public State withAccountId(Account.ID id) {
            if (Objects.equals(accountId().orElse(null), id)) {
                return this;
            }

            return new State(Optional.ofNullable(id), Optional.empty(), Optional.empty());
        }

        public State withBoardId(Board.ID boardId) {
            if (Objects.equals(boardId().orElse(null), boardId)) {
                return this;
            }

            return new State(accountId(), Optional.ofNullable(boardId), Optional.empty());
        }

        public State withCardId(Card.ID cardId) {
            return new State(accountId(), boardId(), Optional.ofNullable(cardId));
        }
    }

    public sealed interface Action {

        record Initialize(State initialState) implements Action {
        }

        record SwitchAccountAction(Account.ID accountId) implements Action {
        }

        record DisplayBoardAction(Board.ID boardId) implements Action {
        }

        record EditCardAction(Card.ID cardId) implements Action {
        }

        record CloseCardAction() implements Action {
        }

        record DeleteCardAction(Card.ID cardId) implements Action {
        }

        record EditBoardAction(Board board) implements Action {
        }
    }
}
