package it.niedermann.nextcloud.deck.ui.card;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import it.niedermann.nextcloud.deck.R;

public class CardTabAdapter extends FragmentStatePagerAdapter {

    private Resources resources;
    private long accountId;
    private long localId;
    private long boardId;

    public CardTabAdapter(FragmentManager fm, Resources resources, long accountId, long localId, long boardId) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.resources = resources;
        this.accountId = accountId;
        this.localId = localId;
        this.boardId = boardId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CardDetailsFragment.newInstance(accountId, localId, boardId);
            case 1:
                return CardAttachmentsFragment.newInstance(accountId, localId, boardId);
            case 2:
                return CardActivityFragment.newInstance(accountId, localId, boardId);
            default:
                throw new IllegalArgumentException("position " + position + "is not available");
        }
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return this.resources.getString(R.string.card_edit_details);
            case 1:
                return this.resources.getString(R.string.card_edit_attachments);
            case 2:
                return this.resources.getString(R.string.card_edit_activity);
            default:
                throw new IllegalArgumentException("position " + position + "is not available");
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
