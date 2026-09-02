package it.niedermann.nextcloud.deck.domain.e2e;

import org.junit.jupiter.api.AfterEach;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import it.niedermann.nextcloud.deck.domain.di.DaggerTestComponent;
import it.niedermann.nextcloud.deck.domain.di.VirtualDeviceComponent;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.ImportAccount;
import jakarta.inject.Inject;

public abstract class EndToEndTest {

    @Inject
    protected ServerManager serverManager;
    @Inject
    protected AppTokenAuthProvider authProvider;
    @Inject
    protected VirtualDeviceComponent.Factory virtualDeviceFactory;
    @Inject
    protected RandomUtil randomUtil;

    private final Map<String, VirtualDeviceComponent> virtualDevices = new HashMap<>();

    protected EndToEndTest() {
        DaggerTestComponent.create().inject(this);
    }

    @AfterEach
    public void close() {
        virtualDevices.clear();
        serverManager.close();
    }

    protected VirtualDeviceComponent createVirtualDevice() {
        final var stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = null;
        boolean foundSelf = false;
        for (final var element : stackTrace) {
            if (foundSelf) {
                if (!element.getClassName().equals(EndToEndTest.class.getName())) {
                    caller = element;
                    break;
                }
            } else if (element.getClassName().equals(EndToEndTest.class.getName()) && element.getMethodName().equals("createVirtualDevice")) {
                foundSelf = true;
            }
        }

        final String source;
        if (caller != null) {
            final var fullClassName = caller.getClassName();
            final var simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
            source = "Device_" + simpleClassName + "#" + caller.getMethodName();
        } else {
            source = "Device_Unknown";
        }
        return createVirtualDevice(randomUtil.randomize(source));
    }

    protected VirtualDeviceComponent createVirtualDevice(String deviceName) {
        return virtualDevices.computeIfAbsent(deviceName, name -> virtualDeviceFactory.create(deviceName));
    }

    protected VirtualDeviceAndAccount getOrCreateRemoteAccountAndImport(VirtualDeviceComponent virtualDevice, String remoteAccountUsername) throws IOException {
        final var user = serverManager.getOrCreateRemoteAccount(remoteAccountUsername);
        final var importAccountUseCase = virtualDevice.getImportAccountUseCase();

        final var token = authProvider.generateToken(user.url(), user.username(), user.password());
        final var account = new ImportAccount(user.url(), user.username(), token);
        final var importedAccountStatus = Flowable.fromPublisher(FlowAdapters.toPublisher(importAccountUseCase.execute(account))).lastElement().blockingGet();
        if (importedAccountStatus == null) {
            throw new IllegalStateException("ImportAccountUseCase returned no status for user: " + remoteAccountUsername);
        }
        final var importedAccount = importedAccountStatus.account();
        return new VirtualDeviceAndAccount(virtualDevice, importedAccount);
    }

    protected void synchronize(VirtualDeviceAndAccount virtualDeviceAndAccount) {
        final var virtualDevice = virtualDeviceAndAccount.virtualDevice();
        final var account = virtualDeviceAndAccount.account();
        final var scheduleSyncUseCase = virtualDevice.getScheduleSyncUseCase();
        Completable.fromPublisher(FlowAdapters.toPublisher(scheduleSyncUseCase.execute(account.id()))).blockingAwait();
    }

    public record VirtualDeviceAndAccount(VirtualDeviceComponent virtualDevice, Account account) {
    }
}
