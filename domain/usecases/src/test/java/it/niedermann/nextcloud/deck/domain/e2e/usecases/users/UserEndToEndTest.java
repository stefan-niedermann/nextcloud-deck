package it.niedermann.nextcloud.deck.domain.e2e.usecases.users;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.util.Collection;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.model.User;

public class UserEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
    }

    @Test
    public void testSearchUser() {
        final var searchUserUseCase = DEVICE_A.virtualDevice().getSearchUserUseCase();

        final Collection<User> users = Flowable.fromPublisher(FlowAdapters.toPublisher(searchUserUseCase.execute("johndoe")))
                .blockingFirst();

        Assertions.assertNotNull(users);
    }
}
