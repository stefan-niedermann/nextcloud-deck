package it.niedermann.nextcloud.deck.ui.stack;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

public class StackFragment extends Fragment {

    private static final String KEY_BOARD_ID = "boardId";
    private static final String KEY_STACK_ID = "stackId";
    private static final String KEY_ACCOUNT = "account";
    private CardAdapter adapter = null;
    private SyncManager syncManager;
    private Activity activity;

    private long stackId;
    private long boardId;
    private Account account;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.no_cards)
    RelativeLayout noCards;

    /**
     * @param boardId of the current stack
     * @return new fragment instance
     * @see <a href="https://gunhansancar.com/best-practice-to-instantiate-fragments-with-arguments-in-android/">Best Practice to Instantiate Fragments with Arguments in Android</a>
     */
    public static StackFragment newInstance(long boardId, long stackId, Account account) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_BOARD_ID, boardId);
        bundle.putLong(KEY_STACK_ID, stackId);
        bundle.putSerializable(KEY_ACCOUNT, account);

        StackFragment fragment = new StackFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stack, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() == null) {
            throw new IllegalArgumentException("account and localStackId are required arguments.");
        }

        boardId = getArguments().getLong(KEY_BOARD_ID);
        stackId = getArguments().getLong(KEY_STACK_ID);
        account = (Account) getArguments().getSerializable(KEY_ACCOUNT);

        activity = Objects.requireNonNull(getActivity());

        syncManager = new SyncManager(activity);

        LiveDataHelper.observeOnce(syncManager.getFullBoardById(account.getId(), boardId), this, (fullBoard) -> {
            adapter = new CardAdapter(boardId, fullBoard.getBoard().isPermissionEdit(), syncManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            if (activity instanceof MainActivity) {
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0)
                            ((MainActivity) activity).hideFab();
                        else if (dy < 0)
                            ((MainActivity) activity).showFab();
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> syncManager.synchronize(new IResponseCallback<Boolean>(account) {
            @Override
            public void onResponse(Boolean response) {
                refreshView();
                activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }

            @Override
            public void onError(Throwable throwable) {
                activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                DeckLog.log("exception! " + throwable.getMessage());
            }
        }));

        refreshView();
        return view;
    }

    private void refreshView() {
        activity.runOnUiThread(() -> syncManager.getStack(account.getId(), stackId).observe(StackFragment.this, (FullStack stack) -> {
            if (stack != null) {
                syncManager.getFullCardsForStack(account.getId(), stack.getLocalId()).observe(StackFragment.this, (List<FullCard> cards) -> {
                    if (cards == null || cards.size() == 0) {
                        this.noCards.setVisibility(View.VISIBLE);
                    } else {
                        this.noCards.setVisibility(View.GONE);
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
        return recyclerView;
    }

    public long getStackId() {
        return this.stackId;
    }
}