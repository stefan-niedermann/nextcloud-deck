package it.niedermann.nextcloud.deck.ui.card.comments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;

@SuppressWarnings("WeakerAccess")
public class CommentsViewModel extends ViewModel {

    private MutableLiveData<FullDeckComment> replyToComment = new MutableLiveData<>();

    public void setReplyToComment(FullDeckComment replyToComment) {
        this.replyToComment.postValue(replyToComment);
    }

    public LiveData<FullDeckComment> getReplyToComment() {
        return this.replyToComment;
    }
}
