package it.niedermann.nextcloud.deck.javafx;

import org.testfx.api.FxRobot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;

public class ScreenshotUtil {

    public static void captureScene(FxRobot robot, String title) throws IOException {
        final var window = robot.targetWindow();
        final var scene = window.getScene();

        final var path = Path.of("build", "screenshots", title + ".png");
        Files.createDirectories(path.getParent());

        final var capture = robot.capture(scene.getRoot());
        final var image = SwingFXUtils.fromFXImage(capture.getImage(), null);

        ImageIO.write(image, "png", path.toFile());
    }
}
