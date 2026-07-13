package it.niedermann.nextcloud.deck.javafx.exception;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import io.reactivex.rxjava4.exceptions.CompositeException;
import io.reactivex.rxjava4.exceptions.OnErrorNotImplementedException;
import jakarta.inject.Inject;

public class ExceptionUnwrapper {

    @Inject
    public ExceptionUnwrapper() {

    }

    public Throwable unwrap(Throwable throwable) {

        if (throwable == null) {
            return null;
        }

        final var unwrappable = Stream.of(

                // RxJava
                OnErrorNotImplementedException.class,
                CompositeException.class,

                // CompletableFuture
                CompletionException.class,
                CancellationException.class

        ).anyMatch(t -> t.isAssignableFrom(throwable.getClass()));

        if (unwrappable) {

            final var cause = throwable.getCause();

            if (cause == null) {
                return throwable;

            } else {
                return unwrap(cause);
            }
        }

        return throwable;
    }
}
