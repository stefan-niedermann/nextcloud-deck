package it.niedermann.nextcloud.deck.model.ocs;

public class Capabilities {
    Version deckVersion;
    Version nextcloudVersion;

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
}
