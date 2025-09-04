package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.DeckCommentsDataProvider;
import okhttp3.Headers;

public class CommentRepository extends AbstractRepository {

    public CommentRepository(@NonNull Context context) {
        super(context);
    }

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return dataBaseAdapter.getFullCommentsForLocalCardId(localCardId);
    }

    @AnyThread
    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, cardId);
            OcsComment commentEntity = OcsComment.of(comment);
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).createEntity(new DeckCommentsDataProvider(null, card), commentEntity, new ResponseCallback<>(account) {
                @Override
                public void onResponse(OcsComment response, Headers headers) {
                    // nothing so far
                }
            });
        });
    }

    @AnyThread
    public void updateComment(long accountId, long localCardId, long localCommentId, String comment) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId);
            DeckComment entity = dataBaseAdapter.getCommentByLocalIdDirectly(accountId, localCommentId);
            entity.setMessage(comment);
            OcsComment commentEntity = OcsComment.of(entity);
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).updateEntity(new DeckCommentsDataProvider(null, card), commentEntity, new ResponseCallback<>(account) {
                @Override
                public void onResponse(OcsComment response, Headers headers) {
                    // nothing so far
                }
            });
        });
    }

    @AnyThread
    public void deleteComment(long accountId, long localCardId, long localCommentId, @NonNull IResponseCallback<EmptyResponse> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            final Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            final Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId);
            final DeckComment entity = dataBaseAdapter.getCommentByLocalIdDirectly(accountId, localCommentId);
            final OcsComment commentEntity = OcsComment.of(entity);
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).deleteEntity(new DeckCommentsDataProvider(null, card),
                    commentEntity, ResponseCallback.from(account, callback));
        });
    }
}