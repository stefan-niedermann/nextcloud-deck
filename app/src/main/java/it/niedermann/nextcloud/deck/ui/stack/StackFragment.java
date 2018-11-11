package it.niedermann.nextcloud.deck.ui.stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.CardItemTouchHelper;

public class StackFragment extends Fragment {

    private static final String KEY_BOARD_ID = "boardId";
    private static final String KEY_STACK_ID = "stackId";
    private CardAdapter adapter = null;
    private SyncManager syncManager;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    /**
     * @param boardId of the current stack
     * @return new fragment instance
     * @see <a href="https://gunhansancar.com/best-practice-to-instantiate-fragments-with-arguments-in-android/">Best Practice to Instantiate Fragments with Arguments in Android</a>
     */
    public static StackFragment newInstance(long boardId, long stackId) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_BOARD_ID, boardId);
        bundle.putLong(KEY_STACK_ID, stackId);

        StackFragment fragment = new StackFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stack, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        long boardId = getArguments().getLong(KEY_BOARD_ID);
        long stackId = getArguments().getLong(KEY_STACK_ID);

        syncManager = new SyncManager(getActivity().getApplicationContext(), getActivity());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            setStack(boardId, stackId);
        });
        setStack(boardId, stackId);
        return view;
    }

    private void setStack(long boardId, long stackId) {
        syncManager.getStack(0, boardId, stackId, new IResponseCallback<Stack>(0) {
            @Override
            public void onResponse(Stack response) {
                adapter.setCardList(response.getCards());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e("Deck", throwable.getMessage());
                throwable.printStackTrace();
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