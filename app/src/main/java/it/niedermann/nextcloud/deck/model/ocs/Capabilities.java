package it.niedermann.nextcloud.deck.model.ocs;

import java.util.HashMap;
import java.util.Map;

public class Capabilities {
    // accountID - Capabiliy
    public static final Map<Long, Capabilities> CACHE = new HashMap<>();

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
