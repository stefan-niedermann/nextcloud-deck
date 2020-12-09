package it.niedermann.nextcloud.deck.ui.card.comments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;

@SuppressWarnings("WeakerAccess")
public class CommentsViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    private final MutableLiveData<FullDeckComment> replyToComment = new MutableLiveData<>();

    public CommentsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public void setReplyToComment(FullDeckComment replyToComment) {
        this.replyToComment.postValue(replyToComment);
    }

    public LiveData<FullDeckComment> getReplyToComment() {
        return this.replyToComment;
    }

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return syncManager.getFullCommentsForLocalCardId(localCardId);
    }

    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
        syncManager.addCommentToCard(accountId, cardId, comment);
    }

    public void updateComment(long accountId, long localCardId, long localCommentId, String comment) {
        syncManager.updateComment(accountId, localCardId, localCommentId, comment);
    }

    public WrappedLiveData<Void> deleteComment(long accountId, long localCardId, long localCommentId) {
        return syncManager.deleteComment(accountId, localCardId, localCommentId);
    }
}
