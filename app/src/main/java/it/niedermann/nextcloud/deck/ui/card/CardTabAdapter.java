package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.niedermann.nextcloud.deck.ui.card.activities.CardActivityFragment;
import it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentsFragment;
import it.niedermann.nextcloud.deck.ui.card.comments.CardCommentsFragment;
import it.niedermann.nextcloud.deck.ui.card.details.CardDetailsFragment;
import it.niedermann.nextcloud.deck.ui.card.projects.CardProjectsFragment;

public class CardTabAdapter extends FragmentStateAdapter {

    private boolean hasCommentsAbility = false;

    public CardTabAdapter(final FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (hasCommentsAbility) {
            switch (position) {
                case 0:
                    return CardDetailsFragment.newInstance();
                case 1:
                    return CardAttachmentsFragment.newInstance();
                case 2:
                    return CardCommentsFragment.newInstance();
                case 3:
                    return CardProjectsFragment.newInstance();
                case 4:
                    return CardActivityFragment.newInstance();
                default:
                    throw new IllegalArgumentException("position " + position + " is not available");
            }
        } else {
            switch (position) {
                case 0:
                    return CardDetailsFragment.newInstance();
                case 1:
                    return CardAttachmentsFragment.newInstance();
                case 2:
                    return CardProjectsFragment.newInstance();
                case 3:
                    return CardActivityFragment.newInstance();
                default:
                    throw new IllegalArgumentException("position " + position + " is not available");
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void enableComments() {
        this.hasCommentsAbility = true;
        notifyItemInserted(2);
    }


    @Override
    public int getItemCount() {
        return hasCommentsAbility ? 5 : 4;
    }
}
