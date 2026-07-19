package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import org.kordamp.ikonli.javafx.FontIcon;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class AvatarView extends ImageView {

    private final Logger logger = Logger.getLogger(AvatarView.class.getName());

    private static GetAvatarUseCase getAvatarUseCase;

    private final ObjectProperty<Account.ID> accountId = new SimpleObjectProperty<>(this, "accountId");
    private final ObjectProperty<User.ID> userId = new SimpleObjectProperty<>(this, "userId");

    public AvatarView() {
        setFitWidth(24);
        setFitHeight(24);
        setPreserveRatio(true);

        final var clip = new Rectangle();
        clip.widthProperty().bind(fitWidthProperty());
        clip.heightProperty().bind(fitHeightProperty());
        clip.arcWidthProperty().bind(fitWidthProperty());
        clip.arcHeightProperty().bind(fitHeightProperty());
        setClip(clip);

        Bindings.createObjectBinding(() -> new AccountIdUserIdSize(accountId.get(), userId.get(), fitWidthProperty().get()), accountId, userId, fitWidthProperty())
                .addListener((_, _, newValue) -> loadImage(newValue.accountId(), newValue.userId(), newValue.size()));
    }

    private record AccountIdUserIdSize(Account.ID accountId, User.ID userId, double size) {
    }

    public static synchronized void initialize(GetAvatarUseCase getAvatarUseCase) {
        if (AvatarView.getAvatarUseCase != null) {
            throw new IllegalStateException("Already initialized.");
        }

        AvatarView.getAvatarUseCase = getAvatarUseCase;
    }

    private void loadImage(Account.ID accountId, User.ID userId, double sizeInPx) {
        if (getAvatarUseCase == null) {
            throw new IllegalStateException("Not yet initialized.");
        }

        if (userId == null) {
            setImage(null);
            return;
        }

        final var cf = accountId == null
                ? getAvatarUseCase.execute(userId, (int) sizeInPx)
                : getAvatarUseCase.execute(accountId, userId, (int) sizeInPx);

        cf.whenCompleteAsync((inputStream, exception) -> {
            if (exception == null) {
                setImage(new Image(inputStream, true));
            } else {
                logger.log(Level.SEVERE, "Failed to load avatar for accountId=" + accountId + " and userId=" + userId, exception);
                final var icon = new FontIcon("fltral-image-off-24");
                icon.setIconSize((int) sizeInPx);
                setImage(icon.snapshot(null, null));
            }
        }, JavaFxScheduler.platform().toExecutorService());
    }

    public void setAvatar(Account account) {
        setAccountId(account.id());
        setUserId(new User.ID(account.username()));
    }

    public void setAvatar(User user) {
        setUserId(user.id());
    }

    public void setAvatar(User.ID userId) {
        setUserId(userId);
    }

    public User.ID getUserId() {
        return userId.get();
    }

    public void setUserId(User.ID user) {
        this.userId.set(user);
    }

    public ObjectProperty<User.ID> userIdProperty() {
        return userId;
    }

    public Account.ID getAccountId() {
        return accountId.get();
    }

    public void setAccountId(Account.ID user) {
        this.accountId.set(user);
    }

    public ObjectProperty<Account.ID> accountIdProperty() {
        return accountId;
    }
}
