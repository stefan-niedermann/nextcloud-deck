package it.niedermann.nextcloud.deck.api;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.UnknownErrorException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import it.niedermann.nextcloud.deck.exceptions.OfflineException;

public class ServerCommunicationErrorHandler {

    private static final Handler[] handlers = new Handler[]{
            new Handler(UnknownErrorException.class, Arrays.asList("econnrefused", "connection refused"), OfflineException.Reason.CONNECTION_REFUSED),
            new Handler(UnknownErrorException.class, Arrays.asList("Unable to resolve host", "No address associated with hostname"), OfflineException.Reason.CONNECTION_REFUSED),
            new Handler(ClassNotFoundException.class, Collections.singletonList("connecttimeoutexception"), OfflineException.Reason.CONNECTION_TIMEOUT)
    };

    public static Throwable translateError(Throwable error) {
        try {
            for (final var handler : handlers) {
                if (error.getClass() == handler.originalExceptionType) {
                    return handler.handle(error);
                }
            }
            return error;
        } catch (NullPointerException e) {
            return error;
        }
    }

    private static class Handler {
        private final Class<?> originalExceptionType;
        private final Collection<String> indicators;
        private final OfflineException.Reason reason;

        Handler(@NonNull Class<?> originalExceptionType, @NonNull Collection<String> indicators, @NonNull OfflineException.Reason reason) {
            this.originalExceptionType = originalExceptionType;
            this.indicators = indicators;
            this.reason = reason;
        }

        @NonNull
        Throwable handle(@NonNull Throwable error) throws NullPointerException {
            final String message = Objects.requireNonNull(error.getMessage(), "ExceptionMessage is null").toLowerCase();
            if (indicators.stream().anyMatch(message::contains)) {
                return new OfflineException(reason);
            }
            return error;
        }
    }
}
