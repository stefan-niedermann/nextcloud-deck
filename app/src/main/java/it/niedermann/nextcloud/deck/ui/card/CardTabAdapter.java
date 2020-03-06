package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CardTabAdapter extends FragmentStateAdapter {

    private long accountId;
    private long localId;
    private long boardId;
    private boolean canEdit;

    public CardTabAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle, long accountId, long localId, long boardId, boolean canEdit) {
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
                return CardActivityFragment.newInstance(accountId, localId, boardId, canEdit);
            default:
                throw new IllegalArgumentException("position " + position + "is not available");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
