package it.niedermann.nextcloud.deck.domain.di;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.FlowAdapters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;

@Module
public class TestModule {

    @Provides
    @NamedUrl
    URL provideUrl() {
        try {
            final var host = System.getenv("NEXTCLOUD_TRUSTED_DOMAINS");
            return URI.create("http://" + host).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @NamedUsername
    String provideUsername() {
        return System.getenv("NEXTCLOUD_ADMIN_USER");
    }

    @Provides
    @NamedPassword
    String providePassword() {
        return System.getenv("NEXTCLOUD_ADMIN_PASSWORD");
    }

    @Provides
    @Singleton
    KeyValueStore provideKeyValueStore() {
        return new InMemoryKeyValueStore();
    }

    @Provides
    @Singleton
    DeckDatabase provideDeckDatabase() {
        return DeckDatabase.Companion
                .getInMemoryDatabaseBuilder()
                .build();
    }

    private static class InMemoryKeyValueStore implements KeyValueStore {

        private final Map<String, Object> store = new HashMap<>();

        @Override
        public void putString(@NotNull String key, @NotNull String value) {
            store.put(key, value);
        }

        @Override
        public void putLong(@NotNull String key, long value) {
            store.put(key, value);
        }

        @Override
        public void putBoolean(@NotNull String key, boolean value) {
            store.put(key, value);
        }

        @Override
        public Flow.@NotNull Publisher<@NotNull String> getString(@NotNull String key) {
            return FlowAdapters.toFlowPublisher(Flowable.just(store.get(key).toString()));
        }

        @Override
        public Flow.@NotNull Publisher<@NotNull Long> getLong(@NotNull String key) {
            return FlowAdapters.toFlowPublisher(Flowable.just((Long) store.get(key)));
        }

        @Override
        public Flow.@NotNull Publisher<@NotNull Boolean> getBoolean(@NotNull String key) {
            return FlowAdapters.toFlowPublisher(Flowable.just((Boolean) store.get(key)));
        }

        @Override
        public boolean containsKey(@NotNull String key) {
            return store.containsKey(key);
        }

        @Override
        public void clear() {
            store.clear();
        }

        @Override
        public void remove(@NotNull String key) {
            store.remove(key);
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({FIELD, METHOD, PARAMETER})
    public @interface NamedUrl {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({FIELD, METHOD, PARAMETER})
    public @interface NamedUsername {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({FIELD, METHOD, PARAMETER})
    public @interface NamedPassword {
    }
}
