package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.javafx.services.application.Theme;
import it.niedermann.nextcloud.deck.javafx.services.stage.PreferencesStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.TitleReportable;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class PreferencesScene extends DisposableController implements TitleReportable {

    @FXML
    private ComboBox<Theme> themeComboBox;
    @FXML
    private CheckBox backgroundSyncCheckBox;
    @FXML
    private CheckBox compactModeCheckBox;
    @FXML
    private CheckBox debugModeCheckBox;

    private final PreferencesStageContext preferencesStageContext;

    @AssistedInject
    public PreferencesScene(
            @Assisted PreferencesStageContext preferencesStageContext
    ) {
        this.preferencesStageContext = preferencesStageContext;
    }

    @AssistedFactory
    public interface Factory {
        PreferencesScene create(PreferencesStageContext preferencesStageContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        themeComboBox.getItems().setAll(Theme.values());

        final var stateDisposable = Flowable.fromPublisher(preferencesStageContext.getState())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(state -> {
                    themeComboBox.setValue(state.theme());
                    backgroundSyncCheckBox.setSelected(state.backgroundSync());
                    compactModeCheckBox.setSelected(state.compactMode());
                    debugModeCheckBox.setSelected(state.debugMode());
                });

        addDisposable(stateDisposable);

        themeComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                preferencesStageContext.dispatch(new PreferencesStageContext.Action.SetTheme(newValue));
            }
        });

        backgroundSyncCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            preferencesStageContext.dispatch(new PreferencesStageContext.Action.SetBackgroundSync(newValue));
        });

        compactModeCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            preferencesStageContext.dispatch(new PreferencesStageContext.Action.SetCompactMode(newValue));
        });

        debugModeCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            preferencesStageContext.dispatch(new PreferencesStageContext.Action.SetDebugMode(newValue));
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        preferencesStageContext.dispose();
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.just("Preferences");
    }
}
