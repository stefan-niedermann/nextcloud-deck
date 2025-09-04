package it.niedermann.nextcloud.deck.repository.sync.exceptions;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import it.niedermann.nextcloud.deck.R;

public class OfflineException extends IllegalStateException {

    private final Reason reason;

    public OfflineException() {
        this(Reason.OFFLINE);
    }

    public OfflineException(@NonNull Reason reason) {
        super(reason.getKey());
        this.reason = reason;
    }

    @NonNull
    public Reason getReason() {
        return reason;
    }

    public enum Reason {
        OFFLINE("Device is currently offline", R.string.error_dialog_tip_offline_no_internet),
        CONNECTION_REFUSED("Connection refused", R.string.error_dialog_tip_offline_connection_refused),
        CONNECTION_TIMEOUT("Connection timeout", R.string.error_dialog_tip_offline_connection_timeout),
        CONNECTION_REJECTED("Connection rejected", R.string.error_dialog_tip_connection_rejected),
        ;

        private final String key;
        @StringRes
        private final int message;

        Reason(@NonNull String key, @StringRes int message) {
            this.key = key;
            this.message = message;
        }

        public String getKey() {
            return key;
        }

        @StringRes
        public int getMessage() {
            return message;
        }
    }
}
