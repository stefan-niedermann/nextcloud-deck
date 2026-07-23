package it.niedermann.nextcloud.auth.webloginflowv2;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;

import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class WebLoginFlowV2AuthProvider {

    private static final Logger logger = Logger.getLogger(WebLoginFlowV2AuthProvider.class.getName());

    private final OkHttpClient client;
    private final GsonConverterFactory gsonConverterFactory;

    @Inject
    public WebLoginFlowV2AuthProvider(OkHttpClient client,
                                      GsonConverterFactory gsonConverterFactory) {
        this.client = client;
        this.gsonConverterFactory = gsonConverterFactory;
    }

    private final Duration pollLimit = Duration.ofMinutes(20);

    public AuthenticatedAccount initializeAuthentication(URL url) throws IOException, URISyntaxException {

        logger.fine("Initialize WebLoginFlowV2");

        final var api = createApi(url);
        final var startTime = Instant.now();
        final var response = api.initWebLoginFlowV2().execute();

        if (!response.isSuccessful()) {
            final var errorBody = response.code();
            throw new RuntimeException("API response was null for server address \"" + url + "\".\n" + response.body() + "\n" + errorBody);
        }

        final var loginUrl = Objects.requireNonNull(response.body()).login();
        final var desktop = Desktop.getDesktop();
        desktop.browse(new URI(loginUrl));

        do {
            try {
                final var pollResponse = api.pollWebLoginFlowV2(response.body().poll().token()).execute();
                if (pollResponse.code() == 200) {
                    return new AuthenticatedAccount(Objects.requireNonNull(pollResponse.body()));
                }
                //noinspection BusyWait
                Thread.sleep(1_000);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } while (Duration.between(startTime, Instant.now()).compareTo(pollLimit) < 0);

        throw new RuntimeException();
    }

    public void invalidateToken(URL url, String username, String token) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private OcsV2CoreApi createApi(URL url) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url.toString())
                .addConverterFactory(gsonConverterFactory)
                .build()
                .create(OcsV2CoreApi.class);
    }
}
