package it.niedermann.nextcloud.deck.app.shared.remote;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.app.shared.di.NamedVerbose;
import jakarta.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class LoggingInterceptor implements Interceptor {

    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    private final boolean verbose;

    @Inject
    public LoggingInterceptor(@NamedVerbose boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final var request = chain.request();
        if (!verbose) {
            return chain.proceed(request);
        }

        logger.info(() -> "--> " + request.method() + " " + request.url());

        final var response = chain.proceed(request);

        logger.info(() -> "<-- " + response.code() + " " + request.url());

        final ResponseBody responseBody = response.body();
        if (responseBody != null) {
            final BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            final Buffer buffer = source.getBuffer();
            logger.info(() -> buffer.clone().readString(StandardCharsets.UTF_8));
        }

        return response;
    }
}
