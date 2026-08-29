package it.niedermann.nextcloud.deck.javafx.ui.shared.views;

import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class IconListViewItem extends HBox {

    @FXML
    FontIcon icon;
    @FXML
    Label title;

    public IconListViewItem() {
        Inflater.getInstance().inflate(this);
    }

    public void bind(String iconLiteral, String title) {
        this.icon.setIconLiteral(iconLiteral);
        this.title.setText(title);
    }
}
