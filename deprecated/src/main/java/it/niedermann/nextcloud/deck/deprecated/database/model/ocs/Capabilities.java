package it.niedermann.nextcloud.deck.database.entity.ocs;

import android.graphics.Color;

import androidx.annotation.ColorInt;

public class Capabilities {

    @ColorInt
    public static final int DEFAULT_COLOR = -16743735; // #0082C9;
    @ColorInt
    public static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private it.niedermann.nextcloud.deck.database.entity.ocs.Version deckVersion;
    private it.niedermann.nextcloud.deck.database.entity.ocs.Version nextcloudVersion;

    @ColorInt
    private int color = DEFAULT_COLOR;
    @ColorInt
    private int textColor = DEFAULT_TEXT_COLOR;
    private boolean maintenanceEnabled = false;

    public Capabilities() {
    }

    public it.niedermann.nextcloud.deck.database.entity.ocs.Version getDeckVersion() {
        return deckVersion;
    }

    public void setDeckVersion(it.niedermann.nextcloud.deck.database.entity.ocs.Version deckVersion) {
        this.deckVersion = deckVersion;
    }

    public it.niedermann.nextcloud.deck.database.entity.ocs.Version getNextcloudVersion() {
        return nextcloudVersion;
    }

    public void setNextcloudVersion(it.niedermann.nextcloud.deck.database.entity.ocs.Version nextcloudVersion) {
        this.nextcloudVersion = nextcloudVersion;
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }

    @ColorInt
    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
    }

    public boolean isMaintenanceEnabled() {
        return maintenanceEnabled;
    }

    public void setMaintenanceEnabled(boolean maintenanceEnabled) {
        this.maintenanceEnabled = maintenanceEnabled;
    }
}
