package it.niedermann.nextcloud.deck.app.shared.data;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.ocs.OcsApi;
import jakarta.inject.Inject;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitApiProvider implements ApiProvider {

    private final OcsApi ocsApi;
    private final DeckApi deckApi;

    private RetrofitApiProvider(Account account, Gson gson) {
        final var retrofit = new Retrofit.Builder()
                .baseUrl(account.url())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        this.ocsApi = retrofit.create(OcsApi.class);
        this.deckApi = retrofit.create(DeckApi.class);
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
        private final Map<Account, ApiProvider> cache = new HashMap<>();

        @Inject
        public Factory(Gson gson) {
            this.gson = gson;
        }

        @Override
        public synchronized ApiProvider create(Account account) {
            return cache.computeIfAbsent(account, a -> new RetrofitApiProvider(a, gson));
        }
    }
}
