package it.niedermann.nextcloud.deck.javafx.di.stage;


import dagger.BindsInstance;
import dagger.Subcomponent;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import javafx.stage.Stage;

@StageScope
@Subcomponent(modules = {
        StageModule.class,
})
public interface StageComponent {

    @Subcomponent.Factory
    interface Factory {
        StageComponent create(@BindsInstance Stage stage);
    }

    StageManager getStageManager();
}
