package it.niedermann.nextcloud.deck.ui.stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.CardItemTouchHelper;

public class StackFragment extends Fragment {

    private long boardId = 0;
    private long stackId = 0;
    private CardAdapter adapter = null;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    /**
     * @see <a href="https://gunhansancar.com/best-practice-to-instantiate-fragments-with-arguments-in-android/">Best Practice to Instantiate Fragments with Arguments in Android</a>
     * @param boardId of the current stack
     * @return new fragment instance
     */
    public static StackFragment newInstance(long boardId, long stackId) {
        Bundle bundle = new Bundle();
        bundle.putLong("boardId", boardId);
        bundle.putLong("stackId", stackId);

        StackFragment fragment = new StackFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stack, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        if(savedInstanceState != null) {
            this.boardId = savedInstanceState.getLong("boardId");
            this.stackId = savedInstanceState.getLong("stackId");
        }
        SyncManager syncManager = new SyncManager(getActivity().getApplicationContext(), getActivity());
        syncManager.getStack(0, boardId, stackId, new IResponseCallback<Stack>(0) {
            @Override
            public void onResponse(Stack response) {
                // TODO set list of cards to adapter
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("Deck", throwable.getMessage());
                throwable.printStackTrace();
            }
        });
        return view ;
    }

    private void initRecyclerView() {
        adapter = new CardAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper touchHelper = new CardItemTouchHelper(adapter);
        touchHelper.attachToRecyclerView(recyclerView);
    }
}