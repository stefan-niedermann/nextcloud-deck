package it.niedermann.nextcloud.deck.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

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

import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.TestUtil;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import it.niedermann.nextcloud.deck.repository.sync.SyncScheduler;

@RunWith(RobolectricTestRunner.class)
public class AccountRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final Context context = ApplicationProvider.getApplicationContext();
    private final DataBaseAdapter dataBaseAdapter = mock(DataBaseAdapter.class);
    private final SyncScheduler syncScheduler = mock(SyncScheduler.class);
    private final ConnectivityUtil connectivityUtil = mock(ConnectivityUtil.class);

    private AccountRepository accountRepository;

    @Before
    public void setup() {
        when(dataBaseAdapter.getCurrentAccountId$()).thenReturn(new MutableLiveData<>());
        accountRepository = new AccountRepository(context,
                connectivityUtil,
                dataBaseAdapter,
                syncScheduler,
                MoreExecutors.newDirectExecutorService(),
                MoreExecutors.newDirectExecutorService(),
                MoreExecutors.newDirectExecutorService(),
                MoreExecutors.newDirectExecutorService(),
                MoreExecutors.newDirectExecutorService());
    }

    @Test
    public void testHasAccounts() throws InterruptedException {
        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(true));
        final var hasAccountsPositive = accountRepository.hasAccounts();
        assertTrue(TestUtil.getOrAwaitValue(hasAccountsPositive));
        verify(dataBaseAdapter, times(1)).hasAccounts();

        reset(dataBaseAdapter);

        when(dataBaseAdapter.hasAccounts()).thenReturn(new MutableLiveData<>(false));
        final var hasAccountsNegative = accountRepository.hasAccounts();
        assertFalse(TestUtil.getOrAwaitValue(hasAccountsNegative));
        verify(dataBaseAdapter, times(1)).hasAccounts();
    }

    @Test
    public void testReadAccount() throws InterruptedException {
        final var account = new Account();
        account.setId(5L);
        account.setName("text@example.com");

        when(dataBaseAdapter.readAccount(5)).thenReturn(new MutableLiveData<>(account));
        assertEquals(account, TestUtil.getOrAwaitValue(accountRepository.readAccount(5)));
        verify(dataBaseAdapter, times(1)).readAccount(5);

        reset(dataBaseAdapter);

        when(dataBaseAdapter.readAccount("test@example.com")).thenReturn(new MutableLiveData<>(account));
        assertEquals(account, TestUtil.getOrAwaitValue(accountRepository.readAccount("test@example.com")));
        verify(dataBaseAdapter, times(1)).readAccount("test@example.com");

        reset(dataBaseAdapter);

        when(dataBaseAdapter.readAccount(5)).thenReturn(new MutableLiveData<>(null));
        assertNull(TestUtil.getOrAwaitValue(accountRepository.readAccount(5)));
        verify(dataBaseAdapter, times(1)).readAccount(5);

        reset(dataBaseAdapter);

        when(dataBaseAdapter.readAccount("test@example.com")).thenReturn(new MutableLiveData<>(null));
        assertNull(TestUtil.getOrAwaitValue(accountRepository.readAccount("test@example.com")));
        verify(dataBaseAdapter, times(1)).readAccount("test@example.com");
    }

    @Test
    public void testReadAccounts() throws InterruptedException {
        final var accounts = Collections.singletonList(new Account(1337L, "Test", "Peter", "example.com"));
        final var wrappedAccounts = new MutableLiveData<List<Account>>();
        wrappedAccounts.setValue(accounts);
        when(dataBaseAdapter.readAccounts()).thenReturn(wrappedAccounts);

        final var result = TestUtil.getOrAwaitValue(accountRepository.readAccounts());

        verify(dataBaseAdapter, times(1)).readAccounts();
        assertEquals(1, result.size());
    }

    @Test
    public void testReadAccountsDirectly() {
        final var accounts = Collections.singletonList(new Account(1337L, "Test", "Peter", "example.com"));
        when(dataBaseAdapter.getAllAccountsDirectly()).thenReturn(accounts);
        assertEquals(1, accountRepository.readAccountsDirectly().size());
    }

    @Test
    public void testReadAccountDirectly() {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        when(dataBaseAdapter.readAccountDirectly(1337L)).thenReturn(account);
        assertEquals(account, accountRepository.readAccountDirectly(1337L));
    }

    @Test
    public void testDeleteAccount() {
        doNothing().when(dataBaseAdapter).deleteAccount(anyLong());
        accountRepository.deleteAccount(1337L).join();
        verify(dataBaseAdapter, times(1)).deleteAccount(1337L);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateAccountWithSuccessfulFirstBoardCall() {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        final var callback = mock(IResponseCallback.class);
        when(dataBaseAdapter.createAccountDirectly(any(Account.class))).thenReturn(account);
        doAnswer(invocation -> {
            ((ResponseCallback<List<FullBoard>>) invocation.getArgument(0))
                    .onResponse(Collections.emptyList(), IResponseCallback.EMPTY_HEADERS);
            return null;
        }).when(serverAdapter).getBoards(any());

        syncRepository.createAccount(account, callback);
        verify(dataBaseAdapter, times(1)).createAccountDirectly(account);
        verify(callback, times(1)).onResponse(account, IResponseCallback.EMPTY_HEADERS);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateAccountWithFailingFirstBoardCall() {
        final var account = new Account(1337L, "Test", "Peter", "example.com");
        final var callback = mock(IResponseCallback.class);
        when(dataBaseAdapter.createAccountDirectly(any(Account.class))).thenReturn(account);
        doAnswer(invocation -> {
            ((ResponseCallback<Capabilities>) invocation.getArgument(0))
                    .onError(new NextcloudHttpRequestFailedException(ApplicationProvider.getApplicationContext(), 404, new RuntimeException()));
            return null;
        }).when(serverAdapter).getBoards(any());

        syncRepository.createAccount(account, callback);
        verify(dataBaseAdapter, times(1)).createAccountDirectly(account);
        verify(callback, times(1)).onResponse(account, IResponseCallback.EMPTY_HEADERS);
    }
}
