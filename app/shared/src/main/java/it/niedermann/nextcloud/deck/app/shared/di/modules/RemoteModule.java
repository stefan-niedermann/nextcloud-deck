package it.niedermann.nextcloud.deck.app.shared.di.modules;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.app.shared.data.RetrofitApiProvider;
import it.niedermann.nextcloud.deck.app.shared.remote.LoggingInterceptor;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RemoteModule {

    @Provides
    @Singleton
    ApiProvider.Factory provideApiProviderFactory(AccountRepository accountRepository,
                                                  Gson gson) {
        return new RetrofitApiProvider.Factory(accountRepository, gson);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(LoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    GsonConverterFactory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }
}
