package it.niedermann.nextcloud.deck.app.shared.di.modules;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.app.shared.data.RetrofitApiProvider;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Singleton;

@Module
public class RemoteModule {

    @Provides
    @Singleton
    ApiProvider.Factory provideApiProviderFactory(AccountRepository accountRepository,
                                                  Gson gson) {
        return new RetrofitApiProvider.Factory(accountRepository, gson);
    }

}
