package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryItemDecoration extends RecyclerView.ItemDecoration {

    @Px
    private final int gutter;

    public GalleryItemDecoration(@Px int gutter) {
        this.gutter = gutter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        if (position >= 0) {
            outRect.left = gutter;
            outRect.top = gutter;
            outRect.right = gutter;
            outRect.bottom = gutter;
        }
    }
}
