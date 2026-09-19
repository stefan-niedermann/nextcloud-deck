package it.niedermann.nextcloud.deck.javafx.ui.shared.views;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class IconCounterView extends HBox {

    @FXML
    FontIcon fontIcon;
    @FXML
    Label counter;

    private final StringProperty text = new SimpleStringProperty();
    private final BooleanProperty countVisible = new SimpleBooleanProperty(true);

    public IconCounterView() {
        Inflater.getInstance().inflate(this);
    }

    public void initialize() {
        counter.textProperty().bind(text);
        counter.visibleProperty().bind(text.isNotEmpty().and(countVisible));
        counter.managedProperty().bind(counter.visibleProperty());
    }

    public void setCounter(int value) {
        text.set(String.valueOf(value));
        countVisible.set(value > 1);
    }

    public void setCounter(int value, boolean alwaysShow) {
        text.set(String.valueOf(value));
        countVisible.set(alwaysShow || value > 1);
    }

    public void setText(String value) {
        text.set(value);
        countVisible.set(value != null && !value.isEmpty());
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
