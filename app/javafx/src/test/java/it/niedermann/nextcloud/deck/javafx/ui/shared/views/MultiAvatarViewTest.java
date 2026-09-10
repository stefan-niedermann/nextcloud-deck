package it.niedermann.nextcloud.deck.javafx.ui.shared.views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;

import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.javafx.AvatarViewInitializer;
import javafx.scene.Scene;
import javafx.stage.Stage;

@ExtendWith({ApplicationExtension.class, AvatarViewInitializer.class})
public class MultiAvatarViewTest {

    private MultiAvatarView multiAvatarView;

    @Start
    public void start(Stage stage) {
        multiAvatarView = new MultiAvatarView();
        stage.setScene(new Scene(multiAvatarView));
        stage.show();
    }

    @Test
    public void testBind(FxRobot robot) {
        final var userIds = List.of(
                new User.ID("user1"),
                new User.ID("user2"),
                new User.ID("user3")
        );

        robot.interact(() -> multiAvatarView.bind(userIds));

        assertEquals(3, multiAvatarView.getChildren().size());
        for (int i = 0; i < 3; i++) {
            final var node = multiAvatarView.getChildren().get(i);
            assertTrue(node instanceof AvatarView);
            final var avatarView = (AvatarView) node;
            assertEquals(24.0, avatarView.getFitWidth());
        }

        // Spacing should be -24 * 0.3 = -7.2
        assertEquals(-7.2, multiAvatarView.getSpacing(), 0.001);
    }

    @Test
    public void testResize(FxRobot robot) {
        robot.interact(() -> multiAvatarView.setAvatarSize(40.0));
        assertEquals(-12.0, multiAvatarView.getSpacing(), 0.001);

        robot.interact(() -> multiAvatarView.bind(List.of(new User.ID("user1"))));
        final var avatarView = (AvatarView) multiAvatarView.getChildren().get(0);
        assertEquals(40.0, avatarView.getFitWidth());
    }

    @Test
    public void testResizeExistingChildren(FxRobot robot) {
        robot.interact(() -> multiAvatarView.bind(List.of(new User.ID("user1"))));
        final var avatarView = (AvatarView) multiAvatarView.getChildren().get(0);
        assertEquals(24.0, avatarView.getFitWidth());

        robot.interact(() -> multiAvatarView.setAvatarSize(40.0));
        assertEquals(40.0, avatarView.getFitWidth());
    }
}
