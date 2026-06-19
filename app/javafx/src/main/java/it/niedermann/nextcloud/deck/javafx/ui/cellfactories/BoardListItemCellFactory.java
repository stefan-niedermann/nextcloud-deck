package it.niedermann.nextcloud.deck.javafx.ui.cellfactories;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.BoardListItemView;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class BoardListItemCellFactory implements Callback<ListView<Board>, ListCell<Board>> {

    private BoardListItemView.BoardListItemActionListener boardListItemActionListener;

    @Inject
    public BoardListItemCellFactory() {
    }

    public void setBoardListItemActionListener(BoardListItemView.BoardListItemActionListener boardListItemActionListener) {
        this.boardListItemActionListener = boardListItemActionListener;
    }

    @Override
    public ListCell<Board> call(ListView<Board> listView) {
        return new ListCell<>() {

            final BoardListItemView view = new BoardListItemView();

            {
                final var totalWidth = Bindings.createDoubleBinding(
                        () -> listView.getWidth()
                              - getPadding().getLeft()
                              - getPadding().getRight()
                              // FIXME This magic number is probably needed for some border, otherwise the items cause overflow
                              - 2,
                        listView.widthProperty(),
                        paddingProperty());

                view.maxWidthProperty().bind(totalWidth);
            }

            @Override
            protected void updateItem(Board board, boolean empty) {
                super.updateItem(board, empty);
                setText(null);

                if (empty) {

                    setGraphic(null);

                } else {

                    view.bind(board, boardListItemActionListener);
                    setGraphic(view);

                }
            }
        };
    }
}
