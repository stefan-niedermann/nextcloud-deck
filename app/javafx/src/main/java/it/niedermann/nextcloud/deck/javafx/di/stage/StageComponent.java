package it.niedermann.nextcloud.deck.javafx.di.stage;


import dagger.BindsInstance;
import dagger.Subcomponent;
import it.niedermann.nextcloud.deck.javafx.ui.controller.stages.EditCardStageController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.stages.MainStageController;
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

    MainStageController getMainStageController();

    EditCardStageController getEditCardStageController();
}
