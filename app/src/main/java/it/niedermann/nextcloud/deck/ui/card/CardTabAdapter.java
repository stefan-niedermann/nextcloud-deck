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
    private final boolean hasCommentsAbility;

    public CardTabAdapter(
            @NonNull FragmentManager fm,
            @NonNull Lifecycle lifecycle,
            long accountId,
            long localId,
            long boardId,
            boolean canEdit,
            boolean hasCommentsAbility
    ) {
        super(fm, lifecycle);
        this.accountId = accountId;
        this.localId = localId;
        this.boardId = boardId;
        this.canEdit = canEdit;
        this.hasCommentsAbility = hasCommentsAbility;
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
                        : CardActivityFragment.newInstance(accountId, localId, boardId, canEdit);
            case 3:
                if (hasCommentsAbility) {
                    return CardActivityFragment.newInstance(accountId, localId, boardId, canEdit);
                }
            default:
                throw new IllegalArgumentException("position " + position + " is not available");
        }
    }

    @Override
    public int getItemCount() {
        return hasCommentsAbility ? 4 : 3;
    }
}
