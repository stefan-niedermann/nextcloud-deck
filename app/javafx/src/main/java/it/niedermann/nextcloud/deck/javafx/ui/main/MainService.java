package it.niedermann.nextcloud.deck.javafx.ui.main;

import com.dlsc.gemsfx.PopOver;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.soabase.recordbuilder.core.RecordBuilder;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.CopyCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.services.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.features.main.MainStageContextStateBuilder;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.BoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.BoardListFeature;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.ColumnFeature;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.HeaderFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.features.PickStackFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class MainService extends Store<MainService.State, MainService.Action> implements
        HeaderFeature.ViewModel,
        BoardFeature.ViewModel,
        BoardListFeature.ViewModel,
        ColumnFeature.ViewModel {

    private final ThemeService themeService;
    private final ApplicationRouter applicationRouter;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final SetCurrentBoardUseCase setCurrentBoardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;
    private final MoveCardUseCase moveCardUseCase;
    private final CopyCardUseCase copyCardUseCase;

    private final Inflater inflater;
    private final PickStackFeature.Factory pickStackFeatureFactory;

    private final GetBoardUseCase getBoardUseCase;

    private PopOver pickStackPopOver;

    @AssistedInject
    public MainService(
            StoreLogger storeLogger,
            ThemeService themeService,
            ApplicationRouter applicationRouter,
            SetCurrentAccountUseCase setCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase,
            SetCurrentBoardUseCase setCurrentBoardUseCase,
            DeleteCardUseCase deleteCardUseCase,
            MoveCardUseCase moveCardUseCase,
            CopyCardUseCase copyCardUseCase,
            Inflater inflater,
            PickStackFeature.Factory pickStackFeatureFactory,
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
        this.moveCardUseCase = moveCardUseCase;
        this.copyCardUseCase = copyCardUseCase;
        this.inflater = inflater;
        this.pickStackFeatureFactory = pickStackFeatureFactory;

        super(storeLogger, initialState);

        on(Action.Initialize.class, (_, action) -> action.initialState());
        on(Action.SwitchAccountAction.class, (state, action) -> state.withAccountId(Optional.of(action.accountId())).withBoardId(Optional.empty()).withCardId(Optional.empty()).withFilter(FilterInformation.EMPTY));
        on(Action.DisplayBoardAction.class, (state, action) -> state.withBoardId(Optional.of(action.boardId())).withCardId(Optional.empty()).withFilter(FilterInformation.EMPTY));
        on(Action.EditCardAction.class, (state, action) -> state.withCardId(Optional.of(action.cardId())));
        on(Action.EditBoardAction.class, (state, _) -> state);
        on(Action.CloseCardAction.class, (state, _) -> state.withCardId(Optional.empty()));
        on(Action.SetFilterAction.class, (state, action) -> state.withFilter(action.filter()));

        effect(Action.SwitchAccountAction.class, (state, action) -> {
            final var accountIdOpt = state.accountId();
            if (accountIdOpt.isEmpty()) {
                return CompletableFuture.failedFuture(new IllegalStateException());
            }
            final var accountId = accountIdOpt.get();
            return setCurrentAccountUseCase.execute(accountId)
                    .thenComposeAsync(_ -> this.getCurrentBoardUseCase.execute(accountId))
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
            return CompletableFuture.completedFuture(Optional.of(new Action.CloseCardAction()));
        });

        effect(Action.MoveCardAction.class, (state, action) -> moveCardUseCase.execute(action.cardId(), action.column().id(), 0)
                .thenApplyAsync(_ -> Optional.empty()));

        effect(Action.CopyCardAction.class, (state, action) -> copyCardUseCase.execute(action.cardId(), action.column().id(), 0)
                .thenApplyAsync(_ -> Optional.empty()));

        effect(Action.PickStackRequestAction.class, (state, action) -> {
            Platform.runLater(() -> {
                if (pickStackPopOver != null) {
                    pickStackPopOver.hide();
                }

                final var feature = pickStackFeatureFactory.create(action.mode(), column -> {
                    if (pickStackPopOver != null) {
                        pickStackPopOver.hide();
                    }
                    if (action.mode() == PickStackFeature.Mode.MOVE) {
                        dispatch(new Action.MoveCardAction(action.cardId(), column));
                    } else {
                        dispatch(new Action.CopyCardAction(action.cardId(), column));
                    }
                });

                final var bundle = inflater.inflate(feature);

                final var popOver = new PopOver(bundle.view());
                popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
                popOver.setAnimated(false);
                this.pickStackPopOver = popOver;

                popOver.setOnShown(_ -> {
                    if (popOver.getScene() != null) {
                        themeService.bind(popOver.getScene());
                    }
                });
                popOver.setOnHidden(_ -> {
                    if (this.pickStackPopOver == popOver) {
                        this.pickStackPopOver = null;
                    }
                });
                popOver.show(action.anchor());
            });

            return CompletableFuture.completedFuture(Optional.empty());
        });
    }

    @AssistedFactory
    public interface Factory {
        MainService createStageContext(State initialState);
    }

    @Override
    public void onAccountSelected(Account.ID accountId) {
        dispatch(new MainService.Action.SwitchAccountAction(accountId));
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

    @Override
    public Flowable<FilterInformation> getFilter() {
        return Flowable.fromPublisher(getState())
                .map(State::filter)
                .distinctUntilChanged();
    }

    @Override
    public void setFilter(FilterInformation filter) {
        dispatch(new Action.SetFilterAction(filter));
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
        dispatch(new MainService.Action.DisplayBoardAction(boardId));
    }

    @Override
    public void onOpenCard(Card.ID cardId) {
        dispatch(new MainService.Action.EditCardAction(cardId));
    }

    @Override
    public void onOpenCardInNewWindow(Card.ID cardId) {
        applicationRouter.launchEditCardStage(cardId);
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
    public void onMoveCard(Card.ID cardId, Node anchor) {
        dispatch(new Action.PickStackRequestAction(cardId, PickStackFeature.Mode.MOVE, anchor));
    }

    @Override
    public void onCopyCard(Card.ID cardId, Node anchor) {
        dispatch(new Action.PickStackRequestAction(cardId, PickStackFeature.Mode.COPY, anchor));
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
                .filter(Boolean.TRUE::equals).ifPresent(_ -> dispatch(new MainService.Action.DeleteCardAction(cardId)));
    }

    @RecordBuilder
    public record State(
            Optional<Account.ID> accountId,
            Optional<Board.ID> boardId,
            Optional<Card.ID> cardId,
            FilterInformation filter
    ) implements MainStageContextStateBuilder.With {
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

        record PickStackRequestAction(Card.ID cardId, PickStackFeature.Mode mode, Node anchor) implements Action {
        }

        record MoveCardAction(Card.ID cardId, Column column) implements Action {
        }

        record CopyCardAction(Card.ID cardId, Column column) implements Action {
        }

        record SetFilterAction(FilterInformation filter) implements Action {
        }
    }
}
