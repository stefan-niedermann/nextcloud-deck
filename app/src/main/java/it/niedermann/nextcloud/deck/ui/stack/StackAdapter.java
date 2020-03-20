package it.niedermann.nextcloud.deck.ui.stack;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullStack;

public class StackAdapter extends FragmentStateAdapter {
    @NonNull
    private final List<FullStack> stackList = new ArrayList<>();
    @NonNull
    private final Account account;
    private final long boardId;
    private final boolean canEdit;

    public StackAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle, @NonNull List<FullStack> stackList, @NonNull Account account, long boardId, boolean canEdit) {
        super(fm, lifecycle);
        this.stackList.addAll(stackList);
        this.account = account;
        this.boardId = boardId;
        this.canEdit = canEdit;
    }

    @Override
    public int getItemCount() {
        return stackList.size();
    }

    public FullStack getItem(int position) {
        return stackList.get(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StackFragment.newInstance(boardId, stackList.get(position).getLocalId(), account, canEdit);
    }
}