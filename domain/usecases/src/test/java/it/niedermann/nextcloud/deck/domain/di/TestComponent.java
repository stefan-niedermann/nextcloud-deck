package it.niedermann.nextcloud.deck.domain.di;


import dagger.Component;
import it.niedermann.nextcloud.deck.app.shared.di.modules.MapperModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.RemoteModule;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {
        TestModule.class,
        TestInfrastructureModule.class,
        MapperModule.class,
        RemoteModule.class,
})
public interface TestComponent {

    @Component.Factory
    interface Factory {
        TestComponent create();
    }

    void inject(EndToEndTest test);

    VirtualDeviceComponent.Factory useCaseComponentFactory();
}
