package it.niedermann.nextcloud.deck.javafx.di.stage;


import dagger.BindsInstance;
import dagger.Subcomponent;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.ui.stages.EditCardStage;
import it.niedermann.nextcloud.deck.javafx.ui.stages.MainStage;
import javafx.stage.Stage;

@StageScope
@Subcomponent(modules = {
        StageModule.class,
})
public interface StageComponent {

    @Subcomponent.Factory
    interface Factory {
        StageComponent create(@BindsInstance Stage stage,
                              @BindsInstance StageContext.State initialState);
    }

    MainStage getMainStage();

    EditCardStage getEditCardStage();
}
