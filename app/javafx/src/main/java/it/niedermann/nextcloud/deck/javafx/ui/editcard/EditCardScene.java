package it.niedermann.nextcloud.deck.javafx.ui.editcard;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.editcard.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.StageTitleResolver;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EditCardScene extends AbstractScene {

    @FXML
    Pane contentHost;

    private final Inflater inflater;
    private final EditCardFeature.Factory editCardFeatureFactory;
    private final EditCardService editCardService;
    private final StageTitleResolver stageTitleResolver;

    @AssistedInject
    public EditCardScene(Inflater inflater,
                         EditCardFeature.Factory editCardFeatureFactory,
                         StageTitleResolver stageTitleResolver,
                         @Assisted EditCardService editCardService) {
        this.inflater = inflater;
        this.editCardFeatureFactory = editCardFeatureFactory;
        this.editCardService = editCardService;
        this.stageTitleResolver = stageTitleResolver;
    }

    @AssistedFactory
    public interface Factory {
        EditCardScene create(EditCardService editCardService);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var bundle = inflater.inflate(editCardFeatureFactory.create(editCardService));
        contentHost.getChildren().add(bundle.view());
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.fromPublisher(editCardService.getState())
                .switchMap(state -> stageTitleResolver.resolve(state.cardId().orElse(null)));
    }
}
