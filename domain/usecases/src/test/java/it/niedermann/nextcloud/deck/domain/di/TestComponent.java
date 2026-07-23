package it.niedermann.nextcloud.deck.domain.di;


import dagger.Component;
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {
        TestModule.class,
        SharedModule.class,
})
public interface TestComponent {

    @Component.Factory
    interface Factory {
        TestComponent create();
    }

    void inject(EndToEndTest test);
}
