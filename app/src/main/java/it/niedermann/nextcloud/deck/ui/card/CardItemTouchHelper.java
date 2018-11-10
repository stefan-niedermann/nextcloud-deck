package it.niedermann.nextcloud.deck.ui.card;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.List;

public class CardItemTouchHelper extends ItemTouchHelper {
    public CardItemTouchHelper(CardAdapter adapter) {
        super(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP |
                        ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT |
                        ItemTouchHelper.START |
                        ItemTouchHelper.END,
                0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adapter.moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                /* silence is gold. */
            }
        });
    }
}