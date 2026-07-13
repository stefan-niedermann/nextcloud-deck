package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.EmptyContentView;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

public class BoardFeature extends DisposableController {

    @FXML
    Label boardTitle;
    @FXML
    ProgressIndicator progress;
    @FXML
    EmptyContentView emptyContentView;
    @FXML
    HBox columns;

    private final Inflater inflater;
    private final GetBoardUseCase getBoardUseCase;
    private final ColumnFeature.Factory columnFactory;
    private final ViewModel viewModel;

    @AssistedInject
    public BoardFeature(
            Inflater inflater,
            GetBoardUseCase getBoardUseCase,
            ColumnFeature.Factory columnFactory,
            @Assisted ViewModel viewModel
    ) {
        this.viewModel = viewModel;
        this.inflater = inflater;
        this.getBoardUseCase = getBoardUseCase;
        this.columnFactory = columnFactory;
    }

    @AssistedFactory
    public interface Factory {
        BoardFeature create(ViewModel viewModel);
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
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    this.boardTitle.setText(board.title());
                    this.setColumns(board.columns());
                });

        addDisposable(disposable);
    }

    private void setColumns(Collection<Column> columns) {

        this.columns.getChildren().clear();

        for (final var column : columns) {
            final var fxBundle = this.inflater.inflate(columnFactory.create(column, viewModel));
            if (fxBundle.controller() instanceof Disposable d) {
                addDisposable(d);
            }
            this.columns.getChildren().add(fxBundle.view());
        }

        if (columns.isEmpty()) {
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
