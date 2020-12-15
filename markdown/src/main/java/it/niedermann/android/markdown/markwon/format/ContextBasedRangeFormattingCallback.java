package it.niedermann.android.markdown.markwon.format;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import it.niedermann.android.markdown.R;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;
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
        final SpannableStringBuilder ssb = new SpannableStringBuilder(editText.getText());
        final int itemId = item.getItemId();
        final int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (itemId == R.id.bold) {
            final String punctuation = "**";
            final boolean hasAlreadyMarkdown = hasAlreadyMarkdown(editText.getText(), start, end, punctuation);
            if (hasAlreadyMarkdown) {
                removeMarkdown(ssb, start, end, punctuation);
            } else {
                ssb.insert(end, punctuation);
                ssb.insert(start, punctuation);
            }
            editText.setMarkdownString(ssb);
            editText.setSelection(hasAlreadyMarkdown ? end - punctuation.length() : end + punctuation.length() * 2);
            return true;
        } else if (itemId == R.id.italic) {
            final String punctuation = "*";
            final boolean hasAlreadyMarkdown = hasAlreadyMarkdown(editText.getText(), start, end, punctuation);
            if (hasAlreadyMarkdown) {
                removeMarkdown(ssb, start, end, punctuation);
            } else {
                ssb.insert(end, punctuation);
                ssb.insert(start, punctuation);
            }
            editText.setMarkdownString(ssb);
            editText.setSelection(hasAlreadyMarkdown ? end - punctuation.length() : end + punctuation.length() * 2);
            return true;
        } else if (itemId == R.id.link) {
            final CharSequence text = editText.getText();
            final boolean textToFormatIsLink = text != null && TextUtils.indexOf(text.subSequence(start, end), "http") == 0;
            if (textToFormatIsLink) {
                ssb.insert(end, ")");
                ssb.insert(start, "[](");
            } else {
                String clipboardURL = ClipboardUtil.INSTANCE.getClipboardURLorNull(editText.getContext());
                if (clipboardURL != null) {
                    ssb.insert(end, "](" + clipboardURL + ")");
                    end += clipboardURL.length();
                } else {
                    ssb.insert(end, "]()");
                }
                ssb.insert(start, "[");
            }
            editText.setMarkdownString(ssb);
            if (textToFormatIsLink) {
                editText.setSelection(start + 1);
            } else {
                editText.setSelection(end + 3); // after <end>](
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Nothing to do here...
    }

    private static boolean hasAlreadyMarkdown(@Nullable CharSequence text, int start, int end, String punctuation) {
        return text != null && (start > punctuation.length() && punctuation.contentEquals(text.subSequence(start - punctuation.length(), start)) &&
                text.length() > end + punctuation.length() && punctuation.contentEquals(text.subSequence(end, end + punctuation.length())));
    }

    private static void removeMarkdown(SpannableStringBuilder ssb, int start, int end, String punctuation) {
        // FIXME disabled, because it does not work properly and might cause data loss
        ssb.delete(start - punctuation.length(), start);
        ssb.delete(end - punctuation.length(), end);
    }
}
