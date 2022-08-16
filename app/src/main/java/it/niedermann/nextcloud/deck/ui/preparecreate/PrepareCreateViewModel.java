package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

@SuppressWarnings("WeakerAccess")
public class PrepareCreateViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public PrepareCreateViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public void saveCard(long accountId, long boardLocalId, long stackLocalId, @NonNull FullCard fullCard, @NonNull IResponseCallback<FullCard> callback) {
        syncManager.createFullCard(accountId, boardLocalId, stackLocalId, fullCard, callback);
    }

    @SuppressWarnings("ConstantConditions")
    public FullCard createFullCard(@NonNull Version version, @Nullable String subject, @Nullable String title, @Nullable String description) {
        if (TextUtils.isEmpty(subject) && TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
            throw new IllegalArgumentException("Subject, title or description must not be empty.");
        }
        if (TextUtils.isEmpty(subject)) {
            if (TextUtils.isEmpty(title)) {
                return createFullCard(version, description);
            } else if (TextUtils.isEmpty(description)) {
                return createFullCard(version, title);
            } else {
                return createFullCard(version, title, description);
            }
        } else if (TextUtils.isEmpty(title)) {
            if (TextUtils.isEmpty(subject)) {
                return createFullCard(version, description);
            } else if (TextUtils.isEmpty(description)) {
                return createFullCard(version, subject);
            } else {
                return createFullCard(version, subject, description);
            }
        } else if (TextUtils.isEmpty(description)) {
            if (TextUtils.isEmpty(subject)) {
                return createFullCard(version, title);
            } else if (TextUtils.isEmpty(title)) {
                return createFullCard(version, description);
            } else {
                return createFullCard(version, subject, title);
            }
        } else {
            return createFullCard(version, subject, title.trim() + "\n\n" + description.trim());
        }
    }

    public FullCard createFullCard(@NonNull Version version, @NonNull String title, @NonNull String description) {
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
            final var fullCard = new FullCard();
            final var card = new Card();
            final int maxLength = version.getCardTitleMaxLength();
            if (title.length() > maxLength) {
                card.setTitle(title.substring(0, maxLength));
                card.setDescription(title.substring(maxLength).trim() + "\n\n" + description);
            } else {
                card.setTitle(title);
                card.setDescription(description);
            }
            fullCard.setCard(card);
            return fullCard;
        } else if (!TextUtils.isEmpty(title)) {
            return createFullCard(version, title);
        } else if (!TextUtils.isEmpty(description)) {
            return createFullCard(version, description);
        } else {
            throw new IllegalArgumentException("Title or description must not be empty.");
        }
    }

    public FullCard createFullCard(@NonNull Version version, @NonNull String content) {
        if (TextUtils.isEmpty(content)) {
            throw new IllegalArgumentException("Content must not be empty.");
        }
        final var fullCard = new FullCard();
        final var card = new Card();
        final int maxLength = version.getCardTitleMaxLength();
        if (content.length() > maxLength) {
            card.setTitle(content.substring(0, maxLength).trim());
            card.setDescription(content.substring(maxLength).trim());
        } else {
            card.setTitle(content);
            card.setDescription(null);
        }
        fullCard.setCard(card);
        return fullCard;
    }
}
