package it.niedermann.nextcloud.deck;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AbstractSyncDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.StackDataProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SyncManager.class, DeckLog.class})
public class SyncManagerTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Context context;
    @Mock
    private ServerAdapter serverAdapter;
    @Mock
    private DataBaseAdapter dataBaseAdapter;
    @Mock
    private DeckLog deckLog;

    @InjectMocks
    private SyncManager syncManager;

    @Before
    public void prepareDoAsync() {
        PowerMockito.mockStatic(DeckLog.class);
        PowerMockito
                .replace(PowerMockito.method(SyncManager.class, "doAsync", Runnable.class))
                .with((obj, method, arguments) -> {
                    ((Runnable) arguments[0]).run();
                    return null;
                });
    }

    /**
     * When {@link SyncManager#synchronizeBoard(IResponseCallback, long)} is triggered, it should
     * pass the given {@link IResponseCallback} to the {@link SyncHelper} and trigger a
     * {@link SyncHelper#doSyncFor(AbstractSyncDataProvider)}.
     * {@link OfflineException} should be caught and passed to the {@link IResponseCallback}
     */
    @Test
    public void testSynchronizeBoard() throws Exception {
        final SyncHelper syncHelper = mock(SyncHelper.class);

        when(dataBaseAdapter.getFullBoardByLocalIdDirectly(anyLong(), anyLong())).thenReturn(new FullBoard());
        when(syncHelper.setResponseCallback(any())).thenReturn(syncHelper);
        doNothing().when(syncHelper).doSyncFor(any());
        whenNew(SyncHelper.class).withAnyArguments().thenReturn(syncHelper);

        IResponseCallback<Boolean> responseCallback = spy(new IResponseCallback<Boolean>(new Account(1L)) {
            @Override
            public void onResponse(Boolean response) {

            }
        });

        syncManager.synchronizeBoard(responseCallback, 1L);

        verify(syncHelper, times(1)).setResponseCallback(responseCallback);
        verify(syncHelper, times(1)).doSyncFor(any(StackDataProvider.class));

        doThrow(OfflineException.class).when(syncHelper).doSyncFor(any());

        syncManager.synchronizeBoard(responseCallback, 1L);

        verify(responseCallback, times(1)).onError(any(OfflineException.class));
    }

    @Test
    public void testHasAccounts() throws InterruptedException {
        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(true));
        final LiveData<Boolean> hasAccountsPositive = syncManager.hasAccounts();
        assertEquals(Boolean.TRUE, TestUtil.getOrAwaitValue(hasAccountsPositive));
        verify(dataBaseAdapter, times(1)).hasAccounts();

        reset(dataBaseAdapter);

        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(false));
        final LiveData<Boolean> hasAccountsNegative = syncManager.hasAccounts();
        assertEquals(Boolean.FALSE, TestUtil.getOrAwaitValue(hasAccountsNegative));
        verify(dataBaseAdapter, times(1)).hasAccounts();
    }

    @Test
    public void testReadAccount() throws InterruptedException {
        final Account account = new Account();
        account.setId(5L);
        account.setName("text@example.com");

        when(dataBaseAdapter.readAccount(5)).thenReturn(new MutableLiveData<>(account));
        assertEquals(account, TestUtil.getOrAwaitValue(syncManager.readAccount(5)));
        verify(dataBaseAdapter, times(1)).readAccount(5);

        reset(dataBaseAdapter);

        when(dataBaseAdapter.readAccount("test@example.com")).thenReturn(new MutableLiveData<>(account));
        assertEquals(account, TestUtil.getOrAwaitValue(syncManager.readAccount("test@example.com")));
        verify(dataBaseAdapter, times(1)).readAccount("test@example.com");

        reset(dataBaseAdapter);

        when(dataBaseAdapter.readAccount(5)).thenReturn(new MutableLiveData<>(null));
        assertNull(TestUtil.getOrAwaitValue(syncManager.readAccount(5)));
        verify(dataBaseAdapter, times(1)).readAccount(5);

        reset(dataBaseAdapter);

        when(dataBaseAdapter.readAccount("test@example.com")).thenReturn(new MutableLiveData<>(null));
        assertNull(TestUtil.getOrAwaitValue(syncManager.readAccount("test@example.com")));
        verify(dataBaseAdapter, times(1)).readAccount("test@example.com");
    }
}
