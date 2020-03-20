package it.niedermann.nextcloud.deck.ui.stack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.databinding.FragmentStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

public class StackFragment extends Fragment {

    private static final String KEY_BOARD_ID = "boardId";
    private static final String KEY_STACK_ID = "stackId";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_HAS_EDIT_PERMISSION = "hasEditPermission";
    private CardAdapter adapter = null;
    private SyncManager syncManager;
    private Activity activity;
    private OnScrollListener onScrollListener;

    private long stackId;
    private long boardId;
    private Account account;
    private boolean canEdit;

    private FragmentStackBinding binding;

    /**
     * @param boardId of the current stack
     * @return new fragment instance
     * @see <a href="https://gunhansancar.com/best-practice-to-instantiate-fragments-with-arguments-in-android/">Best Practice to Instantiate Fragments with Arguments in Android</a>
     */
    public static StackFragment newInstance(long boardId, long stackId, Account account, boolean hasEditPermission) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_BOARD_ID, boardId);
        bundle.putLong(KEY_STACK_ID, stackId);
        bundle.putBoolean(KEY_HAS_EDIT_PERMISSION, hasEditPermission);
        bundle.putSerializable(KEY_ACCOUNT, account);

        StackFragment fragment = new StackFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollListener) {
            this.onScrollListener = (OnScrollListener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStackBinding.inflate(inflater, container, false);

        if (getArguments() == null) {
            throw new IllegalArgumentException("account and localStackId are required arguments.");
        }

        boardId = getArguments().getLong(KEY_BOARD_ID);
        stackId = getArguments().getLong(KEY_STACK_ID);
        account = (Account) getArguments().getSerializable(KEY_ACCOUNT);
        canEdit = getArguments().getBoolean(KEY_HAS_EDIT_PERMISSION);

        activity = requireActivity();

        syncManager = new SyncManager(activity);

        if(requireActivity() instanceof CardAdapter.SelectCardListener) {
            adapter = new CardAdapter(boardId, stackId, getArguments().getBoolean(KEY_HAS_EDIT_PERMISSION), syncManager, this, (CardAdapter.SelectCardListener) requireActivity());
        } else {
            adapter = new CardAdapter(boardId, stackId, getArguments().getBoolean(KEY_HAS_EDIT_PERMISSION), syncManager, this);
        }
        binding.recyclerView.setAdapter(adapter);
        if (onScrollListener != null) {
            binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0)
                        onScrollListener.onScrollDown();
                    else if (dy < 0)
                        onScrollListener.onScrollUp();
                }
            });
        }

        if (!canEdit) {
            binding.emptyContentView.hideDescription();
        }

        refreshView();
        return binding.getRoot();
    }

    private void refreshView() {
        activity.runOnUiThread(() -> syncManager.getStack(account.getId(), stackId).observe(getViewLifecycleOwner(), (FullStack stack) -> {
            if (stack != null) {
                syncManager.getFullCardsForStack(account.getId(), stack.getLocalId()).observe(getViewLifecycleOwner(), (List<FullCard> cards) -> {
                    if (cards == null || cards.size() == 0) {
                        binding.emptyContentView.setVisibility(View.VISIBLE);
                    } else {
                        binding.emptyContentView.setVisibility(View.GONE);
                        adapter.setCardList(cards);
                    }
                });
            }
        }));
    }

    public CardAdapter getAdapter() {
        return adapter;
    }

    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    public long getStackId() {
        return this.stackId;
    }

    public interface OnScrollListener {
        void onScrollUp();

        void onScrollDown();
    }
}