package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnIDsUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.EmptyContentView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

public class BoardKanbanFeature extends AbstractFeature {

    @FXML
    ProgressIndicator progress;
    @FXML
    EmptyContentView emptyContentView;
    @FXML
    HBox columns;

    private final GetBoardUseCase getBoardUseCase;
    private final ColumnFeature.Factory columnFactory;
    private final ListColumnIDsUseCase listColumnIDsUseCase;
    private final ViewModel viewModel;

    @AssistedInject
    public BoardKanbanFeature(
            Inflater inflater,
            GetBoardUseCase getBoardUseCase,
            ColumnFeature.Factory columnFactory,
            ListColumnIDsUseCase listColumnIDsUseCase,
            @Assisted ViewModel viewModel
    ) {
        super(inflater);

        this.viewModel = viewModel;
        this.getBoardUseCase = getBoardUseCase;
        this.columnFactory = columnFactory;
        this.listColumnIDsUseCase = listColumnIDsUseCase;
    }

    @AssistedFactory
    public interface Factory {
        BoardKanbanFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        this.progress.managedProperty().bind(this.progress.visibleProperty());
        this.emptyContentView.managedProperty().bind(this.emptyContentView.visibleProperty());
        this.columns.managedProperty().bind(this.columns.visibleProperty());

        final var disposable = viewModel.getBoardId()
                .observeOn(JavaFxScheduler.platform())
                .doOnNext(_ -> {
                    this.progress.setVisible(true);
                    this.emptyContentView.setVisible(false);
                    this.columns.setVisible(false);
                })
                .observeOn(Schedulers.virtual())
                .switchMap(this.getBoardUseCase::execute)
                .switchMap(board -> listColumnIDsUseCase.execute(board.id()))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(this::setColumns);

        addDisposable(disposable);
    }

    private void setColumns(Collection<Column.ID> columnIds) {

        this.columns.getChildren().clear();

        boolean first = true;
        for (final var columnId : columnIds) {
            final var columnFeature = columnFactory.create(columnId, viewModel);
            if (first) {
                columnFeature.setShouldRequestInitialFocus(true);
                first = false;
            }
            addDisposable(columnFeature);
            this.columns.getChildren().add(columnFeature.getRoot());
        }

        if (columnIds.isEmpty()) {
            this.emptyContentView.setVisible(true);
            this.columns.setVisible(false);
        } else {
            this.emptyContentView.setVisible(false);
            this.columns.setVisible(true);
        }

        this.progress.setVisible(false);
    }

    public interface ViewModel extends ColumnFeature.ViewModel {
        Flowable<Board.ID> getBoardId();
    }
}
