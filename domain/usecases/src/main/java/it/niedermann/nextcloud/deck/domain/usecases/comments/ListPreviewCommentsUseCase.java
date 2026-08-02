package it.niedermann.nextcloud.deck.domain.usecases.comments;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewComment;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import jakarta.inject.Inject;

public class ListPreviewCommentsUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public ListPreviewCommentsUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Flow.Publisher<List<PreviewComment>> execute(Card.ID cardId) {
        return commentRepository.getNotDeletedCommentPreviews(cardId);
    }
}
