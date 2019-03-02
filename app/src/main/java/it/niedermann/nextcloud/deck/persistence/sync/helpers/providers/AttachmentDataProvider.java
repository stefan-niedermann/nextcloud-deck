package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class AttachmentDataProvider implements IDataProvider<Attachment> {

    private FullCard card;
    private List<Attachment> attachments;

    public AttachmentDataProvider(FullCard card, List<Attachment> attachments) {
        this.card = card;
        this.attachments = attachments;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Attachment>> responder) {
        responder.onResponse(attachments);
    }

    @Override
    public Attachment getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment entity) {
        return dataBaseAdapter.getAttachmentByRemoteIdDirectly(accountId, entity.getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment) {
        attachment.setCardId(card.getCard().getLocalId());
        return dataBaseAdapter.createAttachment(accountId, attachment);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment) {
        attachment.setCardId(card.getCard().getLocalId());
        dataBaseAdapter.updateAttachment(accountId, attachment);

    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment) {

    }

    @Override
    public void goDeeper(SyncHelper syncHelper, Attachment existingEntity, Attachment entityFromServer) {

    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Attachment> responder, Attachment entity) {

    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Attachment> callback, Attachment entity) {

    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Attachment> callback, Attachment entity) {

    }
}
