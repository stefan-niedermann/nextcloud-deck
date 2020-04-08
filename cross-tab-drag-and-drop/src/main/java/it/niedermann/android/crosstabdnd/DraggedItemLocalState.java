package it.niedermann.android.crosstabdnd;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class DraggedItemLocalState<
        TabFragment extends Fragment & DragAndDropTab<ItemAdapter>,
        ItemAdapter extends RecyclerView.Adapter<?> & DragAndDropAdapter<ItemModel>,
        ItemModel> {
    private ItemModel draggedCard;
    /** The original dragged view */
    private final View originalDraggedView;
    /** The currently dragged view (can change when the tab changes */
    private View draggedView;
    private ItemAdapter itemAdapter;
    private int positionInCardAdapter;
    private RecyclerView.OnChildAttachStateChangeListener insertedListener = null;
    private RecyclerView recyclerView = null;
    private long currentTabId;

    public DraggedItemLocalState(ItemModel draggedCard, View draggedView, ItemAdapter itemAdapter, int positionInCardAdapter) {
        this.draggedCard = draggedCard;
        this.draggedView = draggedView;
        this.originalDraggedView = draggedView;
        this.itemAdapter = itemAdapter;
        this.positionInCardAdapter = positionInCardAdapter;
    }

    protected void onDragStart(ViewPager2 viewPager, FragmentManager fm) {
        this.currentTabId = Objects.requireNonNull(viewPager.getAdapter()).getItemId(viewPager.getCurrentItem());
        this.recyclerView = DragAndDropUtil.<TabFragment>getTabFragment(fm, currentTabId).getRecyclerView();
    }

    protected void onTabChanged(ViewPager2 viewPager, FragmentManager fm) {
        if (insertedListener != null) {
            this.recyclerView.removeOnChildAttachStateChangeListener(insertedListener);
            this.insertedListener = null;
        }
        this.currentTabId = Objects.requireNonNull(viewPager.getAdapter()).getItemId(viewPager.getCurrentItem());
        this.recyclerView = DragAndDropUtil.<TabFragment>getTabFragment(fm, currentTabId).getRecyclerView();
        this.itemAdapter = (ItemAdapter) recyclerView.getAdapter();
    }

    protected long getCurrentTabId() {
        return currentTabId;
    }

    protected ItemModel getDraggedItemModel() {
        return draggedCard;
    }

    protected View getOriginalDraggedView() {
        return originalDraggedView;
    }

    protected View getDraggedView() {
        return draggedView;
    }

    protected void setDraggedView(View draggedView) {
        this.draggedView = draggedView;
    }

    protected ItemAdapter getItemAdapter() {
        return itemAdapter;
    }

    protected int getPositionInItemAdapter() {
        return positionInCardAdapter;
    }

    protected void setPositionInItemAdapter(int positionInCardAdapter) {
        this.positionInCardAdapter = positionInCardAdapter;
    }

    protected void setInsertedListener(RecyclerView.OnChildAttachStateChangeListener insertedListener) {
        this.insertedListener = insertedListener;
    }

    public RecyclerView.OnChildAttachStateChangeListener getInsertedListener() {
        return insertedListener;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

}
