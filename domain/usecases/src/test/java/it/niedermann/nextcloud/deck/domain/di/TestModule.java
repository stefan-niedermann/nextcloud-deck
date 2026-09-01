package it.niedermann.nextcloud.deck.domain.di;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.app.shared.di.NamedVerbose;
import it.niedermann.nextcloud.deck.app.shared.remote.LoggingInterceptor;
import it.niedermann.nextcloud.deck.domain.e2e.ServerManager;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;

@Module
public class TestModule {

    @Provides
    @Singleton
    public ServerManager provideServerManager(OkHttpClient httpClient,
                                              Gson gson) {

        final String username = System.getenv("NEXTCLOUD_ADMIN_USER");
        final String password = System.getenv("NEXTCLOUD_ADMIN_PASSWORD");
        final URL url;

        try {
            final var host = System.getenv("NEXTCLOUD_TRUSTED_DOMAINS");
            Objects.requireNonNull(host);
            if (host.isBlank()) {
                throw new IllegalArgumentException("Invalid host");
            }
            if (!host.startsWith("http")) {
                url = URI.create("http://" + host).toURL();
            } else {
                url = URI.create(host).toURL();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new ServerManager(httpClient, gson, url, username, password);
    }

    @Provides
    @NamedVerbose
    boolean provideVerbose() {
        return true;
    }

    @Provides
    @Singleton
    LoggingInterceptor provideLoggingInterceptor(@NamedVerbose boolean verbose) {
        return new LoggingInterceptor(verbose);
    }
}
