package it.niedermann.nextcloud.deck.javafx.ui.editboard.features;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class EditBoardFeature extends AbstractFeature {

    @FXML
    Tab detailsTab;
    @FXML
    Tab columnsTab;
    @FXML
    Tab labelsTab;
    @FXML
    Tab shareTab;

    private final EditBoardDetailsFeature.Factory detailsFeatureFactory;
    private final EditBoardColumnsFeature.Factory columnsFeatureFactory;
    private final EditBoardLabelsFeature.Factory labelsFeatureFactory;
    private final EditBoardShareFeature.Factory shareFeatureFactory;
    private final ViewModel viewModel;

    @AssistedInject
    public EditBoardFeature(Inflater inflater,
                            EditBoardDetailsFeature.Factory detailsFeatureFactory,
                            EditBoardColumnsFeature.Factory columnsFeatureFactory,
                            EditBoardLabelsFeature.Factory labelsFeatureFactory,
                            EditBoardShareFeature.Factory shareFeatureFactory,
                            @Assisted ViewModel viewModel) {
        super(inflater);

        this.detailsFeatureFactory = detailsFeatureFactory;
        this.columnsFeatureFactory = columnsFeatureFactory;
        this.labelsFeatureFactory = labelsFeatureFactory;
        this.shareFeatureFactory = shareFeatureFactory;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        detailsTab.setContent(detailsFeatureFactory.create(viewModel).getRoot());
        columnsTab.setContent(columnsFeatureFactory.create(viewModel).getRoot());
        labelsTab.setContent(labelsFeatureFactory.create(viewModel).getRoot());
        shareTab.setContent(shareFeatureFactory.create(viewModel).getRoot());
    }

    public interface ViewModel extends
            EditBoardDetailsFeature.ViewModel,
            EditBoardColumnsFeature.ViewModel,
            EditBoardLabelsFeature.ViewModel,
            EditBoardShareFeature.ViewModel {
        Flowable<Board> getBoard();
    }
}
