package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.view.GanttChart;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnIDsUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.EmptyContentView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BoardGanttFeature extends AbstractScene {

    @FXML
    VBox root;
    @FXML
    StackPane container;
    @FXML
    ProgressIndicator progress;
    @FXML
    EmptyContentView emptyContentView;

    private final ListCardPreviewsUseCase listCardPreviewsUseCase;
    private final ListColumnIDsUseCase listColumnIDsUseCase;
    private final ViewModel viewModel;

    private GanttChart<CardRow> ganttChart;

    @AssistedInject
    public BoardGanttFeature(
            ListCardPreviewsUseCase listCardPreviewsUseCase,
            ListColumnIDsUseCase listColumnIDsUseCase,
            @Assisted ViewModel viewModel
    ) {
        this.listCardPreviewsUseCase = listCardPreviewsUseCase;
        this.listColumnIDsUseCase = listColumnIDsUseCase;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        BoardGanttFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        this.progress.managedProperty().bind(this.progress.visibleProperty());
        this.emptyContentView.managedProperty().bind(this.emptyContentView.visibleProperty());

        final var disposable = viewModel.getBoardId()
                .observeOn(JavaFxScheduler.platform())
                .doOnNext(_ -> {
                    this.progress.setVisible(true);
                    this.emptyContentView.setVisible(false);
                    if (ganttChart != null) {
                        container.getChildren().remove(ganttChart);
                    }
                })
                .observeOn(Schedulers.virtual())
                .switchMap(boardId -> Flowable.fromPublisher(listColumnIDsUseCase.execute(boardId))
                        .flatMap(columnIds -> {
                            final var cardFlowables = columnIds.stream()
                                    .map(id -> Flowable.fromPublisher(listCardPreviewsUseCase.execute(id)))
                                    .collect(Collectors.toList());
                            if (cardFlowables.isEmpty()) {
                                return Flowable.just(List.<PreviewCard>of());
                            }
                            return Flowable.combineLatest(cardFlowables, args -> {
                                return java.util.Arrays.stream(args)
                                        .flatMap(arg -> ((List<PreviewCard>) arg).stream())
                                        .filter(card -> card.startDate() != null || card.dueDate() != null)
                                        .collect(Collectors.toList());
                            });
                        }))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(this::updateGantt);

        addDisposable(disposable);
    }

    private void updateGantt(List<PreviewCard> cards) {
        this.progress.setVisible(false);

        if (cards.isEmpty()) {
            this.emptyContentView.setVisible(true);
            return;
        }

        this.emptyContentView.setVisible(false);

        CardRow rootRow = new CardRow("ROOT");
        Layer layer = new Layer("Cards");

        for (PreviewCard card : cards) {
            CardRow row = new CardRow(card.title());
            rootRow.getChildren().add(row);
            row.addActivity(layer, new CardActivity(card));
        }

        if (ganttChart == null) {
            ganttChart = new GanttChart<>(rootRow);
            ganttChart.getLayers().add(layer);
            VBox.setVgrow(ganttChart, javafx.scene.layout.Priority.ALWAYS);
        } else {
            ganttChart.getLayers().setAll(layer);
            ganttChart.setRoot(rootRow);
        }

        if (!container.getChildren().contains(ganttChart)) {
            container.getChildren().add(ganttChart);
        }
        ganttChart.getGraphics().showEarliestActivities();
    }

    public static class CardActivity extends MutableActivityBase<PreviewCard> {
        public CardActivity(PreviewCard card) {
            setUserObject(card);
            setName(card.title());
            Instant start = (card.startDate() != null ? card.startDate() : card.dueDate()).toInstant();
            Instant end = (card.dueDate() != null ? card.dueDate() : card.startDate()).toInstant();
            if (start.equals(end)) {
                end = end.plus(1, java.time.temporal.ChronoUnit.HOURS);
            }
            setStartTime(start);
            setEndTime(end);
        }
    }

    public static class CardRow extends Row<CardRow, CardRow, CardActivity> {
        public CardRow(String name) {
            super(name);
        }
    }

    public interface ViewModel {
        Flowable<Board.ID> getBoardId();
    }
}
