package it.niedermann.nextcloud.deck.app.shared.data;

import static org.reactivestreams.FlowAdapters.toPublisher;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
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

        private final AccountRepository accountRepository;
        private final Gson gson;
        private final Map<Account, ApiProvider> cache = new HashMap<>();

        @Inject
        public Factory(AccountRepository accountRepository,
                       Gson gson) {

            this.accountRepository = accountRepository;
            this.gson = gson;
        }

        @Override
        public CompletableFuture<ApiProvider> create(Account.ID accountId) {

            return Flowable.fromPublisher(toPublisher(accountRepository.getAccount(accountId)))
                    .firstElement()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApply(account -> cache.computeIfAbsent(account, this::create));
        }

        private ApiProvider create(Account account) {
            return new RetrofitApiProvider(account, gson);
        }
    }
}
