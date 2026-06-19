package it.niedermann.nextcloud.deck.ui.card;

import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import org.jetbrains.annotations.Contract;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.view.DueDateChip;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;
import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;

public abstract class AbstractCardViewHolder extends RecyclerView.ViewHolder {

    public AbstractCardViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * Removes all {@link OnClickListener} and {@link OnLongClickListener}
     */
    @CallSuper
    public void bind(@NonNull FullCard fullCard, @NonNull Account account, @Nullable Long boardRemoteId, boolean hasEditPermission, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener, @NonNull String counterMaxValue, @Nullable ThemeUtils utils) {
        final var context = itemView.getContext();

        bindCardClickListener(null);
        bindCardLongClickListener(null);

        getCardMenu().setVisibility(hasEditPermission ? View.VISIBLE : View.GONE);
        getCardTitle().setText(fullCard.getCard().getTitle().trim());
        getNotSyncedYet().setVisibility(DBStatus.LOCAL_EDITED.equals(fullCard.getStatusEnum()) ? View.VISIBLE : View.GONE);

        applyTheme(utils);

        if (fullCard.getCard().getDueDate() != null || fullCard.getCard().getDone() != null) {
            setupDueDate(getCardDueDate(), fullCard.getCard());
            getCardDueDate().setVisibility(View.VISIBLE);
        } else {
            getCardDueDate().setVisibility(View.GONE);
        }

        getCardMenu().setOnClickListener(view -> {
            final var popup = new PopupMenu(context, view);
            popup.inflate(optionsMenu);
            final var menu = popup.getMenu();
            if (containsUser(fullCard.getAssignedUsers(), account.getUserName())) {
                menu.removeItem(menu.findItem(R.id.action_card_assign).getItemId());
            } else {
                menu.removeItem(menu.findItem(R.id.action_card_unassign).getItemId());
            }
            if (boardRemoteId == null || fullCard.getCard().getId() == null) {
                menu.removeItem(R.id.share_link);
            }

            popup.setOnMenuItemClickListener(item -> optionsItemsSelectedListener.onCardOptionsItemSelected(item, fullCard));
            popup.show();
        });
    }

    @CallSuper
    protected void applyTheme(@Nullable ThemeUtils utils) {
        if (utils != null) {
            utils.platform.colorImageView(getNotSyncedYet(), ColorRole.PRIMARY);
            utils.platform.colorImageView(getCardMenu(), ColorRole.ON_SURFACE_VARIANT);
            utils.platform.colorTextView(getCardTitle(), ColorRole.ON_SURFACE);

            // TODO should be discussed with UX
            // utils.material.themeCardView(getCard());
        }

    }

    protected abstract DueDateChip getCardDueDate();

    protected abstract ImageView getNotSyncedYet();

    protected abstract TextView getCardTitle();

    protected abstract ImageView getCardMenu();

    protected abstract MaterialCardView getCard();

    public void bindCardClickListener(@Nullable OnClickListener l) {
        getCard().setOnClickListener(l);
    }

    public void bindCardLongClickListener(@Nullable OnLongClickListener l) {
        getCard().setOnLongClickListener(l);
    }

    public MaterialCardView getDraggable() {
        return getCard();
    }

    private static void setupDueDate(@NonNull DueDateChip cardDueDate, @NonNull Card card) {
        final boolean isDone = card.getDone() != null;
        final Instant date = isDone ? card.getDone() : card.getDueDate();

        if (date == null) {
            throw new IllegalArgumentException("Expected due date or done date to be present but both were null.");
        }
        cardDueDate.setDueDate(date, isDone);
    }

    protected static void setupCoverImages(@NonNull Account account, @NonNull ViewGroup coverImagesHolder, @NonNull FullCard fullCard, int maxCoverImagesCount) {
        coverImagesHolder.removeAllViews();
        if (maxCoverImagesCount > 0) {
            final var coverImages = fullCard.getAttachments()
                    .stream()
                    .filter(attachment -> MimeTypeUtil.isImage(attachment.getMimetype()))
                    .limit(maxCoverImagesCount)
                    .collect(Collectors.toList());
            if (coverImages.size() > 0) {
                coverImagesHolder.setVisibility(View.VISIBLE);
                coverImagesHolder.post(() -> {
                    for (final var coverImage : coverImages) {
                        final var coverImageView = new ImageView(coverImagesHolder.getContext());
                        final int coverWidth = coverImagesHolder.getWidth() / coverImages.size();
                        final int coverHeight = coverImagesHolder.getHeight();
                        coverImageView.setLayoutParams(new LinearLayout.LayoutParams(coverWidth, coverHeight));
                        coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        coverImagesHolder.addView(coverImageView);

                        final var requestManager = Glide.with(coverImageView);
                        AttachmentUtil.getThumbnailUrl(account, fullCard.getId(), coverImage, coverWidth, coverHeight)
                                .map(Uri::toString)
                                .map(uri -> requestManager.load(new SingleSignOnUrl(account.getName(), uri)))
                                .orElseGet(() -> requestManager.load(R.drawable.ic_image_24dp))
                                .placeholder(R.drawable.ic_image_24dp)
                                .error(R.drawable.ic_image_24dp)
                                .into(coverImageView);
                    }
                });
            } else {
                coverImagesHolder.setVisibility(View.GONE);
            }
        } else {
            coverImagesHolder.setVisibility(View.GONE);
        }
    }

    @Contract("null, _ -> false")
    private static boolean containsUser(List<User> userList, String username) {
        if (userList != null) {
            for (final var user : userList) {
                if (user.getPrimaryKey().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }
}