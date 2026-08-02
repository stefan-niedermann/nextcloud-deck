package it.niedermann.nextcloud.deck.javafx.services.stage;

import java.util.Collection;
import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Maybe;
import io.reactivex.rxjava4.disposables.Disposable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.BoardShare;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardSharesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.RemoveBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.DeleteColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.UpdateColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.AddLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.DeleteLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.UpdateLabelUseCase;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardColumnsFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardDetailsFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardLabelsFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardShareFeature;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;

public class EditBoardStageContext extends Store<EditBoardStageContext.State, EditBoardStageContext.Action> implements
        EditBoardFeature.ViewModel,
        EditBoardDetailsFeature.ViewModel,
        EditBoardColumnsFeature.ViewModel,
        EditBoardLabelsFeature.ViewModel,
        EditBoardShareFeature.ViewModel {

    private final GetBoardUseCase getBoardUseCase;
    private final ListCardsUseCase listCardsUseCase;

    private final AddColumnUseCase addColumnUseCase;
    private final UpdateColumnUseCase updateColumnUseCase;
    private final DeleteColumnUseCase deleteColumnUseCase;
    private final ListColumnsUseCase listColumnsUseCase;
    private final GetColumnUseCase getColumnUseCase;

    private final AddLabelUseCase addLabelUseCase;
    private final UpdateLabelUseCase updateLabelUseCase;
    private final DeleteLabelUseCase deleteLabelUseCase;
    private final ListLabelsUseCase listLabelsUseCase;

    private final ListBoardSharesUseCase listBoardSharesUseCase;
    private final AddBoardShareUseCase addBoardShareUseCase;
    private final RemoveBoardShareUseCase removeBoardShareUseCase;
    private final UpdateBoardShareUseCase updateBoardShareUseCase;

    @AssistedInject
    public EditBoardStageContext(
            StoreLogger storeLogger,
            GetBoardUseCase getBoardUseCase,
            ListCardsUseCase listCardsUseCase,
            AddColumnUseCase addColumnUseCase,
            UpdateColumnUseCase updateColumnUseCase,
            DeleteColumnUseCase deleteColumnUseCase,
            ListColumnsUseCase listColumnsUseCase,
            GetColumnUseCase getColumnUseCase,
            AddLabelUseCase addLabelUseCase,
            UpdateLabelUseCase updateLabelUseCase,
            DeleteLabelUseCase deleteLabelUseCase,
            ListLabelsUseCase listLabelsUseCase,
            ListBoardSharesUseCase listBoardSharesUseCase,
            AddBoardShareUseCase addBoardShareUseCase,
            RemoveBoardShareUseCase removeBoardShareUseCase,
            UpdateBoardShareUseCase updateBoardShareUseCase,
            @Assisted State initialState
    ) {
        super(storeLogger, initialState);
        this.getBoardUseCase = getBoardUseCase;
        this.listCardsUseCase = listCardsUseCase;
        this.addColumnUseCase = addColumnUseCase;
        this.updateColumnUseCase = updateColumnUseCase;
        this.deleteColumnUseCase = deleteColumnUseCase;
        this.listColumnsUseCase = listColumnsUseCase;
        this.getColumnUseCase = getColumnUseCase;
        this.addLabelUseCase = addLabelUseCase;
        this.updateLabelUseCase = updateLabelUseCase;
        this.deleteLabelUseCase = deleteLabelUseCase;
        this.listLabelsUseCase = listLabelsUseCase;
        this.listBoardSharesUseCase = listBoardSharesUseCase;
        this.addBoardShareUseCase = addBoardShareUseCase;
        this.removeBoardShareUseCase = removeBoardShareUseCase;
        this.updateBoardShareUseCase = updateBoardShareUseCase;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardStageContext create(State initialState);
    }

    @Override
    public Flowable<Board> getBoard() {
        return Flowable.fromPublisher(getState())
                .observeOn(Schedulers.virtual())
                .map(State::boardId)
                .switchMap(getBoardUseCase::execute);
    }

    @Override
    public Flowable<List<Column>> getColumns() {
        return Flowable.fromPublisher(getState())
                .observeOn(Schedulers.virtual())
                .map(State::boardId)
                .switchMap(id -> Flowable.fromPublisher(listColumnsUseCase.execute(id)))
                .switchMap(ids -> Flowable.fromIterable(ids)
                        .concatMap(id -> Flowable.fromPublisher(getColumnUseCase.execute(id)))
                        .toList()
                        .toFlowable());
    }

    @Override
    public void onAddColumn(String title) {
        addColumnUseCase.execute(new CreateColumn(initialState.boardId(), title, 0));
    }

    @Override
    public Disposable onDeleteColumn(Column column) {
        return Maybe.fromPublisher(listCardsUseCase.execute(column.id()))
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cards -> {
                    if (cards.isEmpty()) {
                        deleteColumnUseCase.execute(column.id());
                    } else {
                        // TODO Confirmation dialog
                        System.out.println("Column not empty!");
                    }
                });
    }

    @Override
    public void onUpdateColumn(Column column, String newTitle) {
        updateColumnUseCase.execute(new Column(column.id(), column.boardId(), newTitle, column.order()));
    }

    @Override
    public Flowable<Collection<Label>> getLabels() {
        return Flowable.fromPublisher(getState())
                .observeOn(Schedulers.virtual())
                .map(State::boardId)
                .switchMap(id -> Flowable.fromPublisher(listLabelsUseCase.execute(id)));
    }

    @Override
    public void onAddLabel(String title, Color color) {
        addLabelUseCase.execute(new Label(new Label.ID(0), initialState.boardId(), title, color));
    }

    @Override
    public void onDeleteLabel(Label label) {
        deleteLabelUseCase.execute(label.id());
    }

    @Override
    public void onUpdateLabel(Label label, String newTitle, Color newColor) {
        updateLabelUseCase.execute(new Label(label.id(), label.boardId(), newTitle, newColor));
    }

    @Override
    public Flowable<List<BoardShare>> getShares() {
        return Flowable.fromPublisher(getState())
                .observeOn(Schedulers.virtual())
                .map(State::boardId)
                .switchMap(id -> Flowable.fromPublisher(listBoardSharesUseCase.execute(id)));
    }

    @Override
    public void onAddShare(User user) {
        addBoardShareUseCase.execute(initialState.boardId(), user.id(), new Board.Permissions(true, false, false, false));
    }

    @Override
    public void onRemoveShare(BoardShare share) {
        removeBoardShareUseCase.execute(initialState.boardId(), share.user().id());
    }

    @Override
    public void onUpdateShare(BoardShare share, Board.Permissions permissions) {
        updateBoardShareUseCase.execute(initialState.boardId(), share.user().id(), permissions);
    }

    public record State(Account.ID accountId, Board.ID boardId) {
    }

    public sealed interface Action {
        record Initialize(State initialState) implements Action {
        }
    }
}
