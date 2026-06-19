package it.niedermann.nextcloud.deck.javafx.ui.fxml;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import jakarta.inject.Inject;

class AssetResolver {

    /// The common path shared by Java controller classes and resource paths
    private static final Path PACKAGE_PREFIX_PATH = Path.of("it", "niedermann", "nextcloud", "deck", "javafx", "ui", "controller");

    private static final Path FXML_ROOT = Path.of("fxml");
    private static final String FXML_FILE_EXTENSION = ".fxml";

    private static final Path PROPERTIES_ROOT = Path.of("i18n");
    private static final Path PROPERTIES_FILE_EXTENSION = Path.of(".properties");

    @Inject
    public AssetResolver() {

    }

    public FxAsset resolveAssets(Class<?> controllerClass) {

        final var packagePath = Path.of("", controllerClass.getPackageName().split("\\."));
        final var relativePath = PACKAGE_PREFIX_PATH.relativize(packagePath);
        final var name = controllerClass.getSimpleName();

        final var fxmlPath = FXML_ROOT.resolve(relativePath).resolve(name + FXML_FILE_EXTENSION);
        final var propertiesPath = PROPERTIES_ROOT.resolve(relativePath).resolve(name);

        final var classLoader = controllerClass.getClassLoader();
        final var fxmlUrl = classLoader.getResource(fxmlPath.toString());

        final var propertiesUrl = classLoader.getResource(propertiesPath.resolveSibling(name + PROPERTIES_FILE_EXTENSION).toString());

        final var resourceBundle = Optional.ofNullable(propertiesUrl)
                .map(_ -> propertiesPath)
                .map(Path::toString)
                .map(ResourceBundle::getBundle);

        return new FxAsset(fxmlUrl, resourceBundle);
    }

    public record FxAsset(URL fxmlUrl, Optional<ResourceBundle> resourceBundle) {
    }
}
