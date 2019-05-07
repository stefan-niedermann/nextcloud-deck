package it.niedermann.nextcloud.deck.ui.stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StackAdapter extends FragmentStatePagerAdapter {
    private final List<StackFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public StackAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public StackFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(StackFragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void clear() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}