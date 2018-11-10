package it.niedermann.nextcloud.deck.ui.card;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Card card = cardList.get(position);
        ((CardViewHolder) viewHolder).cardTitle.setText(card.getTitle());
        ((CardViewHolder) viewHolder).cardDescription.setText(card.getDescription());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void setCardList(@NonNull List<Card> cardList) {
        this.cardList = cardList;
        notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {
        cardList.add(toPosition, cardList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_title)
        TextView cardTitle;
        @BindView(R.id.card_description)
        TextView cardDescription;

        private CardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
