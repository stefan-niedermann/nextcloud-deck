package it.niedermann.nextcloud.deck.exceptions;

import androidx.annotation.Nullable;

public class OfflineException extends IllegalStateException {

    private final Reason reason;

    public enum Reason {
        OFFLINE("offline",  "The device is currently offline"),
        CONNECTION_REFUSED("connection_refused", "Connection was refused, please check if your server is reachable"),
        CONNECTION_TIMEOUT("connection_timeout", "Connection timed out, please check if you're connected to the internet"),
        ;

        private String key;
        private String whatHappened;

        Reason(String key, String whatHappened) {
            this.key = key;
            this.whatHappened = whatHappened;
        }

        public String getKey() {
            return key;
        }

        public String getWhatHappened() {
            return whatHappened;
        }
    }


    public OfflineException() {
        this(Reason.OFFLINE);
    }
    public OfflineException(Reason reason) {
        super(reason.getWhatHappened());
        this.reason = reason;
    }

    @Nullable
    public Reason getReason() {
        return reason;
    }
}
