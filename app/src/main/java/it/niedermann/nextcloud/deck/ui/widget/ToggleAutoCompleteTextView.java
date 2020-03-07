package it.niedermann.nextcloud.deck.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.AdapterView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import it.niedermann.nextcloud.deck.DeckLog;

/**
 * This AutoCompleteTextView implementation closes the dropdown on each second click
 */
public class ToggleAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private boolean hideOnNextClick = false;

    public ToggleAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener((v) -> {
            if (hideOnNextClick) {
                DeckLog.log("clicked twice, hide dropdown");
                dismissDropDown();
            } else {
                DeckLog.log("clicked once, hide dropdown");
                performFiltering(getText(), 0);
                showDropDown();
            }
            hideOnNextClick = !hideOnNextClick;
        });
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        super.setOnItemClickListener((parent, view, position, id) -> {
            DeckLog.log("clicked ITEM SELECTED");
            hideOnNextClick = false;
            l.onItemClick(parent, view, position, id);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setOnDismissListener(OnDismissListener dismissListener) {
        super.setOnDismissListener(() -> {
            dismissListener.onDismiss();
            hideOnNextClick = true;
        });
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            DeckLog.log("clicked FOCUSED, show dropdown");
            hideOnNextClick = true;
            performFiltering(getText(), 0);
            showDropDown();
        } else {
            DeckLog.log("clicked FOCUSED leave");
            hideOnNextClick = false;
        }
    }
}
