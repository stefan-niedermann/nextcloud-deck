package it.niedermann.nextcloud.deck.javafx.fxml;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import jakarta.inject.Inject;

class AssetResolver {

    private static final Path FXML_ROOT = Path.of("fxml");
    private static final String FXML_FILE_EXTENSION = ".fxml";

    private static final Path PROPERTIES_ROOT = Path.of("i18n");
    private static final String PROPERTIES_FILE_EXTENSION = ".properties";

    @Inject
    public AssetResolver() {

    }

    public FxAsset resolveAssets(Class<?> controllerClass) {
        final var type = resolveType(controllerClass);
        final var name = controllerClass.getSimpleName();

        final var fxmlPath = FXML_ROOT.resolve(type).resolve(name + FXML_FILE_EXTENSION);
        final var propertiesPath = PROPERTIES_ROOT.resolve(type).resolve(name);

        final var classLoader = controllerClass.getClassLoader();
        final var fxmlUrl = classLoader.getResource(fxmlPath.toString());

        final var propertiesUrl = classLoader.getResource(propertiesPath + PROPERTIES_FILE_EXTENSION);

        final var resourceBundle = Optional.ofNullable(propertiesUrl)
                .map(_ -> propertiesPath)
                .map(Path::toString)
                .map(ResourceBundle::getBundle);

        return new FxAsset(fxmlUrl, resourceBundle);
    }

    private String resolveType(Class<?> controllerClass) {
        final var name = controllerClass.getSimpleName();
        if (name.endsWith("Scene")) {
            return "scenes";
        }
        if (name.endsWith("Feature")) {
            return "features";
        }
        if (name.endsWith("View")) {
            return "views";
        }

        final var packageName = controllerClass.getPackageName();
        if (packageName.contains(".scenes")) {
            return "scenes";
        }
        if (packageName.contains(".features")) {
            return "features";
        }
        if (packageName.contains(".views")) {
            return "views";
        }

        throw new IllegalArgumentException("Could not determine asset type for " + controllerClass.getName());
    }

    public record FxAsset(URL fxmlUrl, Optional<ResourceBundle> resourceBundle) {
    }
}
