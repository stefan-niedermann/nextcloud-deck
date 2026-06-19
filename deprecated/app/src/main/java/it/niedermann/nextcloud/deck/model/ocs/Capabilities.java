package it.niedermann.nextcloud.deck.model.ocs;

import android.graphics.Color;

import androidx.annotation.ColorInt;

public class Capabilities {

    @ColorInt
    public static final int DEFAULT_COLOR = -16743735; // #0082C9;
    @ColorInt
    public static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private Version deckVersion;
    private Version nextcloudVersion;

    @ColorInt
    private int color = DEFAULT_COLOR;
    @ColorInt
    private int textColor = DEFAULT_TEXT_COLOR;
    private boolean maintenanceEnabled = false;

    public Capabilities() {
    }

    public Version getDeckVersion() {
        return deckVersion;
    }

    public void setDeckVersion(Version deckVersion) {
        this.deckVersion = deckVersion;
    }

    public Version getNextcloudVersion() {
        return nextcloudVersion;
    }

    public void setNextcloudVersion(Version nextcloudVersion) {
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
