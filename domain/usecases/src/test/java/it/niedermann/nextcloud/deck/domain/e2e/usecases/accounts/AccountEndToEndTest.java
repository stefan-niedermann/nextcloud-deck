package it.niedermann.nextcloud.deck.domain.e2e.usecases.accounts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.util.Collection;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import it.niedermann.nextcloud.deck.domain.di.VirtualDeviceComponent;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.AuthenticatedAccount;

public class AccountEndToEndTest extends EndToEndTest {

    private VirtualDeviceComponent DEVICE_A;
    private VirtualDeviceComponent DEVICE_B;

    @BeforeEach
    public void setup() throws IOException {
        DEVICE_A = createVirtualDevice();
        DEVICE_B = createVirtualDevice();
    }

    @Test
    public void testImportAccount() throws IOException {
        final var user = serverManager.getOrCreateRemoteAccount("user-import");
        final var token = authProvider.generateToken(user.url(), user.username(), user.password());
        final var authenticatedAccount = new AuthenticatedAccount(user.url(), user.username(), token);

        final var importedAccount = Flowable.fromPublisher(FlowAdapters.toPublisher(DEVICE_A.getImportAccountUseCase().execute(authenticatedAccount)))
                .lastElement()
                .blockingGet()
                .account();

        assertNotNull(importedAccount);
        assertEquals(user.username(), importedAccount.username());
    }

    @Test
    public void testGetAccount() throws IOException {
        final var userA = getOrCreateRemoteAccountAndImport(DEVICE_A, "user-a");

        final Account account = Maybe.fromPublisher(FlowAdapters.toPublisher(DEVICE_A.getGetAccountUseCase().execute(userA.account().id()))).blockingGet();

        assertNotNull(account);
        assertEquals(userA.account().id(), account.id());
    }

    @Test
    public void testGetAccounts() throws IOException {
        getOrCreateRemoteAccountAndImport(DEVICE_A, "user-a");
        getOrCreateRemoteAccountAndImport(DEVICE_A, "user-b");

        final Collection<Account> accounts = Flowable.fromPublisher(FlowAdapters.toPublisher(DEVICE_A.getGetAccountsUseCase().execute())).blockingFirst();

        assertNotNull(accounts);
        assertTrue(accounts.size() >= 2);
    }

    @Test
    public void testHasAccounts() throws IOException {
        final var hasAccountsBefore = Maybe.fromPublisher(FlowAdapters.toPublisher(DEVICE_A.getHasAccountsUseCase().execute())).blockingGet();
        assertFalse(hasAccountsBefore);

        getOrCreateRemoteAccountAndImport(DEVICE_A, "user-a");

        final var hasAccountsAfter = Maybe.fromPublisher(FlowAdapters.toPublisher(DEVICE_A.getHasAccountsUseCase().execute())).blockingGet();
        assertTrue(hasAccountsAfter);
    }

    @Test
    public void testRemoveAccount() throws IOException {
        final var userA = getOrCreateRemoteAccountAndImport(DEVICE_A, "user-a");

        DEVICE_A.getRemoveAccountUseCase().execute(userA.account().id()).join();

        final var accounts = Flowable.fromPublisher(FlowAdapters.toPublisher(DEVICE_A.getGetAccountsUseCase().execute())).blockingFirst();
        assertTrue(accounts.stream().noneMatch(a -> a.id().equals(userA.account().id())));
    }

    @Test
    public void testSetAndGetCurrentAccount() throws IOException {
        final var userA = getOrCreateRemoteAccountAndImport(DEVICE_A, "user-a");

        DEVICE_A.getSetCurrentAccountUseCase().execute(userA.account().id()).join();

        final var currentAccountId = DEVICE_A.getGetCurrentAccountUseCase().execute().join();
        assertEquals(userA.account().id(), currentAccountId);
    }

    @Test
    public void testIsolationBetweenDevices() throws IOException {
        getOrCreateRemoteAccountAndImport(DEVICE_A, "user-a");
        getOrCreateRemoteAccountAndImport(DEVICE_B, "user-b");

        final var accountsA = Flowable.fromPublisher(FlowAdapters.toPublisher(DEVICE_A.getGetAccountsUseCase().execute())).blockingFirst();
        final var accountsB = Flowable.fromPublisher(FlowAdapters.toPublisher(DEVICE_B.getGetAccountsUseCase().execute())).blockingFirst();

        assertEquals(1, accountsA.size());
        assertEquals(1, accountsB.size());
        assertTrue(accountsA.stream().anyMatch(a -> a.username().contains("user-a")));
        assertTrue(accountsB.stream().anyMatch(a -> a.username().contains("user-b")));
    }
}
