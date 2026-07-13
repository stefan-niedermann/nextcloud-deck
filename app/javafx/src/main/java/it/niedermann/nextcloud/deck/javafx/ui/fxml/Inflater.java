package it.niedermann.nextcloud.deck.javafx.ui.fxml;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Inflater {

    private static final Inflater INSTANCE = new Inflater();

    private final AssetResolver resolver;

    private Inflater() {
        this.resolver = new AssetResolver();
    }

    /// Static access to Inflater is necessary for Views that must have a No-Args constructor to work well with FXML
    public static Inflater getInstance() {
        return INSTANCE;
    }

    /// Inflates the corresponding view and attaches the passed controller instance
    public <T> FxBundle<T> inflate(Object controller) {
        final var loader = createLoader(controller.getClass());
        loader.setController(controller);

        if (controller instanceof Parent) {
            loader.setRoot(controller);
        }

        try {
            final Parent parent = loader.load();
            return new FxBundle<>(loader.getController(), parent);
        } catch (IOException e) {
            throw new InflateException(e);
        }
    }

    private FXMLLoader createLoader(Class<?> controllerClass) {
        final var assets = this.resolver.resolveAssets(controllerClass);
        return new FXMLLoader(assets.fxmlUrl(), assets.resourceBundle().orElse(null));
    }

    public static class InflateException extends RuntimeException {
        private InflateException(IOException cause) {
            super(cause);
        }
    }

    /// Record of a view with its corresponding controller
    public record FxBundle<TController>(TController controller,
                                        Parent view) {
    }
}
