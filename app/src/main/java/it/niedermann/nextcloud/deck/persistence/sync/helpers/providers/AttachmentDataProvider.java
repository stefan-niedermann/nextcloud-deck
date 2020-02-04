package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import android.net.Uri;

import java.io.File;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class AttachmentDataProvider extends AbstractSyncDataProvider<Attachment> {

    private FullCard card;
    private Board board;
    private Stack stack;
    private List<Attachment> attachments;

    public AttachmentDataProvider(AbstractSyncDataProvider<?> parent, Board board, Stack stack, FullCard card, List<Attachment> attachments) {
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
        serverAdapter.uploadAttachment(board.getId(), stack.getId(), card.getId(), entity.getType(), new File(entity.getLocalPath()), responder);
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

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<Attachment> entitiesFromServer) {
        List<Attachment> localAttachments = dataBaseAdapter.getAttachmentsForLocalCardIdDirectly(accountId, card.getLocalId());
        List<Attachment> delta = findDelta(entitiesFromServer, localAttachments);
        for (Attachment attachment : delta) {
            dataBaseAdapter.deleteAttachment(accountId, attachment, false);
        }
    }
}
