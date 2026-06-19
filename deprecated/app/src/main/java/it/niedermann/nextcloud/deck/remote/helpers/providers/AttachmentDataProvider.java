package it.niedermann.nextcloud.deck.remote.helpers.providers;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import okhttp3.Headers;

public class AttachmentDataProvider extends AbstractSyncDataProvider<Attachment> {

    private final FullCard card;
    private final Board board;
    private final Stack stack;
    private final List<Attachment> attachments;

    public AttachmentDataProvider(AbstractSyncDataProvider<?> parent, Board board, Stack stack, FullCard card, List<Attachment> attachments) {
        super(parent);
        this.board = board;
        this.stack = stack;
        this.card = card;
        this.attachments = attachments;
    }

    @Override
    public void onInsertFailed(DataBaseAdapter dataBaseAdapter, RuntimeException cause, Account account, long accountId, List<Attachment> response, Attachment entityFromServer) {
        Account foundAccount = dataBaseAdapter.getAccountByIdDirectly(accountId);
        Card foundCard = dataBaseAdapter.getCardByLocalIdDirectly(accountId, entityFromServer.getCardId());
        List<Long> accountIDs = dataBaseAdapter.getAllAccountsDirectly().stream().map(Account::getId).collect(Collectors.toList());
        List<Long> allCardIDs = dataBaseAdapter.getAllCardIDs();
        throw new RuntimeException("Error creating Attachment.\n" +
                "AccountID: "+accountId+" (parent-DataProvider gave CardID: "+card.getLocalId()+" in account "+card.getAccountId()+") (existing: "+(foundAccount != null)+")\n" +
                "cardID: "+entityFromServer.getCardId()+" (existing: "+(foundCard != null)+")\n" +
                "all existing account-IDs: "+accountIDs + "\n" +
                "all existing card-IDs: "+allCardIDs, cause);
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<List<Attachment>> responder, Instant lastSync) {
        responder.onResponse(attachments, IResponseCallback.EMPTY_HEADERS);
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
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<Attachment> responder, Attachment entity) {
        File file = new File(entity.getLocalPath());
        serverAdapter.uploadAttachment(board.getId(), stack.getId(), card.getId(), file, new ResponseCallback<>(responder.getAccount()) {
            @Override
            public void onResponse(Attachment response, Headers headers) {
                if (file.delete()) {
                    responder.onResponse(response, headers);
                } else {
                    responder.onError(new IOException("Could not delete local file after successful upload: " + file.getAbsolutePath()));
                }
            }

            @SuppressLint("MissingSuperCall")
            @Override
            public void onError(Throwable throwable) {
                if (!file.delete()) {
                    DeckLog.error("Could not delete local file:", file.getAbsolutePath());
                }
                // if (HandledServerErrors.ATTACHMENTS_FILE_ALREADY_EXISTS == HandledServerErrors.fromThrowable(throwable)) {
                dataBaseAdapter.deleteAttachment(accountId, entity, false);
                // }
                responder.onError(throwable);
            }
        });
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<Attachment> callback, Attachment entity) {
        Uri uri = Uri.fromFile(new File(entity.getLocalPath()));
        String type = dataBaseAdapter.getContext().getContentResolver().getType(uri);
        serverAdapter.updateAttachment(board.getId(), stack.getId(), card.getId(), entity.getId(), type, uri, callback);

    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<EmptyResponse> callback, Attachment entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteAttachment(board.getId(), stack.getId(), card.getId(), entity, callback);
    }

    @Override
    public List<Attachment> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return dataBaseAdapter.getLocallyChangedAttachmentsByLocalCardIdDirectly(accountId, card.getLocalId());
    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<Attachment> entitiesFromServer) {
        List<Attachment> localAttachments = dataBaseAdapter.getAttachmentsForLocalCardIdDirectly(accountId, card.getLocalId());
        List<Attachment> delta = findDelta(entitiesFromServer, localAttachments);
        for (Attachment attachment : delta) {
            if (attachment.getId() == null) {
                // not pushed up yet so:
                continue;
            }
            dataBaseAdapter.deleteAttachment(accountId, attachment, false);
        }
        for (Attachment attachment : entitiesFromServer) {
            if (attachment.getDeletedAt() != null && attachment.getDeletedAt().toEpochMilli() != 0) {
                Attachment toDelete = dataBaseAdapter.getAttachmentByRemoteIdDirectly(accountId, attachment.getId());
                if (toDelete != null) {
                    dataBaseAdapter.deleteAttachment(accountId, toDelete, false);
                }
            }
        }
    }
}
