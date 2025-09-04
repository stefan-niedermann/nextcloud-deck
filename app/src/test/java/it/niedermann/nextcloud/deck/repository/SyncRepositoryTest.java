package it.niedermann.nextcloud.deck.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
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
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.AbstractSyncDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.CardDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.StackDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import okhttp3.Headers;

@RunWith(RobolectricTestRunner.class)
public class SyncRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final Context context = ApplicationProvider.getApplicationContext();
    private final ServerAdapter serverAdapter = mock(ServerAdapter.class);
    private final DataBaseAdapter dataBaseAdapter = mock(DataBaseAdapter.class);
    private final SyncHelper.Factory syncHelperFactory = mock(SyncHelper.Factory.class);
    private final ConnectivityUtil connectivityUtil = mock(ConnectivityUtil.class);

    private SyncRepository syncRepository;

    @Before
    public void setup() {
        when(dataBaseAdapter.getCurrentAccountId$()).thenReturn(new MutableLiveData<>());
        syncRepository = new SyncRepository(context, serverAdapter, connectivityUtil, syncHelperFactory, dataBaseAdapter, MoreExecutors.newDirectExecutorService());
    }

    /**
     * When {@link SyncRepository#synchronizeBoard(long, ResponseCallback)} is triggered, it should
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
            public void onResponse(Boolean response, Headers headers) {

            }
        });

        syncRepository.synchronizeBoard(1L, responseCallback);

        verify(syncHelper, times(1)).setResponseCallback(responseCallback);
        verify(syncHelper, times(1)).doSyncFor(any(StackDataProvider.class));

        doThrow(OfflineException.class).when(syncHelper).doSyncFor(any());

        syncRepository.synchronizeBoard(1L, responseCallback);

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
            public void onResponse(Boolean response, Headers headers) {

            }
        });

        final var card = new Card();
        card.setStackId(5000L);

        syncRepository.synchronizeCard(responseCallback, card);

        verify(syncHelper, times(1)).setResponseCallback(responseCallback);
        verify(syncHelper, times(1)).doSyncFor(any(CardDataProvider.class));

        doThrow(OfflineException.class).when(syncHelper).doSyncFor(any());

        syncRepository.synchronizeCard(responseCallback, card);

        verify(responseCallback, times(1)).onError(any(OfflineException.class));
    }

    @Test
    public void testRefreshCapabilities() throws ExecutionException, InterruptedException {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        account.setEtag("This-Is-The-Old_ETag");
        final var serverResponse = new Capabilities();
        serverResponse.setDeckVersion(Version.of("1.0.0"));
        final var headers = Headers.of("ETag", "New-ETag");
        when(dataBaseAdapter.getAccountByIdDirectly(anyLong())).thenReturn(account);

        // Happy path

        doAnswer(invocation -> {
            assertEquals("The old eTag must be passed to the " + ServerAdapter.class.getSimpleName(),
                    "This-Is-The-Old_ETag", invocation.getArgument(0));
            //noinspection unchecked
            ((ResponseCallback<Capabilities>) invocation.getArgument(1))
                    .onResponse(serverResponse, headers);
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncRepository.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response, Headers headers) {
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
            ((ResponseCallback<Capabilities>) invocation.getArgument(1))
                    .onError(new NextcloudHttpRequestFailedException(ApplicationProvider.getApplicationContext(), 304, new RuntimeException()));
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncRepository.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response, Headers headers) {
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
            ((ResponseCallback<Capabilities>) invocation.getArgument(1))
                    .onError(new NextcloudHttpRequestFailedException(ApplicationProvider.getApplicationContext(), 500, new RuntimeException()));
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncRepository.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response, Headers headers) {
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
            ((ResponseCallback<Capabilities>) invocation.getArgument(1))
                    .onError(new NextcloudHttpRequestFailedException(ApplicationProvider.getApplicationContext(), 503, new RuntimeException("{\"ocs\": {\"meta\": {\"statuscode\": 503}, \"data\": {\"version\": {\"major\": 20, \"minor\": 0, \"patch\": 1}}}}")));
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncRepository.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response, Headers headers) {
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
            ((ResponseCallback<Capabilities>) invocation.getArgument(1))
                    .onError(new NetworkErrorException());
            return null;
        }).when(serverAdapter).getCapabilities(anyString(), any());

        syncRepository.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response, Headers headers) {
                fail("In case of any other exception the callback must not be responded successfully.");
            }

            @Override
            public void onError(Throwable throwable) {
                assertEquals(NetworkErrorException.class, throwable.getClass());
            }
        }).get();


        // No network available

        doThrow(new OfflineException()).when(serverAdapter).getCapabilities(anyString(), any());

        syncRepository.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response, Headers headers) {
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
        final var syncManagerSpy = spy(syncRepository);

        LastSyncUtil.init(ApplicationProvider.getApplicationContext());
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        final var capabilities = new Capabilities();
        capabilities.setDeckVersion(Version.minimumSupported());
        // Act as if refreshing capabilities is always successful
        doAnswer((invocation -> {
            //noinspection unchecked
            ((IResponseCallback<Capabilities>) invocation.getArgument(0)).onResponse(capabilities, IResponseCallback.EMPTY_HEADERS);
            return null;
        })).when(syncManagerSpy).refreshCapabilities(any());

        // Actual method invocation
        final var finalCallback = spy(new ResponseCallback<Boolean>(account) {
            @Override
            public void onResponse(Boolean response, Headers headers) {
            }
        });


        // Happy path

        final var syncHelper_positive = new SyncHelperMock(true);
        when(syncHelperFactory.create(any(), any(), any())).thenReturn(syncHelper_positive);

        syncManagerSpy.synchronize(finalCallback);

        verify(finalCallback, times(1)).onResponse(any(), any(Headers.class));


        // Bad paths

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
        public <T extends IRemoteEntity> void doSyncFor(@NonNull AbstractSyncDataProvider<T> provider, boolean parallel) {
            if (success) {
                cb.onResponse(true, IResponseCallback.EMPTY_HEADERS);
            } else {
                cb.onError(new RuntimeException("Bad path mocking"));
            }
        }

        @Override
        public <T extends IRemoteEntity> void doUpSyncFor(@NonNull AbstractSyncDataProvider<T> provider) {
            if (success) {
                cb.onResponse(true, IResponseCallback.EMPTY_HEADERS);
            } else {
                cb.onError(new RuntimeException("Bad path mocking"));
            }
        }
    }
}
