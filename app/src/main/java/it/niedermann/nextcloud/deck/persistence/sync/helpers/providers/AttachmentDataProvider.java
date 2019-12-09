package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import android.net.Uri;

import java.io.File;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class AttachmentDataProvider extends AbstractSyncDataProvider<Attachment> {

    private Card card;
    private Board board;
    private Stack stack;
    private List<Attachment> attachments;

    public AttachmentDataProvider(AbstractSyncDataProvider<?> parent, Board board, Stack stack, Card card, List<Attachment> attachments) {
        super(parent);
        this.board = board;
        this.stack = stack;
        this.card = card;
        this.attachments = attachments;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Attachment>> responder, Date lastSync) {
        responder.onResponse(attachments);
    }

    @Override
    public Attachment getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment entity) {
        return dataBaseAdapter.getAttachmentByRemoteIdDirectly(accountId, entity.getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment) {
        attachment.setCardId(card.getLocalId());
        return dataBaseAdapter.createAttachment(accountId, attachment);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment, boolean setStatus) {
        attachment.setCardId(card.getLocalId());
        dataBaseAdapter.updateAttachment(accountId, attachment, setStatus);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }


    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment) {
        dataBaseAdapter.deleteAttachment(accountId, attachment, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Attachment> responder, Attachment entity) {
        Uri uri = Uri.fromFile(new File(entity.getLocalPath()));
        String type = Attachment.getMimetypeForUri(dataBaseAdapter.getContext(), uri);
        serverAdapter.uploadAttachment(board.getId(), stack.getId(), card.getId(), type, uri, responder);
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Attachment> callback, Attachment entity) {
        Uri uri = Uri.fromFile(new File(entity.getLocalPath()));
        String type = Attachment.getMimetypeForUri(dataBaseAdapter.getContext(), uri);
        serverAdapter.updateAttachment(board.getId(), stack.getId(), card.getId(), entity.getId(), type, uri, callback);

    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, Attachment entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteAttachment(board.getId(), stack.getId(), card.getId(), entity.getId(), callback);
    }

    @Override
    public List<Attachment> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return dataBaseAdapter.getLocallyChangedAttachmentsByLocalCardIdDirectly(accountId, card.getLocalId());
    }
}
