package it.niedermann.nextcloud.deck.app.shared.remote;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Response;

public class LoggingInterceptor implements Interceptor {

    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    @Inject
    public LoggingInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        final var response = chain.proceed(chain.request());

        logger.info(() -> "HTTP " + response.code() + " " + chain.request().url());
        logger.finer(() -> {
            try {
                return response.peekBody(Long.MAX_VALUE).toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        logger.finest(() -> chain.request().headers().toString());

        return response;
    }
}