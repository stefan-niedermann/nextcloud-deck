package it.niedermann.android.crosstabdnd;

import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("WeakerAccess")
public class ScrollHelper implements Runnable {

    private static final int SROLL_SPEED = 200;

    public enum ScrollDirection {
        UP,
        DOWN
    }

    private boolean shouldScroll = false;
    private ScrollDirection scrollDirection;
    private RecyclerView currentRecyclerView;
    private Handler handler = new Handler();

    public void startScroll(RecyclerView recyclerView, ScrollDirection scrollDirection) {
        this.scrollDirection = scrollDirection;
        this.currentRecyclerView = recyclerView;
        if (!shouldScroll) {
            this.shouldScroll = true;
            handler.post(this);
        }
    }

    public void stopScroll() {
        this.shouldScroll = false;
    }

    @Override
    public void run() {
        if (scrollDirection == ScrollDirection.UP) {
            currentRecyclerView.smoothScrollBy(0, SROLL_SPEED * -1);
        } else {
            currentRecyclerView.smoothScrollBy(0, SROLL_SPEED);
        }
        if (shouldScroll) {
            handler.postDelayed(this, 100);
        }
    }
}