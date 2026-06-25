package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.FeatureFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.EmptyContentView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
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

    private final StageContext stageContext;
    private final FeatureFactory featureFactory;
    private final GetBoardUseCase getBoardUseCase;

    @Inject
    public BoardFeature(
            StageContext stageContext,
            FeatureFactory featureFactory,
            GetBoardUseCase getBoardUseCase
    ) {
        this.stageContext = stageContext;
        this.featureFactory = featureFactory;
        this.getBoardUseCase = getBoardUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        this.progress.managedProperty().bind(this.progress.visibleProperty());
        this.emptyContentView.managedProperty().bind(this.emptyContentView.visibleProperty());
        this.columns.managedProperty().bind(this.columns.visibleProperty());

        final var disposable = Flowable.fromPublisher(this.stageContext.getState())
                .map(StageContext.State::boardId)
                .distinctUntilChanged()
                .doOnNext(_ -> {
                    this.progress.setVisible(true);
                    this.emptyContentView.setVisible(false);
                    this.columns.setVisible(false);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
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
            final var fxBundle = this.featureFactory.inflateFeature(ColumnFeature.class);
            addDisposable(fxBundle.controller());
            fxBundle.controller().render(column);
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

}
