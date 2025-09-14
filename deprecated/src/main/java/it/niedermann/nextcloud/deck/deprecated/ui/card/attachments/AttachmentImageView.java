package it.niedermann.nextcloud.deck.deprecated.ui.card.attachments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otaliastudios.zoom.ZoomImageView;

public class AttachmentImageView extends ZoomImageView {
    public AttachmentImageZoomListener zoomListener;
    private GestureDetector gestureDetector;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        gestureDetector = new GestureDetector(getContext(), new GestureListener());

        if (getContext() instanceof AttachmentImageZoomListener) {
            this.zoomListener = (AttachmentImageZoomListener) getContext();
        } else {
            throw new ClassCastException("Caller must implement " + AttachmentImageZoomListener.class.getCanonicalName());
        }
    }

    public AttachmentImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        int pointerCount = ev.getPointerCount();

        boolean canChange = pointerCount == 1 && getZoom() <= 1;
        zoomListener.onAbleToChangePage(canChange);

        gestureDetector.onTouchEvent(ev);

        return super.onTouchEvent(ev);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float zoomLevel = 2;
            zoomTo(getZoom() < zoomLevel ? zoomLevel : 1, true);

            return true;
        }
    }
}
