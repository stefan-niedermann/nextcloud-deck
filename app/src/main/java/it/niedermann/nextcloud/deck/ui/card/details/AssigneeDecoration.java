package it.niedermann.nextcloud.deck.ui.card.details;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

public class AssigneeDecoration extends RecyclerView.ItemDecoration {

    private final int gutter;

    public AssigneeDecoration(@Px int gutter) {
        this.gutter = gutter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);

        if (position >= 0) {
            // All columns get some spacing at the bottom and at the right side
            outRect.right = gutter;
            outRect.bottom = gutter;
        }
    }
}
