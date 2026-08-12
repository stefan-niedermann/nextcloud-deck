package it.niedermann.nextcloud.deck.javafx;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.atomic.AtomicBoolean;

import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AvatarView;

public class AvatarViewInitializer implements BeforeAllCallback {

    private static final AtomicBoolean initialized = new AtomicBoolean();

    @Override
    public void beforeAll(ExtensionContext context) {
        if (initialized.compareAndSet(false, true)) {
            AvatarView.initialize(mock(GetAvatarUseCase.class));
        }
    }
}
