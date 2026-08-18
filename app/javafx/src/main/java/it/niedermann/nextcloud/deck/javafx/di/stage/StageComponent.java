package it.niedermann.nextcloud.deck.javafx.di.stage;


import dagger.BindsInstance;
import dagger.Subcomponent;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.EditBoardStage;
import it.niedermann.nextcloud.deck.javafx.ui.editcard.EditCardStage;
import it.niedermann.nextcloud.deck.javafx.ui.main.MainStage;
import it.niedermann.nextcloud.deck.javafx.ui.preferences.PreferencesStage;
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

    MainStage.Factory getMainStageFactory();

    EditCardStage.Factory getEditCardStageFactory();

    EditBoardStage.Factory getEditBoardStageFactory();

    PreferencesStage.Factory getPreferencesStageFactory();
}
