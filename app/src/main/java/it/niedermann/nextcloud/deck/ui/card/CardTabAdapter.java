package it.niedermann.nextcloud.deck.ui.card;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CardTabAdapter extends FragmentStatePagerAdapter {
    private CardDetailsFragment detailsFragment;
    private CardAttachmentsFragment attachmentsFragment;
    private CardActivityFragment activityFragment;

    private long accountId;
    private long localId;

    public CardTabAdapter(FragmentManager fm, long accountId, long localId) {
        super(fm);
        this.accountId = accountId;
        this.localId = localId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                detailsFragment = CardDetailsFragment.newInstance(accountId, localId);
                return detailsFragment;
            case 1:
                attachmentsFragment = CardAttachmentsFragment.newInstance();
                return attachmentsFragment;
            case 2:
                activityFragment = CardActivityFragment.newInstance();
                return activityFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Details";
        } else if (position == 1) {
            return "Attachments";
        } else if (position == 2) {
            return "Activity";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
