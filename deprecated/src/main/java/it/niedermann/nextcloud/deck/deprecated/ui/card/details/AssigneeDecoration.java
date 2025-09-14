package it.niedermann.nextcloud.deck.deprecated.ui.card.details;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

public class AssigneeDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int gutter;

    public AssigneeDecoration(int spanCount, @Px int gutter) {
        this.spanCount = spanCount;
        this.gutter = gutter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        outRect.left = column * gutter / spanCount;
        outRect.right = gutter - (column + 1) * gutter / spanCount;

        if (position >= spanCount) {
            outRect.top = gutter;
        }
    }
}
