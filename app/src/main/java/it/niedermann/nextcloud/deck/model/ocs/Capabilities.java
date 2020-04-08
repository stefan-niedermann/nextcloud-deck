package it.niedermann.nextcloud.deck.model.ocs;

import java.util.HashMap;
import java.util.Map;

public class Capabilities {
    // accountID - Capabiliy
    public static final Map<Long, Capabilities> CACHE = new HashMap<>();

    private Version deckVersion;
    private Version nextcloudVersion;

    private String color = "#0082c9";
    private String textColor = "#ffffff";
    private String serverDeckVersion = "0.6.4";
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

    public static Map<Long, Capabilities> getCACHE() {
        return CACHE;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getServerDeckVersion() {
        return serverDeckVersion;
    }

    public void setServerDeckVersion(String serverDeckVersion) {
        this.serverDeckVersion = serverDeckVersion;
    }

    public boolean isMaintenanceEnabled() {
        return maintenanceEnabled;
    }

    public void setMaintenanceEnabled(boolean maintenanceEnabled) {
        this.maintenanceEnabled = maintenanceEnabled;
    }
}
