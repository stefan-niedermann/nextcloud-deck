package it.niedermann.nextcloud.deck.javafx.util;

import org.intellij.lang.annotations.Language;

import java.awt.Color;
import java.util.Arrays;
import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class FxUtils {

    public static String createAccentColorCss(Color accentColor) {
        final var rgb = accentColor.getRGB();
        final var hexString = '#' + Integer.toHexString(rgb).substring(2);

        @Language("CSS") final var css = """
                -fx-accent: %1$s;
                -fx-default-button: derive(-fx-accent, 90%%);
                -fx-focus-color: derive(-fx-accent, 60%%);
                -fx-faint-focus-color: derive(-fx-accent, 65%%);
                """.formatted(hexString);

        return css;
    }

    public static Background colorToBackground(Color color) {
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getAlpha() / 255.0
        );

        return new Background(new BackgroundFill(fxColor, CornerRadii.EMPTY, Insets.EMPTY));
    }

    /// @return Traverses the [Node] hierarchy to returns the closest parent element that is a [ListCell] or [ListView]
    public static Optional<Node> findListCellOrListViewParent(Node node) {
        while (node != null && !(node instanceof ListCell) && !(node instanceof ListView<?>)) {
            node = node.getParent();
        }

        return Optional.ofNullable(node);
    }

    /// @param listCellOrListViewNode a [ListView] node or a [ListCell] node
    /// @return the closest index of the given [ListCell]. Can be the index of the given [ListCell] or the index after, depending on the `sceneY` argument. Can also be the
    public static int identifyClosestListViewIndex(Node listCellOrListViewNode, double sceneY) {
        if (listCellOrListViewNode instanceof ListCell<?> listCell) {
            return identifyClosestListViewIndex(listCell, sceneY);

        } else if (listCellOrListViewNode instanceof ListView<?> listView) {
            return listView.getItems().size();

        } else {
            throw new IllegalArgumentException("This method only accepts nodes of type " + ListCell.class.getSimpleName() + " or " + ListView.class.getSimpleName());
        }
    }

    private static int identifyClosestListViewIndex(ListCell<?> listCell, double sceneY) {
        final var intersectedIndex = listCell.getIndex();
        final var listCellPositionInScene = listCell.localToScene(0, 0);
        final double listCellBlockStartInScene = listCellPositionInScene.getY();
        final double listCellBlockEndInScene = listCellBlockStartInScene + listCell.getHeight();
        final boolean resultIsBeforeIntersectedNode = Math.abs(sceneY - listCellBlockStartInScene) <=
                                                      Math.abs(sceneY - listCellBlockEndInScene);
        return resultIsBeforeIntersectedNode
                ? intersectedIndex
                : intersectedIndex + 1;
    }

    public static BooleanBinding anyVisible(Node... nodes) {
        return Bindings.createBooleanBinding(() -> Arrays.stream(nodes).anyMatch(Node::isVisible),
                Arrays.stream(nodes).map(Node::visibleProperty).toArray(Observable[]::new));
    }
}