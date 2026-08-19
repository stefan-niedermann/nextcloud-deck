package it.niedermann.nextcloud.deck.javafx.services.stage;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
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
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.preferences.PreferencesService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ExceptionService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;

@ExtendWith(ApplicationExtension.class)
class PreferencesServiceTest {

    private StoreLogger storeLogger;
    private KeyValueStore keyValueStore;

    private PreferencesService preferencesService;

    @BeforeEach
    void setUp() {
        storeLogger = mock(StoreLogger.class);
        keyValueStore = mock(KeyValueStore.class);

        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.just("AUTO"));
        when(keyValueStore.getBoolean(PreferencesService.KEY_BACKGROUND_SYNC)).thenReturn(Flowable.just(true));
        when(keyValueStore.getBoolean(PreferencesService.KEY_COMPACT_MODE)).thenReturn(Flowable.just(false));
        when(keyValueStore.getBoolean(ExceptionService.KEY_DEBUG_MODE)).thenReturn(Flowable.just(false));
        when(keyValueStore.containsKey(anyString())).thenReturn(CompletableFuture.completedFuture(true));

        preferencesService = new PreferencesService(
                storeLogger,
                keyValueStore,
                new PreferencesService.State()
        );
    }

    @Test
    void testInitialState() {
        final var testSubscriber = new TestSubscriber<PreferencesService.State>();
        preferencesService.getState().subscribe(testSubscriber);
        
        testSubscriber.awaitCount(1);
        testSubscriber.assertValue(new PreferencesService.State(ThemeService.Theme.AUTO, true, false, false));
    }

    @Test
    void testSetTheme() {
        doReturn(CompletableFuture.completedFuture(null)).when(keyValueStore).putString(ThemeService.KEY_THEME, "DARK");

        final var testSubscriber = new TestSubscriber<PreferencesService.State>();
        preferencesService.getState().subscribe(testSubscriber);

        preferencesService.dispatch(new PreferencesService.Action.SetTheme(ThemeService.Theme.DARK));

        testSubscriber.awaitCount(2);
        testSubscriber.assertValueAt(1, state -> state.theme() == ThemeService.Theme.DARK);

        verify(keyValueStore).putString(ThemeService.KEY_THEME, "DARK");
    }

    @Test
    void testSetBackgroundSync() {
        doReturn(CompletableFuture.completedFuture(null)).when(keyValueStore).putBoolean(PreferencesService.KEY_BACKGROUND_SYNC, false);

        final var testSubscriber = new TestSubscriber<PreferencesService.State>();
        preferencesService.getState().subscribe(testSubscriber);

        preferencesService.dispatch(new PreferencesService.Action.SetBackgroundSync(false));

        testSubscriber.awaitCount(2);
        testSubscriber.assertValueAt(1, state -> !state.backgroundSync());

        verify(keyValueStore).putBoolean(PreferencesService.KEY_BACKGROUND_SYNC, false);
    }
}
