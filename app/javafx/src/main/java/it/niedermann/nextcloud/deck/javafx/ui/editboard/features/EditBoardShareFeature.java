package it.niedermann.nextcloud.deck.javafx.ui.editboard.features;

import com.dlsc.gemsfx.SearchField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.BoardShare;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.UserSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders.UserSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.UserChip;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class EditBoardShareFeature extends AbstractFeature {

    @FXML
    SearchField<User> userSearch;
    @FXML
    ListView<BoardShare> shares;

    private Board board;
    private final UserSuggestionProvider userSuggestionProvider;
    private final UserSearchViewConverter userSearchViewConverter;
    private final ViewModel viewModel;

    @AssistedInject
    public EditBoardShareFeature(Inflater inflater,
                                 UserSuggestionProvider userSuggestionProvider,
                                 UserSearchViewConverter userSearchViewConverter,
                                 @Assisted ViewModel viewModel) {
        super(inflater);

        this.userSuggestionProvider = userSuggestionProvider;
        this.userSearchViewConverter = userSearchViewConverter;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardShareFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        userSearch.setSuggestionProvider(userSuggestionProvider);
        userSearch.setConverter(userSearchViewConverter);
        userSearch.setSelectedItem(null);
        userSearch.selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                viewModel.onAddShare(newValue);
                userSearch.setSelectedItem(null);
            }
        });

        shares.setCellFactory(_ -> new BoardShareListCell());

        final var sharesDisposable = viewModel.getShares()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(list -> shares.getItems().setAll(list));

        final var permissionsDisposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    this.board = board;
                    final boolean disable = !board.permissions().permissionShare();
                    userSearch.setDisable(disable);
                    shares.refresh();
                });

        addDisposable(sharesDisposable, permissionsDisposable);
    }

    private class BoardShareListCell extends ListCell<BoardShare> {
        private final HBox root = new HBox(10);
        private final UserChip userChip = new UserChip();
        private final CheckBox read = new CheckBox("Read");
        private final CheckBox edit = new CheckBox("Edit");
        private final CheckBox manage = new CheckBox("Manage");
        private final CheckBox share = new CheckBox("Share");
        private final Button removeButton = new Button("Remove");

        public BoardShareListCell() {
            final var spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            root.getChildren().addAll(userChip, spacer, read, edit, manage, share, removeButton);

            read.setOnAction(_ -> updatePermissions());
            edit.setOnAction(_ -> updatePermissions());
            manage.setOnAction(_ -> updatePermissions());
            share.setOnAction(_ -> updatePermissions());

            removeButton.setOnAction(_ -> {
                if (getItem() != null) {
                    viewModel.onRemoveShare(getItem());
                }
            });
        }

        private void updatePermissions() {
            if (getItem() != null) {
                final var permissions = new Board.Permissions(read.isSelected(), edit.isSelected(), manage.isSelected(), share.isSelected());
                viewModel.onUpdateShare(getItem(), permissions);
            }
        }

        @Override
        protected void updateItem(BoardShare item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || board == null) {
                setGraphic(null);
            } else {
                final boolean disable = !board.permissions().permissionShare();

                userChip.bind(item.user(), userSearchViewConverter);
                read.setSelected(item.permissions().permissionRead());
                edit.setSelected(item.permissions().permissionEdit());
                manage.setSelected(item.permissions().permissionManage());
                share.setSelected(item.permissions().permissionShare());

                read.setDisable(disable);
                edit.setDisable(disable);
                manage.setDisable(disable);
                share.setDisable(disable);
                removeButton.setDisable(disable);

                setGraphic(root);
            }
        }
    }

    public interface ViewModel {
        Flowable<Board> getBoard();
        Flowable<List<BoardShare>> getShares();
        void onAddShare(User user);
        void onRemoveShare(BoardShare share);
        void onUpdateShare(BoardShare share, Board.Permissions permissions);
    }
}
