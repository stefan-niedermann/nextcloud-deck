package it.niedermann.nextcloud.deck.javafx.ui.preferences;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class PreferencesScene extends AbstractScene {

    @FXML
    private ComboBox<ThemeService.Theme> themeComboBox;
    @FXML
    private CheckBox backgroundSyncCheckBox;
    @FXML
    private CheckBox compactModeCheckBox;
    @FXML
    private CheckBox debugModeCheckBox;

    private final PreferencesService preferencesService;

    @AssistedInject
    public PreferencesScene(
            @Assisted PreferencesService preferencesService
    ) {
        this.preferencesService = preferencesService;
    }

    @AssistedFactory
    public interface Factory {
        PreferencesScene create(PreferencesService preferencesService);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        themeComboBox.getItems().setAll(ThemeService.Theme.values());

        final var stateDisposable = Flowable.fromPublisher(preferencesService.getState())
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
                preferencesService.dispatch(new PreferencesService.Action.SetTheme(newValue));
            }
        });

        backgroundSyncCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            preferencesService.dispatch(new PreferencesService.Action.SetBackgroundSync(newValue));
        });

        compactModeCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            preferencesService.dispatch(new PreferencesService.Action.SetCompactMode(newValue));
        });

        debugModeCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            preferencesService.dispatch(new PreferencesService.Action.SetDebugMode(newValue));
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        preferencesService.dispose();
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.just("Preferences");
    }
}
