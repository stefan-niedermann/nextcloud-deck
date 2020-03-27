package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.niedermann.nextcloud.deck.ui.card.comments.CardCommentsFragment;

public class CardTabAdapter extends FragmentStateAdapter {

    private final long accountId;
    private final long localId;
    private final long boardId;
    private final boolean canEdit;
    private boolean hasCommentsAbility = false;

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
                return CardDetailsFragment.newInstance(accountId, localId, boardId, canEdit);
            case 1:
                return CardAttachmentsFragment.newInstance(accountId, localId, boardId, canEdit);
            case 2:
                return hasCommentsAbility
                        ? CardCommentsFragment.newInstance(accountId, localId, canEdit)
                        : CardActivityFragment.newInstance(accountId, localId, boardId);
            case 3:
                if (hasCommentsAbility) {
                    return CardActivityFragment.newInstance(accountId, localId, boardId);
                }
            default:
                throw new IllegalArgumentException("position " + position + " is not available");
        }
    }

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
