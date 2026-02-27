package it.niedermann.nextcloud.deck.domain.model;

public abstract class Capabilities {

    protected int color;
    protected boolean maintenanceEnabled = false;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isMaintenanceEnabled() {
        return maintenanceEnabled;
    }

    public void setMaintenanceEnabled(boolean maintenanceEnabled) {
        this.maintenanceEnabled = maintenanceEnabled;
    }
}
