package it.niedermann.nextcloud.deck.ui.stack;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.android.crosstabdnd.DragAndDropTab;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.FragmentStackBinding;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.filter.FilterViewModel;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardListener;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class StackFragment extends BrandedFragment implements DragAndDropTab<CardAdapter>, MoveCardListener {

    private static final String KEY_STACK_ID = "stackId";

    private FragmentStackBinding binding;
    private MainViewModel mainViewModel;
    private FragmentActivity activity;
    private OnScrollListener onScrollListener;

    @Nullable
    private CardAdapter adapter = null;
    private LiveData<List<FullCard>> cardsLiveData;

    private long stackId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        final Bundle args = getArguments();
        if (args == null || !args.containsKey(KEY_STACK_ID)) {
            throw new IllegalArgumentException(KEY_STACK_ID + " is required.");
        }

        stackId = args.getLong(KEY_STACK_ID);

        if (context instanceof OnScrollListener) {
            this.onScrollListener = (OnScrollListener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = requireActivity();
        binding = FragmentStackBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(activity).get(MainViewModel.class);

        final FilterViewModel filterViewModel = new ViewModelProvider(activity).get(FilterViewModel.class);

        // This might be a zombie fragment with an empty MainViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (mainViewModel.getCurrentAccount() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + StackFragment.class.getSimpleName() + " because mainViewModel.getCurrentAccount() is null"));
            return binding.getRoot();
        }

        adapter = new CardAdapter(requireContext(), getChildFragmentManager(), stackId, mainViewModel, this,
                (requireActivity() instanceof SelectCardListener)
                        ? (SelectCardListener) requireActivity()
                        : null);
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

        if (!mainViewModel.currentBoardHasEditPermission()) {
            binding.emptyContentView.hideDescription();
        }

        final Observer<List<FullCard>> cardsObserver = (fullCards) -> activity.runOnUiThread(() -> {
            if (fullCards != null && fullCards.size() > 0) {
                binding.emptyContentView.setVisibility(View.GONE);
                adapter.setCardList(fullCards);
            } else {
                binding.emptyContentView.setVisibility(View.VISIBLE);
            }
        });

        cardsLiveData = mainViewModel.getFullCardsForStack(mainViewModel.getCurrentAccount().getId(), stackId, filterViewModel.getFilterInformation().getValue());
        cardsLiveData.observe(getViewLifecycleOwner(), cardsObserver);

        filterViewModel.getFilterInformation().observe(getViewLifecycleOwner(), (filterInformation -> {
            cardsLiveData.removeObserver(cardsObserver);
            cardsLiveData = mainViewModel.getFullCardsForStack(mainViewModel.getCurrentAccount().getId(), stackId, filterInformation);
            cardsLiveData.observe(getViewLifecycleOwner(), cardsObserver);
        }));

        return binding.getRoot();
    }

    @Nullable
    @Override
    public CardAdapter getAdapter() {
        return adapter;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    @Override
    public void applyBrand(int mainColor) {
        if (this.adapter != null) {
            this.adapter.applyBrand(mainColor);
        }
    }

    public static Fragment newInstance(long stackId) {
        final Bundle args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);

        final StackFragment fragment = new StackFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void move(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        final WrappedLiveData<Void> liveData = mainViewModel.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId);
        observeOnce(liveData, requireActivity(), (next) -> {
            if (liveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(liveData.getError())) {
                ExceptionDialogFragment.newInstance(liveData.getError(), null).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            } else {
                DeckLog.log("Moved " + Card.class.getSimpleName() + " \"" + originCardLocalId + "\" to " + Stack.class.getSimpleName() + " \"" + targetStackLocalId + "\"");
            }
        });
    }

}