package it.niedermann.nextcloud.deck.app.shared.data;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.ocs.OcsApi;
import jakarta.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitApiProvider implements ApiProvider {

    private final OcsApi ocsApi;
    private final DeckApi deckApi;

    private RetrofitApiProvider(Account account, Gson gson, OkHttpClient client) {
        String baseUrl = account.url().toString();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        final var authenticatedClient = client.newBuilder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    final var request = chain.request();
                    final String credentials = account.username() + ":" + account.token();
                    final String auth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                    return chain.proceed(request.newBuilder()
                            .header("Authorization", auth)
                            .build());
                })
                .build();

        final var commonBuilder = new Retrofit.Builder()
                .client(authenticatedClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create());

        this.ocsApi = commonBuilder.baseUrl(baseUrl).build().create(OcsApi.class);
        this.deckApi = commonBuilder.baseUrl(baseUrl + "index.php/apps/deck/api/").build().create(DeckApi.class);
    }

    @Override
    public OcsApi getOcsApi() {
        return ocsApi;
    }

    @Override
    public DeckApi getDeckApi() {
        return deckApi;
    }

    public static class Factory implements ApiProvider.Factory {

        private final Gson gson;
        private final OkHttpClient client;
        private final Map<Account, ApiProvider> cache = new HashMap<>();

        @Inject
        public Factory(Gson gson, OkHttpClient client) {
            this.gson = gson;
            this.client = client;
        }

        @Override
        public synchronized ApiProvider create(Account account) {
            return cache.computeIfAbsent(account, a -> new RetrofitApiProvider(a, gson, client));
        }
    }
}
