package it.niedermann.nextcloud.deck.api;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.UnknownErrorException;

import java.util.Objects;

import it.niedermann.nextcloud.deck.exceptions.OfflineException;

public class ServerCommunicationErrorHandler {
    public static Throwable translateError(Throwable error) {
        try {
            if (error.getClass() == UnknownErrorException.class) {
                return handleSsoExceptions((UnknownErrorException) error);
            } else if (error.getClass() == ClassNotFoundException.class) {
                return handleClassNotFoundError((ClassNotFoundException) error);
            } else {
                return error;
            }
        } catch (NullPointerException e) {
            return error;
        }
    }

    private static Throwable handleSsoExceptions(@NonNull UnknownErrorException error) {
        final String message = Objects.requireNonNull(error.getMessage(), "SSO handler got no ExceptionMessage").toLowerCase();
        if (message.contains("econnrefused") || message.contains("connection refused")) {
            return new OfflineException(OfflineException.Reason.CONNECTION_REFUSED);
        }
        return error;
    }

    private static Throwable handleClassNotFoundError(@NonNull ClassNotFoundException error) {
        final String message = Objects.requireNonNull(error.getMessage(), "ClassNotFound handler got no ExceptionMessage").toLowerCase();
        if (message.contains("connecttimeoutexception")) {
            return new OfflineException(OfflineException.Reason.CONNECTION_TIMEOUT);
        }
        return error;
    }
}
