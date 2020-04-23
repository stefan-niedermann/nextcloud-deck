package it.niedermann.nextcloud.deck.ui.stack;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.android.crosstabdnd.DragAndDropTab;
import it.niedermann.nextcloud.deck.databinding.FragmentStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;

public class StackFragment extends BrandedFragment implements DragAndDropTab<CardAdapter> {

    private static final String KEY_BOARD_ID = "boardId";
    private static final String KEY_STACK_ID = "stackId";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_HAS_EDIT_PERMISSION = "hasEditPermission";
    private CardAdapter adapter = null;
    private FragmentStackBinding binding;

    MainViewModel viewModel;
    private SyncManager syncManager;
    private FragmentActivity activity;
    private OnScrollListener onScrollListener;

    private LiveData<List<FullCard>> defaultCardsLiveData;
    private LiveData<List<FullCard>> filteredCardsLiveData;
    private Observer<List<FullCard>> cardObserver = (List<FullCard> cards) -> {
        activity.runOnUiThread(() -> {
            if (cards != null && cards.size() > 0) {
                binding.emptyContentView.setVisibility(View.GONE);
                adapter.setCardList(cards);
            } else {
                binding.emptyContentView.setVisibility(View.VISIBLE);
            }
        });
    };

    private long stackId;
    private long boardId;
    private Account account;
    private boolean canEdit;

    public static StackFragment newInstance(long boardId, long stackId, Account account, boolean hasEditPermission) {
        final Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, boardId);
        args.putLong(KEY_STACK_ID, stackId);
        args.putBoolean(KEY_HAS_EDIT_PERMISSION, hasEditPermission);
        args.putSerializable(KEY_ACCOUNT, account);

        StackFragment fragment = new StackFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        final Bundle args = getArguments();
        if (args == null || !args.containsKey(KEY_ACCOUNT) || !args.containsKey(KEY_BOARD_ID) || !args.containsKey(KEY_STACK_ID)) {
            throw new IllegalArgumentException("account, boardId and localStackId are required arguments.");
        }

        boardId = args.getLong(KEY_BOARD_ID);
        stackId = args.getLong(KEY_STACK_ID);
        account = (Account) args.getSerializable(KEY_ACCOUNT);
        canEdit = args.getBoolean(KEY_HAS_EDIT_PERMISSION);

        if (context instanceof OnScrollListener) {
            this.onScrollListener = (OnScrollListener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStackBinding.inflate(inflater, container, false);
        activity = requireActivity();

        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);

        syncManager = new SyncManager(activity);

        adapter = new CardAdapter(requireContext(), account, boardId, stackId, canEdit, syncManager, this, (requireActivity() instanceof SelectCardListener) ? (SelectCardListener) requireActivity() : null);
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

        defaultCardsLiveData = syncManager.getFullCardsForStack(account.getId(), stackId);
        defaultCardsLiveData.observe(getViewLifecycleOwner(), cardObserver);

        viewModel.getFilterInformation().observe(activity, (filterInformation -> {
            // Remove filter only if it has never been set before
            if (filterInformation == null && filteredCardsLiveData != null) {
                filteredCardsLiveData.removeObserver(cardObserver);
                defaultCardsLiveData.observe(getViewLifecycleOwner(), cardObserver);
            } else {
                defaultCardsLiveData.removeObserver(cardObserver);
                filteredCardsLiveData = syncManager.getFullCardsForStack(account.getId(), stackId, filterInformation);
                filteredCardsLiveData.observe(getViewLifecycleOwner(), cardObserver);
            }
        }));
        return binding.getRoot();
    }

    @Override
    public CardAdapter getAdapter() {
        return adapter;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        this.adapter.applyBrand(mainColor, textColor);
    }
}