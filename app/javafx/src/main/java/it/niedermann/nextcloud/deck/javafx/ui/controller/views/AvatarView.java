package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.processors.BehaviorProcessor;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Avatar;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class AvatarView extends ImageView {

    private final Logger logger = Logger.getLogger(AvatarView.class.getName());

    private static GetAvatarUseCase getAvatarUseCase;

    private final BehaviorProcessor<Request> requestProcessor = BehaviorProcessor.createDefault(new Request(null, null, null));
    private final BehaviorProcessor<Double> sizeProcessor = BehaviorProcessor.createDefault(24.0);

    public AvatarView() {
        setFitWidth(24);
        setFitHeight(24);
        setPreserveRatio(true);

        final var placeholderIcon = new FontIcon("fltfal-arrow-sync-20");
        placeholderIcon.setIconSize(24);
        final var image = placeholderIcon.snapshot(new SnapshotParameters(), null);
        setImage(image);

        final var clip = new Rectangle();
        clip.widthProperty().bind(fitWidthProperty());
        clip.heightProperty().bind(fitHeightProperty());
        clip.arcWidthProperty().bind(fitWidthProperty());
        clip.arcHeightProperty().bind(fitHeightProperty());
        setClip(clip);

        sizeProcessor.onNext(getFitWidth());
        fitWidthProperty().addListener((_, _, newValue) -> sizeProcessor.onNext(newValue.doubleValue()));

        Flowable.combineLatest(
                requestProcessor,
                sizeProcessor,
                RequestSize::new
        ).subscribe(newValue -> loadImage(newValue.request(), newValue.size()));
    }

    private record Request(Account account, User.ID userId, CompletableFuture<Void> onLoaded) {
    }

    private record RequestSize(Request request, double size) {
    }

    public static synchronized void initialize(GetAvatarUseCase getAvatarUseCase) {
        if (AvatarView.getAvatarUseCase != null) {
            throw new IllegalStateException("Already initialized.");
        }

        AvatarView.getAvatarUseCase = getAvatarUseCase;
    }

    private void loadImage(Request request, double sizeInPx) {
        if (getAvatarUseCase == null) {
            throw new IllegalStateException("Not yet initialized.");
        }

        final var account = request.account();
        final var userId = request.userId();

        if (account == null && userId == null) {
            setImage(null);
            if (request.onLoaded() != null) {
                request.onLoaded().complete(null);
            }
            return;
        }

        final CompletableFuture<Avatar> cf;
        if (account != null && userId != null) {
            cf = getAvatarUseCase.execute(account, userId, (int) sizeInPx);
        } else if (account != null) {
            cf = getAvatarUseCase.execute(account, (int) sizeInPx);
        } else {
            cf = getAvatarUseCase.execute(userId, (int) sizeInPx);
        }

        cf.whenCompleteAsync((avatar, exception) -> {
            if (exception == null) {
                setImage(new Image(new ByteArrayInputStream(avatar.content()), true));
                if (request.onLoaded() != null) {
                    request.onLoaded().complete(null);
                }
            } else {
                logger.log(Level.SEVERE, "Failed to load avatar for account=" + account + " and user=" + userId, exception);
                final var icon = new FontIcon("fltral-image-off-24");
                icon.setIconSize((int) sizeInPx);
                setImage(icon.snapshot(null, null));
                if (request.onLoaded() != null) {
                    request.onLoaded().completeExceptionally(exception);
                }
            }
        }, JavaFxScheduler.platform().toExecutorService());
    }

    private CompletableFuture<Void> update(Account account, User.ID userId) {
        final var onLoaded = new CompletableFuture<Void>();
        requestProcessor.onNext(new Request(account, userId, onLoaded));
        return onLoaded;
    }

    public CompletableFuture<Void> setAvatar(Account account, User.ID userId) {
        return update(account, userId);
    }

    public CompletableFuture<Void> setAvatar(Account account) {
        return update(account, null);
    }

    public CompletableFuture<Void> setAvatar(User.ID userId) {
        return update(null, userId);
    }
}
