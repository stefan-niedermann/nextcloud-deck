package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

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
import javafx.util.Pair;

public class AvatarView extends ImageView {

    private final Logger logger = Logger.getLogger(AvatarView.class.getName());
    private static GetAvatarUseCase getAvatarUseCase;

    private final ObjectProperty<Account.ID> accountId = new SimpleObjectProperty<>(this, "accountId");
    private final ObjectProperty<User.ID> userId = new SimpleObjectProperty<>(this, "userId");

    public AvatarView() {
        Bindings.createObjectBinding(() -> new Pair<>(accountId.get(), userId.get()), accountId, userId)
                .addListener((_, _, newValue) -> loadImage(newValue.getKey(), newValue.getValue()));
    }

    public static synchronized void initialize(GetAvatarUseCase getAvatarUseCase) {
        if (AvatarView.getAvatarUseCase != null) {
            throw new IllegalStateException("Already initialized.");
        }

        AvatarView.getAvatarUseCase = getAvatarUseCase;
    }

    private void loadImage(Account.ID accountId, User.ID userId) {
        if (getAvatarUseCase == null) {
            throw new IllegalStateException("Not yet initialized.");
        }

        if (userId == null) {
            setImage(null);
            return;
        }

        final var cf = accountId == null
                ? getAvatarUseCase.execute(userId)
                : getAvatarUseCase.execute(accountId, userId);

        cf.whenCompleteAsync((inputStream, exception) -> {
            if (exception == null) {
                setImage(new Image(inputStream, true));
            } else {
                logger.log(Level.SEVERE, "Failed to load avatar for accountId=" + accountId + " and userId=" + userId, exception);
                setImage(null);
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
