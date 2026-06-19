package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class IconCounterView extends HBox {

    @FXML
    ImageView imageView;
    @FXML
    Label counter;

    IntegerProperty counterProperty = new SimpleIntegerProperty();

    public IconCounterView() {
        Inflater.getInstance().inflateAndBind(this);

        Bindings.bindBidirectional(
                counter.textProperty(),
                counterProperty,
                new javafx.util.converter.NumberStringConverter()
        );

        counter.visibleProperty().bind(counter.textProperty().isNotNull());
        counter.managedProperty().bind(counter.visibleProperty());
    }

    public int getCounter() {
        return counterProperty.get();
    }

    public void setCounter(int value) {
        counterProperty.set(value);
    }

    public IntegerProperty counterProperty() {
        return counterProperty;
    }

    public String getImage() {
        return imageView.getImage().getUrl();
    }

    public void setImage(String value) {
        imageView.setImage(new Image(value));
    }
}
