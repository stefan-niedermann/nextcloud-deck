package it.niedermann.nextcloud.deck.javafx.services.stage;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.subscribers.TestSubscriber;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.services.application.Theme;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;

@ExtendWith(ApplicationExtension.class)
class PreferencesStageContextTest {

    private StoreLogger storeLogger;
    private KeyValueStore keyValueStore;

    private PreferencesStageContext preferencesStageContext;

    @BeforeEach
    void setUp() {
        storeLogger = mock(StoreLogger.class);
        keyValueStore = mock(KeyValueStore.class);

        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.just("AUTO"));
        when(keyValueStore.getBoolean(PreferencesStageContext.KEY_BACKGROUND_SYNC)).thenReturn(Flowable.just(true));
        when(keyValueStore.getBoolean(PreferencesStageContext.KEY_COMPACT_MODE)).thenReturn(Flowable.just(false));
        when(keyValueStore.getBoolean(it.niedermann.nextcloud.deck.javafx.services.application.ExceptionService.KEY_DEBUG_MODE)).thenReturn(Flowable.just(false));
        when(keyValueStore.containsKey(anyString())).thenReturn(CompletableFuture.completedFuture(true));

        preferencesStageContext = new PreferencesStageContext(
                storeLogger,
                keyValueStore,
                new PreferencesStageContext.State()
        );
    }

    @Test
    void testInitialState() {
        final var testSubscriber = new TestSubscriber<PreferencesStageContext.State>();
        preferencesStageContext.getState().subscribe(testSubscriber);
        
        testSubscriber.awaitCount(1);
        testSubscriber.assertValue(new PreferencesStageContext.State(Theme.AUTO, true, false, false));
    }

    @Test
    void testSetTheme() {
        when(keyValueStore.putString(ThemeService.KEY_THEME, "DARK")).thenReturn(CompletableFuture.completedFuture(null));

        final var testSubscriber = new TestSubscriber<PreferencesStageContext.State>();
        preferencesStageContext.getState().subscribe(testSubscriber);

        preferencesStageContext.dispatch(new PreferencesStageContext.Action.SetTheme(Theme.DARK));

        testSubscriber.awaitCount(2);
        testSubscriber.assertValueAt(1, state -> state.theme() == Theme.DARK);

        verify(keyValueStore).putString(ThemeService.KEY_THEME, "DARK");
    }

    @Test
    void testSetBackgroundSync() {
        when(keyValueStore.putBoolean(PreferencesStageContext.KEY_BACKGROUND_SYNC, false)).thenReturn(CompletableFuture.completedFuture(null));

        final var testSubscriber = new TestSubscriber<PreferencesStageContext.State>();
        preferencesStageContext.getState().subscribe(testSubscriber);

        preferencesStageContext.dispatch(new PreferencesStageContext.Action.SetBackgroundSync(false));

        testSubscriber.awaitCount(2);
        testSubscriber.assertValueAt(1, state -> !state.backgroundSync());

        verify(keyValueStore).putBoolean(PreferencesStageContext.KEY_BACKGROUND_SYNC, false);
    }
}
