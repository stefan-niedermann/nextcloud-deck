package it.niedermann.nextcloud.deck.javafx.ui.preferences;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.preferences.features.AccountPreferencesFeature;
import it.niedermann.nextcloud.deck.javafx.ui.preferences.features.AccountPreferencesService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PreferencesScene extends AbstractScene {

    @FXML
    private ListView<NavigationItem> navigationListView;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox generalPreferences;

    @FXML
    private ComboBox<ThemeService.Theme> themeComboBox;
    @FXML
    private CheckBox compactModeCheckBox;
    @FXML
    private CheckBox debugModeCheckBox;

    private final PreferencesService preferencesService;
    private final AccountPreferencesFeature.Factory accountPreferencesFeatureFactory;
    private final AccountPreferencesService.Factory accountPreferencesServiceFactory;

    private final Map<Account.ID, AccountPreferencesFeature> accountFeatures = new HashMap<>();

    @AssistedInject
    public PreferencesScene(
            @Assisted PreferencesService preferencesService,
            Inflater inflater,
            AccountPreferencesFeature.Factory accountPreferencesFeatureFactory,
            AccountPreferencesService.Factory accountPreferencesServiceFactory,
            ThemeService themeService,
            HostServices hostServices,
            BuildConfig buildConfig,
            SyncScheduler syncScheduler
    ) {
        super(themeService, hostServices, buildConfig, syncScheduler, inflater);

        this.preferencesService = preferencesService;
        this.accountPreferencesFeatureFactory = accountPreferencesFeatureFactory;
        this.accountPreferencesServiceFactory = accountPreferencesServiceFactory;
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
                    compactModeCheckBox.setSelected(state.compactMode());
                    debugModeCheckBox.setSelected(state.debugMode());

                    final var items = new ArrayList<NavigationItem>();
                    items.add(new NavigationItem(PreferencesService.Section.GENERAL, Optional.empty()));
                    for (var account : state.accounts()) {
                        items.add(new NavigationItem(PreferencesService.Section.ACCOUNT, Optional.of(account)));
                    }

                    final var currentItems = navigationListView.getItems();
                    if (!Objects.equals(currentItems, items)) {
                        navigationListView.getItems().setAll(items);
                    }

                    // Handle section switching
                    switch (state.selectedSection()) {
                        case GENERAL -> contentArea.getChildren().setAll(generalPreferences);
                        case ACCOUNT -> state.selectedAccount().ifPresent(account -> {
                            final var bundle = accountFeatures.computeIfAbsent(account.id(), _ -> {
                                final var service = accountPreferencesServiceFactory.create(account);
                                return accountPreferencesFeatureFactory.create(service);
                            });
                            contentArea.getChildren().setAll(bundle.getRoot());
                        });
                    }
                });

        addDisposable(stateDisposable);

        navigationListView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                preferencesService.dispatch(new PreferencesService.Action.SwitchSection(newValue.section(), newValue.account()));
            }
        });

        themeComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                preferencesService.dispatch(new PreferencesService.Action.SetTheme(newValue));
            }
        });

        compactModeCheckBox.selectedProperty().addListener((_, _, newValue) -> preferencesService.dispatch(new PreferencesService.Action.SetCompactMode(newValue)));

        debugModeCheckBox.selectedProperty().addListener((_, _, newValue) -> preferencesService.dispatch(new PreferencesService.Action.SetDebugMode(newValue)));

        // Select General by default if nothing selected
        if (navigationListView.getSelectionModel().getSelectedItem() == null) {
            navigationListView.getSelectionModel().select(0);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        preferencesService.dispose();
        accountFeatures.values().forEach(AccountPreferencesFeature::dispose);
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.just("Preferences");
    }

    public record NavigationItem(
            PreferencesService.Section section,
            Optional<Account> account
    ) {
        @Override
        @NotNull
        public String toString() {
            return account.map(Account::username).orElse("General");
        }
    }
}
