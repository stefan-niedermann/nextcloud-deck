package it.niedermann.nextcloud.deck.ui.stack;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.CardItemTouchHelper;

public class StackFragment extends Fragment {

    private static final String KEY_BOARD_ID = "boardId";
    private static final String KEY_STACK_ID = "stackId";
    private static final String KEY_ACCOUNT = "account";
    private CardAdapter adapter = null;
    private SyncManager syncManager;

    private long boardId;
    private long stackId;
    private Account account;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stack, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();

        boardId = getArguments().getLong(KEY_BOARD_ID);
        stackId = getArguments().getLong(KEY_STACK_ID);
        account = (Account) getArguments().getSerializable(KEY_ACCOUNT);

        syncManager = new SyncManager(getActivity().getApplicationContext(), getActivity());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            syncManager.synchronize(new IResponseCallback<Boolean>(account) {
                @Override
                public void onResponse(Boolean response) {
                    DeckLog.log("yay. whatever"); //TODO: is this what we want?
                }

                @Override
                public void onError(Throwable throwable) {
                    DeckLog.log("exception! " + throwable.getMessage());
                }
            });
            refreshView();
        });

        refreshView();
        return view;
    }

    private void refreshView() {
        syncManager.getStack(account.getId(), stackId).observe(StackFragment.this, (FullStack stack) -> {
            if (stack != null) {
                syncManager.getFullCardsForStack(account.getId(), stack.getLocalId()).observe(StackFragment.this, (List<FullCard> cards) -> {
                    if (cards != null) {
                        adapter.setCardList(cards);
                    }
                });
            }
        });
    }


    private void initRecyclerView() {
        adapter = new CardAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper touchHelper = new CardItemTouchHelper(adapter);
        touchHelper.attachToRecyclerView(recyclerView);
    }
}