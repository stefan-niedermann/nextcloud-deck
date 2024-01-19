package it.niedermann.nextcloud.deck.ui.card.comments;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;

@SuppressWarnings("WeakerAccess")
public class CommentsViewModel extends SyncViewModel {

    private final MutableLiveData<FullDeckComment> replyToComment = new MutableLiveData<>();

    public CommentsViewModel(@NonNull Application application, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        super(application, account);
    }

    public void setReplyToComment(FullDeckComment replyToComment) {
        this.replyToComment.postValue(replyToComment);
    }

    public LiveData<FullDeckComment> getReplyToComment() {
        return this.replyToComment;
    }

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return distinctUntilChanged(baseRepository.getFullCommentsForLocalCardId(localCardId));
    }

    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
        syncRepository.addCommentToCard(accountId, cardId, comment);
    }

    public void updateComment(long accountId, long localCardId, long localCommentId, String comment) {
        syncRepository.updateComment(accountId, localCardId, localCommentId, comment);
    }

    public void deleteComment(long accountId, long localCardId, long localCommentId, @NonNull IResponseCallback<EmptyResponse> callback) {
        syncRepository.deleteComment(accountId, localCardId, localCommentId, callback);
    }
}
