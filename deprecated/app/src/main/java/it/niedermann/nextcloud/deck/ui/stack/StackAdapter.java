package it.niedermann.nextcloud.deck.ui.stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Stack;

public class StackAdapter extends FragmentStateAdapter {

    @Nullable
    private Account account;
    @Nullable
    private Long boardId;
    @NonNull
    private final List<Stack> stackList = new ArrayList<>();

    public StackAdapter(final FragmentActivity fa) {
        super(fa);
    }

    @Override
    public int getItemCount() {
        return stackList.size();
    }

    public Stack getItem(int position) {
        return stackList.get(position);
    }

    /**
     * @return the position of the {@link Stack} where {@link Stack#getLocalId()} equals {@param stackLocalId}.
     * @throws NoSuchElementException in case the searched {@param stackLocalId} is not in the list.
     */
    public int getPosition(long stackLocalId) throws NoSuchElementException {
        for (int i = 0; i < stackList.size(); i++) {
            if (stackList.get(i).getLocalId() == stackLocalId) {
                return i;
            }
        }
        throw new NoSuchElementException("Stack with localId " + stackLocalId + " is not in the current list.");
    }

    @Override
    public long getItemId(int position) {
        return stackList.get(position).getLocalId();
    }

    @Override
    public boolean containsItem(long itemId) {
        for (final var stack : stackList) {
            if (stack.getLocalId() == itemId) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        final var stack = stackList.get(position);
        if (account == null) {
            throw new NullPointerException("Account in " + StackAdapter.class.getSimpleName() + " is null, can not create " + StackFragment.class.getSimpleName());
        }
        return StackFragment.newInstance(account, stack.getBoardId(), stack.getLocalId());
    }

    public void setStacks(@Nullable Account account, @Nullable Long boardId, @NonNull List<Stack> stacks) {
        this.account = account;
        this.boardId = boardId;
        this.stackList.clear();
        this.stackList.addAll(stacks);
        notifyDataSetChanged();
    }

    @Nullable
    public Account getAccount() {
        return account;
    }

    @Nullable
    public Long getBoardId() {
        return boardId;
    }
}