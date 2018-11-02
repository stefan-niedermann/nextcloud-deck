package it.niedermann.nextcloud.deck.ui.stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

public class StackFragment extends Fragment {

    SyncManager syncManager = null;
    CardAdapter adapter = null;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_stack, container, false);
        ButterKnife.bind(this, view);
        adapter = new CardAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        syncManager = new SyncManager(getActivity().getApplicationContext(), getActivity());
        syncManager.getCards(0, 0, new IResponseCallback<List<Card>>() {
            @Override
            public void onResponse(List<Card> response) {
                adapter.setCardList(response);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        return view ;
    }
}