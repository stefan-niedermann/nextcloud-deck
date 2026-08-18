package it.niedermann.nextcloud.deck.javafx.ui.editboard.features;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class EditBoardDetailsFeature extends AbstractScene {

    @FXML
    TextField title;
    @FXML
    ColorPicker color;
    @FXML
    Label editedAt;

    private final ViewModel viewModel;

    @AssistedInject
    public EditBoardDetailsFeature(@Assisted ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardDetailsFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var disposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    title.setText(board.title());
                    color.setValue(Color.rgb(board.color().getRed(), board.color().getGreen(), board.color().getBlue()));
                    editedAt.setText(board.lastModified().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

                    final boolean disable = !board.permissions().permissionManage();
                    title.setDisable(disable);
                    color.setDisable(disable);
                });

        addDisposable(disposable);
    }

    public interface ViewModel {
        Flowable<Board> getBoard();
    }
}
