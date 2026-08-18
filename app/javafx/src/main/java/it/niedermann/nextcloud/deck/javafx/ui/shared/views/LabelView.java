package it.niedermann.nextcloud.deck.javafx.ui.shared.views;

import it.niedermann.nextcloud.deck.util.ColorUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;

public class LabelView extends Label {
    private final BooleanProperty compact = new SimpleBooleanProperty(this, "compact", false);
    private it.niedermann.nextcloud.deck.domain.model.Color color;
    private String title;
    private final ColorUtil colorUtil;

    public LabelView(ColorUtil colorUtil) {
        this.colorUtil = colorUtil;
        compact.addListener((_, _, _) -> updateView());
    }

    public void setLabel(String title, it.niedermann.nextcloud.deck.domain.model.Color color) {
        this.title = title;
        this.color = color;
        updateView();
    }

    public BooleanProperty compactProperty() {
        return compact;
    }

    public void setCompact(boolean compact) {
        this.compact.set(compact);
    }

    private void updateView() {
        if (color == null) {
            return;
        }
        if (compact.get()) {
            setText(null);
            setPrefSize(38, 3);
            setMinSize(38, 3);
            setMaxSize(38, 3);
            setStyle("-fx-background-color: " + colorUtil.toWebColor(color) + "; -fx-background-radius: 2;");
        } else {
            setText(title);
            setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            setMaxSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            final int fgArgb = colorUtil.getForegroundColorForBackgroundColor(color.argb());
            setStyle("-fx-background-color: " + colorUtil.toWebColor(color) + "; -fx-text-fill: " + colorUtil.toWebColor(fgArgb) + "; -fx-padding: 2 4; -fx-background-radius: 4; -fx-font-size: 10px;");
        }
    }
}
