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
import it.niedermann.nextcloud.deck.DeckApplication;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.FragmentStackBinding;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.filter.FilterViewModel;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardListener;

public class StackFragment extends Fragment implements DragAndDropTab<CardAdapter>, MoveCardListener {

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

        final var args = requireArguments();
        if (!args.containsKey(KEY_STACK_ID)) {
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

        final var filterViewModel = new ViewModelProvider(activity).get(FilterViewModel.class);

        // This might be a zombie fragment with an empty MainViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (mainViewModel.getCurrentAccount() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + StackFragment.class.getSimpleName() + " because mainViewModel.getCurrentAccount() is null"));
            return binding.getRoot();
        }

        adapter = new CardAdapter(requireActivity(), getChildFragmentManager(), stackId, mainViewModel,
                (requireActivity() instanceof SelectCardListener)
                        ? (SelectCardListener) requireActivity()
                        : null);
        binding.recyclerView.setAdapter(adapter);
        binding.loadingSpinner.show();

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
            binding.loadingSpinner.hide();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeckApplication.readCurrentBoardColor().observe(getViewLifecycleOwner(), this::applyBrand);
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

    private void applyBrand(int mainColor) {
        if (this.adapter != null) {
            this.adapter.applyBrand(mainColor);
        }
    }

    public static Fragment newInstance(long stackId) {
        final var fragment = new StackFragment();

        final var args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void move(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        mainViewModel.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.log("Moved", Card.class.getSimpleName(), originCardLocalId, "to", Stack.class.getSimpleName(), targetStackLocalId);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                    ExceptionDialogFragment.newInstance(throwable, null).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }
        });
    }

}