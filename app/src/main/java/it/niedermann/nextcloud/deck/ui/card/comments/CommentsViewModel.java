package it.niedermann.nextcloud.deck.ui.card.comments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;

public class CommentsViewModel extends ViewModel {

    private MutableLiveData<DeckComment> replyToComment = new MutableLiveData<>();

    public void setReplyToComment(DeckComment replyToComment) {
        this.replyToComment.postValue(replyToComment);
    }

    public LiveData<DeckComment> getReplyToComment() {
        return this.replyToComment;
    }
}
