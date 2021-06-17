package it.niedermann.nextcloud.deck.ui.stack;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.model.Stack;

public class StackAdapter extends FragmentStateAdapter {
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
     * @return the position of the direct neighbour of the given {@param position} if available. Prefers neighbours to the start of the wanted, but might also return a neighbour to the end.
     * @throws NoSuchElementException in case this is the only {@link Stack}.
     */
    public int getNeighbourPosition(int position) throws NoSuchElementException, IndexOutOfBoundsException {
        if (position >= stackList.size()) {
            throw new IndexOutOfBoundsException("Position " + position + " is not in the current stack list.");
        }
        if (stackList.size() < 2) {
            throw new NoSuchElementException("There is no neighbour.");
        }
        return position > 0
                ? position - 1
                : position + 1;
    }

    @Override
    public long getItemId(int position) {
        return stackList.get(position).getLocalId();
    }

    @Override
    public boolean containsItem(long itemId) {
        for (Stack stack : stackList) {
            if (stack.getLocalId() == itemId) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StackFragment.newInstance(stackList.get(position).getLocalId());
    }

    public void setStacks(@NonNull List<Stack> stacks) {
        this.stackList.clear();
        this.stackList.addAll(stacks);
        notifyDataSetChanged();
    }
}