package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.niedermann.nextcloud.deck.ui.card.activities.CardActivityFragment;
import it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentsFragment;
import it.niedermann.nextcloud.deck.ui.card.comments.CardCommentsFragment;
import it.niedermann.nextcloud.deck.ui.card.details.CardDetailsFragment;

public class CardTabAdapter extends FragmentStateAdapter {

    private final long accountId;
    private final long localId;
    private final long boardId;
    private final boolean canEdit;
    private boolean hasCommentsAbility = false;

    @SuppressWarnings("WeakerAccess")
    public CardTabAdapter(
            @NonNull FragmentManager fm,
            @NonNull Lifecycle lifecycle,
            long accountId,
            long localId,
            long boardId,
            boolean canEdit
    ) {
        super(fm, lifecycle);
        this.accountId = accountId;
        this.localId = localId;
        this.boardId = boardId;
        this.canEdit = canEdit;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CardDetailsFragment.newInstance();
            case 1:
                return CardAttachmentsFragment.newInstance(accountId, localId, boardId, canEdit);
            case 2:
                return hasCommentsAbility
                        ? CardCommentsFragment.newInstance()
                        : CardActivityFragment.newInstance();
            case 3:
                if (hasCommentsAbility) {
                    return CardActivityFragment.newInstance();
                }
            default:
                throw new IllegalArgumentException("position " + position + " is not available");
        }
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public void enableComments() {
        this.hasCommentsAbility = true;
        notifyItemInserted(2);
    }

    @Override
    public long getItemId(int position) {
        if (!this.hasCommentsAbility) {
            return position;
        } else {
            switch (position) {
                case 0:
                case 1:
                    return position;
                case 2: // Comments tab is on position 2
                    return 3;
                case 3: // Activities tab moved to position 3
                default:
                    return 2;
            }
        }
    }

    @Override
    public int getItemCount() {
        return hasCommentsAbility ? 4 : 3;
    }
}
