package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class BoardLabelRelationshipProvider implements IRelationshipProvider {

    private Board board;
    private List<Label> labels;

    public BoardLabelRelationshipProvider(Board board, List<Label> labels) {
        this.board = board;
        this.labels = labels;
    }

    @Override
    public void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId) {
        if (labels== null){
            return;
        }
        for (Label label : labels){
            Label existingLabel = dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, label.getId());
            dataBaseAdapter.createJoinBoardWithLabel(board.getLocalId(), existingLabel.getLocalId());
        }
    }

    @Override
    public void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId) {
        dataBaseAdapter.deleteJoinedLabelsForBoard(board.getLocalId());
    }
}
