package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CardTabAdapter extends FragmentStatePagerAdapter {
    private CardDetailsFragment detailsFragment;
    private CardAttachmentsFragment attachmentsFragment;
    private CardActivityFragment activityFragment;

    private long accountId;
    private long localId;
    private long boardId;

    public CardTabAdapter(FragmentManager fm, long accountId, long localId, long boardId) {
        super(fm);
        this.accountId = accountId;
        this.localId = localId;
        this.boardId = boardId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                detailsFragment = CardDetailsFragment.newInstance(accountId, localId, boardId);
                return detailsFragment;
            case 1:
                attachmentsFragment = CardAttachmentsFragment.newInstance(accountId, localId, boardId);
                return attachmentsFragment;
            case 2:
                activityFragment = CardActivityFragment.newInstance();
                return activityFragment;
            default:
                throw new IllegalArgumentException("position " + position + "is not available");
        }
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        // TODO extract string resources
        switch (position) {
            case 0:
                return "Details";
            case 1:
                return "Attachments";
            case 2:
                return "Activity";
            default:
                throw new IllegalArgumentException("position " + position + "is not available");
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
