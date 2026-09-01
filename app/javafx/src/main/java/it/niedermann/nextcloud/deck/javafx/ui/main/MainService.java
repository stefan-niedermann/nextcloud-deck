package it.niedermann.nextcloud.deck.javafx.ui.main;

import com.dlsc.gemsfx.PopOver;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Maybe;
import io.soabase.recordbuilder.core.RecordBuilder;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AssignCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.CopyCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UnassignCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.services.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.BoardGanttFeature;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.BoardKanbanFeature;
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
        BoardKanbanFeature.ViewModel,
        BoardGanttFeature.ViewModel,
        BoardListFeature.ViewModel,
        ColumnFeature.ViewModel {

    private static final Logger logger = Logger.getLogger(MainService.class.getName());

    private final ThemeService themeService;
    private final ApplicationRouter applicationRouter;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final SetCurrentBoardUseCase setCurrentBoardUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final DeleteCardUseCase deleteCardUseCase;
    private final MoveCardUseCase moveCardUseCase;
    private final CopyCardUseCase copyCardUseCase;
    private final AssignCardUseCase assignCardUseCase;
    private final UnassignCardUseCase unassignCardUseCase;

    private final Inflater inflater;
    private final PickStackFeature.Factory pickStackFeatureFactory;

    private final GetBoardUseCase getBoardUseCase;
    private final AddBoardUseCase addBoardUseCase;

    private PopOver pickStackPopOver;

    @AssistedInject
    public MainService(
            StoreLogger storeLogger,
            ThemeService themeService,
            ApplicationRouter applicationRouter,
            SetCurrentAccountUseCase setCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase,
            SetCurrentBoardUseCase setCurrentBoardUseCase,
            GetAccountUseCase getAccountUseCase,
            DeleteCardUseCase deleteCardUseCase,
            MoveCardUseCase moveCardUseCase,
            CopyCardUseCase copyCardUseCase,
            AssignCardUseCase assignCardUseCase,
            UnassignCardUseCase unassignCardUseCase,
            Inflater inflater,
            PickStackFeature.Factory pickStackFeatureFactory,
            GetBoardUseCase getBoardUseCase,
            AddBoardUseCase addBoardUseCase,
            @Assisted State initialState
    ) {
        this.themeService = themeService;
        this.applicationRouter = applicationRouter;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.setCurrentBoardUseCase = setCurrentBoardUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.getBoardUseCase = getBoardUseCase;
        this.addBoardUseCase = addBoardUseCase;
        this.deleteCardUseCase = deleteCardUseCase;
        this.moveCardUseCase = moveCardUseCase;
        this.copyCardUseCase = copyCardUseCase;
        this.assignCardUseCase = assignCardUseCase;
        this.unassignCardUseCase = unassignCardUseCase;
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
        on(Action.AddBoardAction.class, (state, _) -> state);
        on(Action.SwitchViewMode.class, (state, action) -> state.withViewMode(action.mode()));
        on(Action.ToggleHeaderVariantAction.class, (state, _) -> state.withHeaderVariant(state.headerVariant() == HeaderVariant.DIRECT_BUTTONS ? HeaderVariant.MENU_BAR : HeaderVariant.DIRECT_BUTTONS));

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

                final var popOver = new PopOver(feature.getRoot());
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

        effect(Action.AddBoardAction.class, (state, action) -> {
            final var accountId = state.accountId();
            if (accountId.isEmpty()) {
                return CompletableFuture.failedFuture(new IllegalStateException());
            }
            return addBoardUseCase.addBoard(new CreateBoard(accountId.get(), action.title()))
                    .thenApplyAsync(Action.DisplayBoardAction::new)
                    .thenApply(Optional::of);
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
    public Flowable<Optional<Card.ID>> getCardId() {
        return Flowable.fromPublisher(getState())
                .map(State::cardId)
                .distinctUntilChanged();
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
    public void onLaunchPreferences(Account.ID accountId) {
        if (accountId != null) {
            applicationRouter.launchPreferencesStage(accountId);
        } else {
            applicationRouter.launchPreferencesStage();
        }
    }

    @Override
    public void onAccountRemoved() {
        // TODO Select any account and set as current OR fallback to login scene
    }

    @Override
    public Flowable<ViewMode> getViewMode() {
        return Flowable.fromPublisher(getState())
                .map(State::viewMode)
                .distinctUntilChanged();
    }

    @Override
    public void onViewModeSelected(ViewMode viewMode) {
        dispatch(new Action.SwitchViewMode(viewMode));
    }

    @Override
    public Flowable<HeaderVariant> getHeaderVariant() {
        return Flowable.fromPublisher(getState())
                .map(State::headerVariant)
                .distinctUntilChanged();
    }

    @Override
    public void onToggleHeaderVariant() {
        dispatch(new Action.ToggleHeaderVariantAction());
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
        Flowable.fromPublisher(getState())
                .firstElement()
                .flatMap(state -> Maybe.fromOptional(state.accountId()))
                .flatMap(accountId -> Flowable.fromPublisher(getAccountUseCase.execute(accountId)).firstElement())
                .map(account -> new User.ID(account.username()))
                .subscribe(userId -> assignCardUseCase.execute(cardId, userId)
                        .exceptionally(throwable -> {
                            logger.log(Level.SEVERE, "Failed to assign card", throwable);
                            return null;
                        }));
    }

    @Override
    public void onUnassignCard(Card.ID cardId) {
        Flowable.fromPublisher(getState())
                .firstElement()
                .flatMap(state -> Maybe.fromOptional(state.accountId()))
                .flatMap(accountId -> Flowable.fromPublisher(getAccountUseCase.execute(accountId)).firstElement())
                .map(account -> new User.ID(account.username()))
                .subscribe(userId -> unassignCardUseCase.execute(cardId, userId)
                        .exceptionally(throwable -> {
                            logger.log(Level.SEVERE, "Failed to unassign card", throwable);
                            return null;
                        }));
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
        final var resources = java.util.ResourceBundle.getBundle("i18n");
        final var alert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("main.alert.delete.content"), ButtonType.CANCEL, ButtonType.YES);
        alert.setTitle(resources.getString("main.alert.delete.title"));
        alert.setHeaderText(resources.getString("main.alert.delete.header"));
        themeService.bind(alert);
        alert.showAndWait()
                .map(ButtonType::getButtonData)
                .map(ButtonBar.ButtonData::isDefaultButton)
                .filter(Boolean.TRUE::equals).ifPresent(_ -> dispatch(new MainService.Action.DeleteCardAction(cardId)));
    }

    public enum ViewMode {
        KANBAN,
        GANTT
    }

    public enum HeaderVariant {
        MENU_BAR,
        DIRECT_BUTTONS,
    }

    @RecordBuilder
    public record State(
            Optional<Account.ID> accountId,
            Optional<Board.ID> boardId,
            Optional<Card.ID> cardId,
            FilterInformation filter,
            ViewMode viewMode,
            HeaderVariant headerVariant
    ) implements MainServiceStateBuilder.With {
    }

    public sealed interface Action {

        record Initialize(State initialState) implements Action {
        }

        record ToggleHeaderVariantAction() implements Action {
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

        record AddBoardAction(String title) implements Action {
        }

        record SwitchViewMode(ViewMode mode) implements Action {
        }
    }
}
