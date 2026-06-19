package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.card.activities.CardActivityFragment;
import it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentsFragment;
import it.niedermann.nextcloud.deck.ui.card.comments.CardCommentsFragment;
import it.niedermann.nextcloud.deck.ui.card.details.CardDetailsFragment;

public class CardTabAdapter extends FragmentStateAdapter {

    private final Account account;
    private boolean hasCommentsAbility = false;

    public CardTabAdapter(
            @NonNull final FragmentActivity fa,
            @NonNull final Account account
    ) {
        super(fa);
        this.account = account;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CardDetailsFragment.newInstance(account);
            case 1:
                return CardAttachmentsFragment.newInstance();
            case 2:
                return hasCommentsAbility
                        ? CardCommentsFragment.newInstance(account)
                        : CardActivityFragment.newInstance();
            case 3:
                if (hasCommentsAbility) {
                    return CardActivityFragment.newInstance();
                }
            default:
                throw new IllegalArgumentException("position " + position + " is not available");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void enableComments() {
        this.hasCommentsAbility = true;
        notifyItemInserted(2);
    }

    @Override
    public long getItemId(int position) {
        if (!this.hasCommentsAbility) {
            return position;
        } else {
            return switch (position) {
                case 0, 1 -> position;
                case 2 -> 3; // Comments tab is on position 2, Activities tab moved to position 3
                default -> 2;
            };
        }
    }

    @Override
    public int getItemCount() {
        return hasCommentsAbility ? 4 : 3;
    }
}
