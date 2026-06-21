package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BoardListItemView extends HBox {

    @FXML
    Circle circle;
    @FXML
    Label title;
    @FXML
    ContextMenu contextMenu;
    @FXML
    MenuItem share;

    public BoardListItemView() {
        Inflater.getInstance().inflateAndBind(this);
    }

    public void bind(Board board) {

        circle.fillProperty().setValue(Color.rgb(board.color().getRed(), board.color().getGreen(), board.color().getBlue()));
        title.textProperty().setValue(board.title());

        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });

    }
}
