package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditBoardStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.TitleReportable;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EditBoardScene extends DisposableController implements TitleReportable {

    @FXML
    Pane contentHost;

    private final Inflater inflater;
    private final EditBoardFeature.Factory editBoardFeatureFactory;
    private final EditBoardStageContext editBoardStageContext;
    private final StageTitleResolver stageTitleResolver;

    @AssistedInject
    public EditBoardScene(Inflater inflater,
                          EditBoardFeature.Factory editBoardFeatureFactory,
                          StageTitleResolver stageTitleResolver,
                          @Assisted EditBoardStageContext editBoardStageContext) {
        this.inflater = inflater;
        this.editBoardFeatureFactory = editBoardFeatureFactory;
        this.editBoardStageContext = editBoardStageContext;
        this.stageTitleResolver = stageTitleResolver;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardScene create(EditBoardStageContext editBoardStageContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var bundle = inflater.inflate(editBoardFeatureFactory.create(editBoardStageContext));
        contentHost.getChildren().add(bundle.view());
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.fromPublisher(editBoardStageContext.getState())
                .switchMap(state -> stageTitleResolver.resolve(state.accountId(), state.boardId()));
    }
}
