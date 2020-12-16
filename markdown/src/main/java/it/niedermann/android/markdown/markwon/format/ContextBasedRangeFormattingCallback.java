package it.niedermann.android.markdown.markwon.format;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import it.niedermann.android.markdown.R;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil;
import it.niedermann.android.util.ClipboardUtil;

public class ContextBasedRangeFormattingCallback implements ActionMode.Callback {

    private static final String TAG = ContextBasedRangeFormattingCallback.class.getSimpleName();

    private final MarkwonMarkdownEditor editText;

    public ContextBasedRangeFormattingCallback(MarkwonMarkdownEditor editText) {
        this.editText = editText;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.context_based_range_formatting, menu);

        final SparseIntArray styleFormatMap = new SparseIntArray();
        styleFormatMap.append(R.id.bold, Typeface.BOLD);
        styleFormatMap.append(R.id.italic, Typeface.ITALIC);

        MenuItem item;
        CharSequence title;
        SpannableStringBuilder ssb;

        for (int i = 0; i < styleFormatMap.size(); i++) {
            item = menu.findItem(styleFormatMap.keyAt(i));
            title = item.getTitle();
            ssb = new SpannableStringBuilder(title);
            ssb.setSpan(new StyleSpan(styleFormatMap.valueAt(i)), 0, title.length(), 0);
            item.setTitle(ssb);
        }

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO hide actions if not available?
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        final Editable editable = editText.getText();
        final StringBuilder ssb = new StringBuilder(editable == null ? "" : editable.toString());
        final int itemId = item.getItemId();
        final int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (itemId == R.id.bold) {
            final int newSelection = MarkwonMarkdownUtil.togglePunctuation(ssb, start, end, "**");
            editText.setMarkdownString(ssb);
            editText.setSelection(newSelection);
            return true;
        } else if (itemId == R.id.italic) {
            final int newSelection = MarkwonMarkdownUtil.togglePunctuation(ssb, start, end, "*");
            editText.setMarkdownString(ssb);
            editText.setSelection(newSelection);
            return true;
        } else if (itemId == R.id.link) {
            final int newSelection = MarkwonMarkdownUtil.insertLink(ssb, start, end, ClipboardUtil.INSTANCE.getClipboardURLorNull(editText.getContext()));
            editText.setMarkdownString(ssb);
            editText.setSelection(newSelection);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Nothing to do here...
    }
}
