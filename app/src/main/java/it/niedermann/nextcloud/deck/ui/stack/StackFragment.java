package it.niedermann.nextcloud.deck.ui.stack;

import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.android.crosstabdnd.DragAndDropTab;
import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.FragmentStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.card.CardActionListener;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.filter.FilterViewModel;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardDialogFragment;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardListener;
import it.niedermann.nextcloud.deck.ui.theme.Themed;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;
import it.niedermann.nextcloud.deck.util.CardUtil;

public class StackFragment extends Fragment implements Themed, DragAndDropTab<CardAdapter>, MoveCardListener, CardActionListener {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_BOARD_ID = "boardId";
    private static final String KEY_STACK_ID = "stackId";

    private FragmentStackBinding binding;
    private StackViewModel stackViewModel;
    private FragmentActivity activity;
    private OnScrollListener onScrollListener;

    @Nullable
    private CardAdapter adapter = null;

    private Account account;
    private long boardId;
    private long stackId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        final var args = requireArguments();

        if (!args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " is required.");
        }
        account = (Account) args.getSerializable(KEY_ACCOUNT);

        if (!args.containsKey(KEY_BOARD_ID)) {
            throw new IllegalArgumentException(KEY_BOARD_ID + " is required.");
        }
        boardId = args.getLong(KEY_BOARD_ID);

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
        stackViewModel = new SyncViewModel.Provider(requireActivity(), requireActivity().getApplication(), account).get(StackViewModel.class);

        applyTheme(account.getColor());

        final var filterViewModel = new ViewModelProvider(activity).get(FilterViewModel.class);

        if (onScrollListener != null) {
            binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (!recyclerView.canScrollVertically(1)) {
                        onScrollListener.onBottomReached();
                    } else if (dy > 0) {
                        onScrollListener.onScrollDown();
                    } else if (dy < 0) {
                        onScrollListener.onScrollUp();
                    }
                }
            });
        }

        stackViewModel.currentBoardHasEditPermission(account.getId(), boardId).observe(getViewLifecycleOwner(), hasEditPermission -> {
            if (hasEditPermission) {
                binding.emptyContentView.showDescription();
            } else {
                binding.emptyContentView.hideDescription();
            }
        });

        @Nullable final var selectCardListener = (activity instanceof SelectCardListener) ? (SelectCardListener) activity : null;

        adapter = new CardAdapter(activity, this, selectCardListener);
        binding.recyclerView.setAdapter(adapter);

        new ReactiveLiveData<>(stackViewModel.getAccount(account.getId()))
                .tap(() -> binding.loadingSpinner.show())
                .tap(account -> adapter.setAccount(account))
                .flatMap(account -> stackViewModel.getFullBoard(account.getId(), boardId))
                .tap(fullBoard -> adapter.setFullBoard(fullBoard))
                .flatMap(filterViewModel::getFilterInformation)
                .flatMap(filterInformation -> stackViewModel.getFullCardsForStack(account.getId(), stackId, filterInformation))
                .combineWith(() -> stackViewModel.getBoardColor$(account.getId(), boardId))
                .observe(getViewLifecycleOwner(), pair -> {
                    binding.loadingSpinner.hide();
                    if (pair.first != null && !pair.first.isEmpty()) {
                        binding.emptyContentView.setVisibility(View.GONE);
                        assert adapter != null;
                        adapter.setCardList(pair.first, pair.second);
                    } else {
                        binding.emptyContentView.setVisibility(View.VISIBLE);
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
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
    public void move(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        stackViewModel.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.log("Moved", Card.class.getSimpleName(), originCardLocalId, "to", Stack.class.getSimpleName(), targetStackLocalId);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    ExceptionDialogFragment.newInstance(throwable, null).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }
        });
    }

    /**
     * Scroll to the bottom of the fragment
     */
    public void scrollToBottom() {
        activity.runOnUiThread(() -> {
            if (adapter == null) {
                DeckLog.warn("Adapter is null");
                return;
            }
            final var layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
            if (layoutManager == null) {
                DeckLog.warn("LayoutManager is null");
                return;
            }
            int currentItem = layoutManager.findFirstVisibleItemPosition();

            if (adapter.getItemCount() - currentItem < 40) {
                binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
            } else {
                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    @Override
    public void onArchive(@NonNull FullCard fullCard) {
        stackViewModel.archiveCard(fullCard, new IResponseCallback<>() {
            @Override
            public void onResponse(FullCard response) {
                DeckLog.info("Successfully archived", Card.class.getSimpleName(), fullCard.getCard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                showExceptionDialog(throwable, fullCard.getAccountId());
            }
        });
    }

    @Override
    public void onDelete(@NonNull FullCard fullCard) {
        stackViewModel.deleteCard(fullCard.getCard(), new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.info("Successfully deleted card", fullCard.getCard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    showExceptionDialog(throwable, fullCard.getAccountId());
                }
            }
        });
    }

    @Override
    public void onAssignCurrentUser(@NonNull FullCard fullCard) {
        stackViewModel.assignUserToCard(fullCard);
    }

    @Override
    public void onUnassignCurrentUser(@NonNull FullCard fullCard) {
        stackViewModel.unassignUserFromCard(fullCard);
    }

    @Override
    public void onMove(@NonNull FullBoard fullBoard, @NonNull FullCard fullCard) {
        DeckLog.verbose("[Move card] Launch move dialog for " + Card.class.getSimpleName() + " \"" + fullCard.getCard().getTitle() + "\" (#" + fullCard.getLocalId() + ") from " + Stack.class.getSimpleName() + " #" + +stackId);
        MoveCardDialogFragment
                .newInstance(fullCard.getAccountId(), fullBoard.getBoard().getLocalId(), fullCard.getCard().getTitle(), fullCard.getLocalId(), CardUtil.cardHasCommentsOrAttachments(fullCard))
                .show(getChildFragmentManager(), MoveCardDialogFragment.class.getSimpleName());
    }

    @Override
    public void onShareLink(@NonNull FullBoard fullBoard, @NonNull FullCard fullCard) {
        stackViewModel.getAccountFuture(fullCard.getAccountId()).thenAcceptAsync(account -> {
            final int shareLinkRes = account.getServerDeckVersionAsObject().getShareLinkResource();
            final var shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType(TEXT_PLAIN)
                    .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TEXT, account.getUrl() + activity.getString(shareLinkRes, fullBoard.getBoard().getId(), fullCard.getCard().getId()));
            activity.startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onShareContent(@NonNull FullCard fullCard) {
        final var shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .setType(TEXT_PLAIN)
                .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                .putExtra(Intent.EXTRA_TEXT, CardUtil.getCardContentAsString(activity, fullCard));
        activity.startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
    }

    @AnyThread
    private void showExceptionDialog(@NonNull Throwable throwable, long accountId) {
        stackViewModel.getAccountFuture(accountId).thenAcceptAsync(account -> ExceptionDialogFragment
                        .newInstance(throwable, account)
                        .show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()),
                ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void applyTheme(int color) {
        binding.emptyContentView.applyTheme(color);
    }

    public static Fragment newInstance(@NonNull Account account, long boardId, long stackId) {
        final var fragment = new StackFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putLong(KEY_BOARD_ID, boardId);
        args.putLong(KEY_STACK_ID, stackId);
        fragment.setArguments(args);

        return fragment;
    }
}