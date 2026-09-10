package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment;

import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.usecases.comments.DeleteCommentUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "delete",
        mixinStandardHelpOptions = true,
        description = "Delete a comment")
public class CommentDeleteCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CommentDeleteCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the comment", required = true)
    Long localId;

    @Inject
    DeleteCommentUseCase deleteCommentUseCase;

    @Override
    public Integer call() {
        try {
            deleteCommentUseCase.execute(new Comment.ID(localId)).join();
            System.out.println("Comment deleted.");
            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
