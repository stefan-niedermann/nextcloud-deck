package it.niedermann.nextcloud.deck.javafx.ui.preferences.features;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class AccountPreferencesFeature extends AbstractFeature {

    public interface ViewModel extends Disposable {
        Account getAccount();
        io.reactivex.rxjava4.core.Flowable<Boolean> getBackgroundSync();
        void setBackgroundSync(boolean enabled);
    }

    @FXML
    private Label accountNameLabel;
    @FXML
    private CheckBox backgroundSyncCheckBox;

    private final ViewModel viewModel;

    @AssistedInject
    public AccountPreferencesFeature(
            Inflater inflater,
            @Assisted ViewModel viewModel
    ) {
        super(inflater);

        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        AccountPreferencesFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        accountNameLabel.setText(viewModel.getAccount().displayName());

        final var disposable = viewModel.getBackgroundSync()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(backgroundSyncCheckBox::setSelected);

        addDisposable(disposable);

        backgroundSyncCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            viewModel.setBackgroundSync(newValue);
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        viewModel.dispose();
    }
}
