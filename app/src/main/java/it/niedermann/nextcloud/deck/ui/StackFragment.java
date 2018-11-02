package it.niedermann.nextcloud.deck.ui;

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
import it.niedermann.nextcloud.deck.model.Card;

public class StackFragment extends Fragment {

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
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card(1, "Card 1"));
        cardList.add(new Card(2, "Card 2"));
        cardList.add(new Card(3, "Card 3"));
        cardList.add(new Card(4, "Card 4"));
        adapter.setCardList(new ArrayList<Card>(cardList));
        return view ;
    }
}