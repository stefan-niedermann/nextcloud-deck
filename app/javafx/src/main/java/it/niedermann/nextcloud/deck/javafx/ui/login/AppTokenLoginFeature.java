package it.niedermann.nextcloud.deck.javafx.ui.login;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import it.niedermann.nextcloud.deck.domain.model.AuthenticatedAccount;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AppTokenLoginFeature extends AbstractScene {

    private static final Logger logger = Logger.getLogger(AppTokenLoginFeature.class.getName());

    @FXML
    private TextField url;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button submit;

    private final AppTokenAuthProvider appTokenAuthProvider;
    private final ViewModel viewModel;

    @AssistedInject
    public AppTokenLoginFeature(
            AppTokenAuthProvider appTokenAuthProvider,
            @Assisted ViewModel viewModel
    ) {
        this.appTokenAuthProvider = appTokenAuthProvider;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        AppTokenLoginFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        url.setOnAction(_ -> submit());
        username.setOnAction(_ -> submit());
        password.setOnAction(_ -> submit());
        submit.setOnAction(_ -> submit());
    }

    public void setUrl(URL url) {
        this.url.setText(url.toString());
    }

    public void submit() {
        final var rawUrlText = this.url.getText().trim();
        final String urlText;
        if (!rawUrlText.startsWith("http://") && !rawUrlText.startsWith("https://")) {
            urlText = "https://" + rawUrlText;
            this.url.setText(urlText);
        } else {
            urlText = rawUrlText;
        }

        final URL parsedUrl;
        try {
            parsedUrl = URI.create(urlText).toURL();
        } catch (MalformedURLException e) {
            viewModel.onAccountAuthenticationFailed(null, e);
            return;
        }

        authenticateViaAppToken(parsedUrl, username.getText(), password.getText())
                .thenAccept(viewModel::onAccountAuthenticated)
                .exceptionally(throwable -> {
                    viewModel.onAccountAuthenticationFailed(parsedUrl, throwable);
                    return null;
                });
    }

    private CompletableFuture<AuthenticatedAccount> authenticateViaAppToken(URL parsedUrl, String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String token = appTokenAuthProvider.generateToken(parsedUrl, username, password);
                return new AuthenticatedAccount(parsedUrl, username, token);
            } catch (IOException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    public interface ViewModel {
        void onAccountAuthenticated(AuthenticatedAccount account);

        void onAccountAuthenticationFailed(URL url, Throwable e);
    }
}
