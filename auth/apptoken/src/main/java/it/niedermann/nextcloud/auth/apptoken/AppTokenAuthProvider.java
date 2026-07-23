package it.niedermann.nextcloud.auth.apptoken;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class AppTokenAuthProvider {

    private static final Logger logger = Logger.getLogger(AppTokenAuthProvider.class.getName());

    private final OkHttpClient client;
    private final GsonConverterFactory gsonConverterFactory;

    @Inject
    public AppTokenAuthProvider(OkHttpClient client,
                                GsonConverterFactory gsonConverterFactory) {
        this.client = client;
        this.gsonConverterFactory = gsonConverterFactory;
    }

    public String generateToken(URL url, String username, String password) throws IOException {
        try {
            logger.fine("Generate token for username \"" + username + "\" on \"" + url + "\"");

            final var api = createApi(url);
            final var credentials = Credentials.basic(username, password);
            final var response = api.getAppPassword(credentials).execute();

            if (!response.isSuccessful()) {
                final var errorBody = response.code();
                final var body = response.body();
                throw new NullPointerException("API response was null for server address \"" + url + "\" and username \"" + username + "\".\n" + body + "\n" + errorBody);
            }

            return response.body().ocs().data().apppassword();
        } catch (Exception e) {

            throw e;
        }
    }

    public void invalidateToken(URL url, String username, String token) throws IOException {

        logger.fine("Invalidate token for username \"" + username + "\" on \"" + url + "\"");

        final var api = createApi(url);
        api.deleteAppPassword().execute();
    }

    private OcsV2CoreApi createApi(URL url) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url.toString() + "/ocs/v2.php/core/")
                .addConverterFactory(gsonConverterFactory)
                .build()
                .create(OcsV2CoreApi.class);
    }


}
