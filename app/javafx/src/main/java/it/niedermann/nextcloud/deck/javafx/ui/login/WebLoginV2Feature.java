package it.niedermann.nextcloud.deck.javafx.ui.login;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.auth.webloginflowv2.WebLoginFlowV2AuthProvider;
import it.niedermann.nextcloud.deck.app.shared.mapper.AuthMapper;
import it.niedermann.nextcloud.deck.domain.model.ImportAccount;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class WebLoginV2Feature extends AbstractFeature {

    private static final Logger logger = Logger.getLogger(WebLoginV2Feature.class.getName());

    @FXML
    private TextField url;
    @FXML
    private Button submit;

    private final WebLoginFlowV2AuthProvider webLoginV2AuthProvider;
    private final AuthMapper authMapper;
    private final ViewModel viewModel;

    @AssistedInject
    public WebLoginV2Feature(
            Inflater inflater,
            WebLoginFlowV2AuthProvider webLoginV2AuthProvider,
            AuthMapper authMapper,
            @Assisted ViewModel viewModel
    ) {
        super(inflater);

        this.webLoginV2AuthProvider = webLoginV2AuthProvider;
        this.authMapper = authMapper;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        WebLoginV2Feature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        url.setOnAction(_ -> submit());
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

        authenticateViaWebLogin(parsedUrl)
                .thenAccept(viewModel::onAccountAuthenticated)
                .exceptionally(throwable -> {
                    viewModel.onAccountAuthenticationFailed(parsedUrl, throwable);
                    return null;
                });
    }

    private CompletableFuture<ImportAccount> authenticateViaWebLogin(URL parsedUrl) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final var account = webLoginV2AuthProvider.initializeAuthentication(parsedUrl);
                return authMapper.toImportAccount(account);
            } catch (IOException | URISyntaxException | UnsupportedOperationException |
                     InterruptedException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                throw new CompletionException(e);
            }
        });
    }

    public interface ViewModel {
        void onAccountAuthenticated(ImportAccount account);

        void onAccountAuthenticationFailed(URL url, Throwable e);
    }
}
