package it.niedermann.nextcloud.deck.persistence.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.accounts.NetworkErrorException;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import com.google.common.util.concurrent.MoreExecutors;
import com.nextcloud.android.sso.api.ParsedResponse;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.TestUtil;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AbstractSyncDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.CardDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.StackDataProvider;

@RunWith(RobolectricTestRunner.class)
public class SyncManagerTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final Context context = ApplicationProvider.getApplicationContext();
    private final ServerAdapter serverAdapter = mock(ServerAdapter.class);
    private final DataBaseAdapter dataBaseAdapter = mock(DataBaseAdapter.class);
    private final SyncHelper.Factory syncHelperFactory = mock(SyncHelper.Factory.class);

    private SyncManager syncManager;

    @Before
    public void setup() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final var constructor = SyncManager.class.getDeclaredConstructor(Context.class,
                DataBaseAdapter.class,
                ServerAdapter.class,
                ExecutorService.class,
                SyncHelper.Factory.class);
        constructor.setAccessible(true);
        syncManager = constructor.newInstance(context,
                dataBaseAdapter,
                serverAdapter,
                MoreExecutors.newDirectExecutorService(),
                syncHelperFactory);
    }

    @Test
    public void testHasAccounts() throws InterruptedException {
        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(true));
        final var hasAccountsPositive = syncManager.hasAccounts();
        assertTrue(TestUtil.getOrAwaitValue(hasAccountsPositive));
        verify(dataBaseAdapter, times(1)).hasAccounts();

        reset(dataBaseAdapter);

        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(false));
        final var hasAccountsNegative = syncManager.hasAccounts();
        assertFalse(TestUtil.getOrAwaitValue(hasAccountsNegative));
        verify(dataBaseAdapter, times(1)).hasAccounts();
    }

    @Test
    public void testReadAccount() throws InterruptedException {
        final var account = new Account();
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

    @Test
    public void testDeleteAccount() {
        doNothing().when(dataBaseAdapter).deleteAccount(anyLong());
        syncManager.deleteAccount(1337L);
        verify(dataBaseAdapter, times(1)).deleteAccount(1337L);
    }

    /**
     * When {@link SyncManager#synchronizeBoard(ResponseCallback, long)} is triggered, it should
     * pass the given {@link ResponseCallback} to the {@link SyncHelper} and trigger a
     * {@link SyncHelper#doSyncFor(AbstractSyncDataProvider)}.
     * {@link OfflineException} should be caught and passed to the {@link ResponseCallback}
     */
    @Test
    public void testSynchronizeBoard() {
        final var syncHelper = mock(SyncHelper.class);

        when(dataBaseAdapter.getFullBoardByLocalIdDirectly(anyLong(), anyLong())).thenReturn(new FullBoard());
        when(syncHelper.setResponseCallback(any())).thenReturn(syncHelper);
        doNothing().when(syncHelper).doSyncFor(any());
        when(syncHelperFactory.create(any(), any(), any())).thenReturn(syncHelper);

        final var responseCallback = spy(new ResponseCallback<Boolean>(new Account(1L)) {
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
    public void testSynchronizeCard() {
        final var syncHelper = mock(SyncHelper.class);
        final var fullStack = new FullStack();
        fullStack.setStack(new Stack("Test", 1L));

        when(dataBaseAdapter.getFullStackByLocalIdDirectly(anyLong())).thenReturn(fullStack);
        when(dataBaseAdapter.getBoardByLocalIdDirectly(anyLong())).thenReturn(new Board());
        when(syncHelper.setResponseCallback(any())).thenReturn(syncHelper);
        doNothing().when(syncHelper).doSyncFor(any());
        when(syncHelperFactory.create(any(), any(), any())).thenReturn(syncHelper);

        final var responseCallback = spy(new ResponseCallback<Boolean>(new Account(1L)) {
            @Override
            public void onResponse(Boolean response) {

            }
        });

        final var card = new Card();
        card.setStackId(5000L);

        syncManager.synchronizeCard(responseCallback, card);

        verify(syncHelper, times(1)).setResponseCallback(responseCallback);
        verify(syncHelper, times(1)).doSyncFor(any(CardDataProvider.class));

        doThrow(OfflineException.class).when(syncHelper).doSyncFor(any());

        syncManager.synchronizeCard(responseCallback, card);

        verify(responseCallback, times(1)).onError(any(OfflineException.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateAccountWithSuccessfulFirstBoardCall() {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        final var callback = mock(IResponseCallback.class);
        when(dataBaseAdapter.createAccountDirectly(any(Account.class))).thenReturn(account);
        doAnswer(invocation -> {
            ((ResponseCallback<ParsedResponse<List<FullBoard>>>) invocation.getArgument(0))
                    .onResponse(ParsedResponse.of(Collections.emptyList()));
            return null;
        }).when(serverAdapter).getBoards(any());

        syncManager.createAccount(account, callback);
        verify(dataBaseAdapter, times(1)).createAccountDirectly(account);
        verify(callback, times(1)).onResponse(account);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateAccountWithFailingFirstBoardCall() {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        final var callback = mock(IResponseCallback.class);
        when(dataBaseAdapter.createAccountDirectly(any(Account.class))).thenReturn(account);
        doAnswer(invocation -> {
            ((ResponseCallback<ParsedResponse<Capabilities>>) invocation.getArgument(0))
                    .onError(new NextcloudHttpRequestFailedException(404, new RuntimeException()));
            return null;
        }).when(serverAdapter).getBoards(any());

        syncManager.createAccount(account, callback);
        verify(dataBaseAdapter, times(1)).createAccountDirectly(account);
        verify(callback, times(1)).onResponse(account);
    }

    @Test
    public void testHasInternetConnection() {
        when(serverAdapter.hasInternetConnection()).thenReturn(true);
        assertTrue(syncManager.hasInternetConnection());

        when(serverAdapter.hasInternetConnection()).thenReturn(false);
        assertFalse(syncManager.hasInternetConnection());
    }

    @Test
    public void testReadAccountDirectly() {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        when(dataBaseAdapter.readAccountDirectly(1337L)).thenReturn(account);
        assertEquals(account, syncManager.readAccountDirectly(1337L));
    }

    @Test
    public void testReadAccounts() throws InterruptedException {
        final var accounts = Collections.singletonList(new Account(1337L, "Test", "Peter", "example.com"));
        final var wrappedAccounts = new WrappedLiveData<List<Account>>();
        wrappedAccounts.setValue(accounts);
        when(dataBaseAdapter.readAccounts()).thenReturn(wrappedAccounts);

        final var result = TestUtil.getOrAwaitValue(syncManager.readAccounts());

        verify(dataBaseAdapter, times(1)).readAccounts();
        assertEquals(1, result.size());
    }

    @Test
    public void testReadAccountsDirectly() {
        final var accounts = Collections.singletonList(new Account(1337L, "Test", "Peter", "example.com"));
        when(dataBaseAdapter.getAllAccountsDirectly()).thenReturn(accounts);
        assertEquals(1, syncManager.readAccountsDirectly().size());
    }

    @Test
    public void testRefreshCapabilities() throws ExecutionException, InterruptedException {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        account.setEtag("This-Is-The-Old_ETag");
        //noinspection unchecked
        final var mockedResponse = mock(ParsedResponse.class);
        final var serverResponse = new Capabilities();
        serverResponse.setDeckVersion(Version.of("1.0.0"));
        when(mockedResponse.getResponse()).thenReturn(serverResponse);
        when(mockedResponse.getHeaders()).thenReturn(Map.of("ETag", "New-ETag"));
        when(dataBaseAdapter.getAccountByIdDirectly(anyLong())).thenReturn(account);

        // Happy path

        doAnswer(invocation -> {
            assertEquals("The old eTag must be passed to the " + ServerAdapter.class.getSimpleName(),
                    "This-Is-The-Old_ETag", invocation.getArgument(0));
            //noinspection unchecked
            ((ResponseCallback<ParsedResponse<Capabilities>>) invocation.getArgument(1))
                    .onResponse(mockedResponse);
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncManager.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response) {
                assertEquals("Capabilities from server must be returned to the original callback",
                        Version.of("1.0.0"), response.getDeckVersion());
                verify(dataBaseAdapter).updateAccount(argThat(account -> "New-ETag".equals(account.getEtag())));
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable.getMessage());
            }
        }).get();


        // HTTP 304 - Not modified

        account.setMaintenanceEnabled(true);
        doAnswer(invocation -> {
            //noinspection unchecked
            ((ResponseCallback<ParsedResponse<Capabilities>>) invocation.getArgument(1))
                    .onError(new NextcloudHttpRequestFailedException(304, new RuntimeException()));
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncManager.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response) {
                assertEquals("Capabilities from server must be returned to the original callback",
                        Version.of("1.0.0"), response.getDeckVersion());
                assertFalse("The maintenance mode must be turned off after a HTTP 304 to avoid stucking \"offline\" and start a real request the next time - the maintenance mode might be off the next time.",
                        account.isMaintenanceEnabled());
                verify(dataBaseAdapter).updateAccount(argThat(account -> "This-Is-The-Old_ETag".equals(account.getEtag())));
            }

            @Override
            public void onError(Throwable throwable) {
                fail("HTTP 304 means nothing has been modified - This is not an error.");
            }
        }).get();


        // HTTP 500 - Server error

        doAnswer(invocation -> {
            //noinspection unchecked
            ((ResponseCallback<ParsedResponse<Capabilities>>) invocation.getArgument(1))
                    .onError(new NextcloudHttpRequestFailedException(500, new RuntimeException()));
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncManager.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response) {
                fail("In case of an HTTP 500 the callback must not be responded successfully.");
            }

            @Override
            public void onError(Throwable throwable) {
                assertEquals(NextcloudHttpRequestFailedException.class, throwable.getClass());
                assertEquals(500, ((NextcloudHttpRequestFailedException) throwable).getStatusCode());
            }
        }).get();


        // HTTP 503 - Maintenance mode

        doAnswer(invocation -> {
            //noinspection unchecked
            ((ResponseCallback<ParsedResponse<Capabilities>>) invocation.getArgument(1))
                    .onError(new NextcloudHttpRequestFailedException(503, new RuntimeException("{\"ocs\": {\"meta\": {\"statuscode\": 503}, \"data\": {\"version\": {\"major\": 20, \"minor\": 0, \"patch\": 1}}}}")));
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncManager.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response) {
                assertEquals(Version.of("20.0.1"), response.getNextcloudVersion());
            }

            @Override
            public void onError(Throwable throwable) {
                fail("Enabled maintenance mode should still return the capabilities");
            }
        }).get();


        // Anything else went wrong during the request

        doAnswer(invocation -> {
            //noinspection unchecked
            ((ResponseCallback<ParsedResponse<Capabilities>>) invocation.getArgument(1))
                    .onError(new NetworkErrorException());
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncManager.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response) {
                fail("In case of any other exception the callback must not be responded successfully.");
            }

            @Override
            public void onError(Throwable throwable) {
                assertEquals(NetworkErrorException.class, throwable.getClass());
            }
        }).get();


        // No network available

        doThrow(new OfflineException()).when(serverAdapter).getCapabilities(anyString(), any());

        syncManager.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response) {
                fail("In case of an " + OfflineException.class.getSimpleName() + " the callback must not be responded successfully.");
            }

            @Override
            public void onError(Throwable throwable) {
                assertEquals(OfflineException.class, throwable.getClass());
            }
        }).get();
    }

    @Test
    public void testSynchronize() {
        final var syncManagerSpy = spy(syncManager);

        LastSyncUtil.init(ApplicationProvider.getApplicationContext());
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        final var capabilities = new Capabilities();
        capabilities.setDeckVersion(Version.minimumSupported());
        // Act as if refreshing capabilities is always successful
        doAnswer((invocation -> {
            //noinspection unchecked
            ((IResponseCallback<Capabilities>) invocation.getArgument(0)).onResponse(capabilities);
            return null;
        })).when(syncManagerSpy).refreshCapabilities(any());

        // Actual method invocation
        final var finalCallback = spy(new ResponseCallback<Boolean>(account) {
            @Override
            public void onResponse(Boolean response) {
            }
        });


        // Happy path

        final var syncHelper_positive = new SyncHelperMock(true);
        when(syncHelperFactory.create(any(), any(), any())).thenReturn(syncHelper_positive);

        syncManagerSpy.synchronize(finalCallback);

        verify(finalCallback, times(1)).onResponse(any());


        // Bad paths

        assertThrows(IllegalArgumentException.class, () -> syncManagerSpy.synchronize(new ResponseCallback<>(new Account(null)) {
            @Override
            public void onResponse(Boolean response) {

            }
        }));

        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> syncManagerSpy.synchronize(new ResponseCallback<>(null) {
            @Override
            public void onResponse(Boolean response) {

            }
        }));

        final var syncHelper_negative = new SyncHelperMock(false);
        when(syncHelperFactory.create(any(), any(), any())).thenReturn(syncHelper_negative);

        syncManagerSpy.synchronize(finalCallback);

        verify(finalCallback, times(1)).onError(any());
    }

    /**
     * A simple {@link SyncHelper} implementation which directly responds to sync requests
     */
    private class SyncHelperMock extends SyncHelper {
        private IResponseCallback<Boolean> cb;
        private final boolean success;

        private SyncHelperMock(boolean success) {
            super(serverAdapter, dataBaseAdapter, Instant.now());
            this.success = success;
        }

        @Override
        public SyncHelper setResponseCallback(@NonNull ResponseCallback<Boolean> callback) {
            this.cb = callback;
            return this;
        }

        @Override
        public <T extends IRemoteEntity> void doSyncFor(@NonNull AbstractSyncDataProvider<T> provider) {
            if (success) {
                cb.onResponse(true);
            } else {
                cb.onError(new RuntimeException("Bad path mocking"));
            }
        }

        @Override
        public <T extends IRemoteEntity> void doUpSyncFor(@NonNull AbstractSyncDataProvider<T> provider) {
            if (success) {
                cb.onResponse(true);
            } else {
                cb.onError(new RuntimeException("Bad path mocking"));
            }
        }
    }
}
