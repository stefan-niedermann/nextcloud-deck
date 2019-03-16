package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class AttachmentDataProvider extends IDataProvider<Attachment> {

    private FullCard card;
    private List<Attachment> attachments;

    public AttachmentDataProvider(IDataProvider<?> parent, FullCard card, List<Attachment> attachments) {
        super(parent);
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
        attachment.setCardId(card.getCard().getLocalId());
        return dataBaseAdapter.createAttachment(accountId, attachment);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment) {
        attachment.setCardId(card.getCard().getLocalId());
        dataBaseAdapter.updateAttachment(accountId, attachment, false);

    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, Attachment attachment) {

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

    @Override
    public List<Attachment> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return null;
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, Attachment entity, Attachment response) {

    }
}
