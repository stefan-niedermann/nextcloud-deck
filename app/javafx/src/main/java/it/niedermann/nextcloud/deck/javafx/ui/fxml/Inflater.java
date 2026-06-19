package it.niedermann.nextcloud.deck.javafx.ui.fxml;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

public class Inflater {

    private static volatile Inflater INSTANCE;

    private final AssetResolver resolver;

    private Inflater() {
        this.resolver = new AssetResolver();
    }

    /// Static access to Inflater is necessary for Views that must have a No-Args constructor to work well with FXML
    public static Inflater getInstance() {
        if (INSTANCE == null) {
            synchronized (Inflater.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Inflater();
                }
            }
        }
        return INSTANCE;
    }

    /// The controller will be instantiated by the given factory and returned together with the view
    public <T> FxBundle<T> inflateFxBundle(Class<T> controllerClass,
                                           Callback<Class<?>, Object> factory) {
        final var loader = createLoader(controllerClass);
        loader.setControllerFactory(factory);

        try {
            final Parent parent = loader.load();
            return new FxBundle<>(loader.getController(), parent);
        } catch (IOException e) {
            throw new InflateException(e);
        }
    }

    /// The controller must be the root node of the corresponding view
    public Parent inflateAndBind(Parent controller) {
        final var loader = createLoader(controller.getClass());
        loader.setRoot(controller);
        loader.setController(controller);

        try {
            return loader.load();
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

    public record FxBundle<TController>(TController controller,
                                        Parent view) {
    }
}
