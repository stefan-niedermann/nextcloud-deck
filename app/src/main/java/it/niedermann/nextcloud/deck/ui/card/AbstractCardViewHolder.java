package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.Contract;

import java.time.ZoneId;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public abstract class AbstractCardViewHolder extends RecyclerView.ViewHolder {

    public AbstractCardViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * Removes all {@link OnClickListener} and {@link OnLongClickListener}
     */
    @CallSuper
    public void bind(@NonNull FullCard fullCard, @NonNull Account account, @Nullable Long boardRemoteId, boolean hasEditPermission, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener, @NonNull String counterMaxValue, @ColorInt int mainColor) {
        final Context context = itemView.getContext();

        bindCardClickListener(null);
        bindCardLongClickListener(null);

        getCardMenu().setVisibility(hasEditPermission ? View.VISIBLE : View.GONE);
        getCardTitle().setText(fullCard.getCard().getTitle().trim());

        DrawableCompat.setTint(getNotSyncedYet().getDrawable(), mainColor);
        getNotSyncedYet().setVisibility(DBStatus.LOCAL_EDITED.equals(fullCard.getStatusEnum()) ? View.VISIBLE : View.GONE);

        if (fullCard.getCard().getDueDate() != null) {
            setupDueDate(getCardDueDate(), fullCard.getCard());
            getCardDueDate().setVisibility(View.VISIBLE);
        } else {
            getCardDueDate().setVisibility(View.GONE);
        }

        getCardMenu().setOnClickListener(view -> {
            final PopupMenu popup = new PopupMenu(context, view);
            popup.inflate(optionsMenu);
            final Menu menu = popup.getMenu();
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

    protected abstract TextView getCardDueDate();

    protected abstract ImageView getNotSyncedYet();

    protected abstract TextView getCardTitle();

    protected abstract View getCardMenu();

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

    private static void setupDueDate(@NonNull TextView cardDueDate, @NonNull Card card) {
        final Context context = cardDueDate.getContext();
        cardDueDate.setText(DateUtil.getRelativeDateTimeString(context, card.getDueDate().toEpochMilli()));
        ViewUtil.themeDueDate(context, cardDueDate, card.getDueDate().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    @Contract("null, _ -> false")
    private static boolean containsUser(List<User> userList, String username) {
        if (userList != null) {
            for (User user : userList) {
                if (user.getPrimaryKey().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }
}