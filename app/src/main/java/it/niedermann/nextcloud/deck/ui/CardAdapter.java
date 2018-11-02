package it.niedermann.nextcloud.deck.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private List<Card> cardList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_card, viewGroup, false);
            return new CardAdapter.CardViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            Card card = cardList.get(position);
            ((CardAdapter.CardViewHolder) viewHolder).cardTitle.setText(card.getTitle());
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }

        void setCardList(@NonNull List<Card> cardList) {
            this.cardList = cardList;
            notifyDataSetChanged();
        }
    static class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.board_title)
        TextView cardTitle;

        private CardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
