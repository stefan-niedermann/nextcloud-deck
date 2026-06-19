package it.niedermann.nextcloud.deck.javafx.util;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class FxUtils {

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

}