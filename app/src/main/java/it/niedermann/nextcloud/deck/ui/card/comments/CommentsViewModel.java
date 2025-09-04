package it.niedermann.nextcloud.deck.ui.card.comments;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;

import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.CommentRepository;

@SuppressWarnings("WeakerAccess")
public class CommentsViewModel extends AndroidViewModel {

    private final CommentRepository commentRepository;

    private final MutableLiveData<FullDeckComment> replyToComment = new MutableLiveData<>();

    public CommentsViewModel(@NonNull Application application) {
        super(application);
        this.commentRepository = new CommentRepository(application);
    }

    public void setReplyToComment(FullDeckComment replyToComment) {
        this.replyToComment.postValue(replyToComment);
    }

    public LiveData<FullDeckComment> getReplyToComment() {
        return this.replyToComment;
    }

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return distinctUntilChanged(commentRepository.getFullCommentsForLocalCardId(localCardId));
    }

    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
        commentRepository.addCommentToCard(accountId, cardId, comment);
    }

    public void updateComment(long accountId, long localCardId, long localCommentId, String comment) {
        commentRepository.updateComment(accountId, localCardId, localCommentId, comment);
    }

    public void deleteComment(long accountId, long localCardId, long localCommentId, @NonNull IResponseCallback<EmptyResponse> callback) {
        commentRepository.deleteComment(accountId, localCardId, localCommentId, callback);
    }
}
