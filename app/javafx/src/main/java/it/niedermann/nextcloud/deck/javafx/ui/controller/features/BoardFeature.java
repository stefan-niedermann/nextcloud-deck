package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import io.reactivex.rxjava4.processors.FlowableProcessor;
import io.reactivex.rxjava4.processors.ReplayProcessor;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.FeatureFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.CardPreviewView;
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

    private final FeatureFactory featureFactory;
    private final GetBoardUseCase getBoardUseCase;

    private final FlowableProcessor<Long> boardId = ReplayProcessor.create();

    private CardPreviewView.CardPreviewActionListener cardPreviewActionListener;

    @Inject
    public BoardFeature(
            FeatureFactory featureFactory,
            GetBoardUseCase getBoardUseCase
    ) {
        this.featureFactory = featureFactory;
        this.getBoardUseCase = getBoardUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var disposable = this.boardId
                .distinctUntilChanged()
                .doOnNext(_ -> {
                    this.progress.setVisible(true);
                    this.progress.setManaged(true);
                    this.emptyContentView.setVisible(false);
                    this.emptyContentView.setManaged(false);
                    this.columns.setVisible(false);
                    this.columns.setManaged(false);
                })
                .switchMap(this.getBoardUseCase::execute)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    this.boardTitle.setText(board.title());
                    this.setColumns(board.columns(), this.cardPreviewActionListener);
                });

        addDisposable(disposable);
    }

    public void setBoard(long boardId) {
        this.boardId.onNext(boardId);
    }

    private void setColumns(Collection<Column> columns,
                            CardPreviewView.CardPreviewActionListener listener) {

        this.columns.getChildren().clear();


        for (final var column : columns) {
            final var fxBundle = this.featureFactory.inflateFeature(ColumnFeature.class);
            addDisposable(fxBundle.controller());
            fxBundle.controller().render(new ColumnFeature.Args(column, listener));
            this.columns.getChildren().add(fxBundle.view());
        }

        if (columns.isEmpty()) {

            this.emptyContentView.setVisible(true);
            this.emptyContentView.setManaged(true);
            this.columns.setVisible(false);
            this.columns.setManaged(false);

        } else {

            this.emptyContentView.setVisible(false);
            this.emptyContentView.setManaged(false);
            this.columns.setVisible(true);
            this.columns.setManaged(true);

        }

        this.progress.setVisible(false);
        this.progress.setManaged(false);
    }

    public void setCardPreviewActionListener(CardPreviewView.CardPreviewActionListener cardPreviewActionListener) {
        this.cardPreviewActionListener = cardPreviewActionListener;
    }
}
