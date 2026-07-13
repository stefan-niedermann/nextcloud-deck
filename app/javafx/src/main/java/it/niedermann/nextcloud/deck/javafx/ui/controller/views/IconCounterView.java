package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class IconCounterView extends HBox {

    @FXML
    FontIcon fontIcon;
    @FXML
    Label counter;

    IntegerProperty counterProperty = new SimpleIntegerProperty();

    public IconCounterView() {
        Inflater.getInstance().inflate(this);
    }

    public void initialize() {
        counter.textProperty().bind(counterProperty.asString());
        counter.visibleProperty().bind(counterProperty.map(counter -> counter != null && counter.intValue() > 1));
        counter.managedProperty().bind(counter.visibleProperty());
    }

    public int getCounter() {
        return counterProperty.get();
    }

    public void setCounter(int value) {
        counterProperty.set(value);
    }

    public ObjectProperty<Ikon> iconCodeProperty() {
        return fontIcon.iconCodeProperty();
    }

    public Ikon getIconCode() {
        return fontIcon.getIconCode();
    }

    public void setIconCode(Ikon iconCode) {
        fontIcon.setIconCode(iconCode);
    }

    public String getIconLiteral() {
        return fontIcon.getIconLiteral();
    }

    public void setIconLiteral(String iconCode) {
        fontIcon.setIconLiteral(iconCode);
    }
}
