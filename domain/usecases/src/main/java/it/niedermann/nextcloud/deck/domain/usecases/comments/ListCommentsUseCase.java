package it.niedermann.nextcloud.deck.domain.usecases.comments;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import jakarta.inject.Inject;

public class ListCommentsUseCase {

    private final CommentRepository commentsRepository;

    @Inject
    public ListCommentsUseCase(
            CommentRepository commentsRepository
    ) {
        this.commentsRepository = commentsRepository;
    }

    public Flow.Publisher<List<Comment>> execute(Card.ID cardId) {
        return commentsRepository.getNotDeletedComments(cardId);
    }
}
