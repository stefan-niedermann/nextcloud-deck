package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment;

import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.usecases.comments.UpdateCommentUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "update",
        mixinStandardHelpOptions = true,
        description = "Update a comment")
public class CommentUpdateCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CommentUpdateCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the comment", required = true)
    Long localId;

    @Option(names = {"-m", "--message"}, description = "New comment message", required = true)
    String message;

    @Inject
    UpdateCommentUseCase updateCommentUseCase;

    @Override
    public Integer call() {
        try {
            updateCommentUseCase.execute(new Comment.ID(localId), message).join();
            System.out.println("Comment updated.");
            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
