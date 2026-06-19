package it.niedermann.nextcloud.auth.apptoken;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import okhttp3.Credentials;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppTokenAuthProvider {

    private static final Logger logger = Logger.getLogger(AppTokenAuthProvider.class.getName());

    public String generateToken(URL url, String username, String password) throws IOException {

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
    }

    public void invalidateToken(URL url, String username, String token) throws IOException {

        logger.fine("Invalidate token for username \"" + username + "\" on \"" + url + "\"");

        final var api = createApi(url);
        api.deleteAppPassword().execute();
    }

    private OcsV2CoreApi createApi(URL url) {
        return new Retrofit.Builder()
                .baseUrl(url.toString() + "/ocs/v2.php/core/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OcsV2CoreApi.class);
    }
}
