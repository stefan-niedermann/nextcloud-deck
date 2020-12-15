package it.niedermann.android.markdown.markwon.format;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import it.niedermann.android.markdown.R;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;
import it.niedermann.android.markdown.markwon.model.EListType;
import it.niedermann.android.util.ClipboardUtil;

import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.getEndOfLine;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.getStartOfLine;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.lineStartsWithCheckbox;

public class ContextBasedFormattingCallback implements ActionMode.Callback {

    private static final String TAG = ContextBasedFormattingCallback.class.getSimpleName();

    private final MarkwonMarkdownEditor editText;

    public ContextBasedFormattingCallback(MarkwonMarkdownEditor editText) {
        this.editText = editText;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.context_based_formatting, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        final CharSequence text = editText.getText();
        if (text != null) {
            final int cursorPosition = editText.getSelectionStart();
            if (cursorPosition >= 0 && cursorPosition <= text.length()) {
                final int startOfLine = getStartOfLine(text, cursorPosition);
                final int endOfLine = getEndOfLine(text, startOfLine);
                final String line = text.subSequence(startOfLine, endOfLine).toString();
                if (lineStartsWithCheckbox(line)) {
                    menu.findItem(R.id.checkbox).setVisible(false);
                    Log.i(TAG, "Hide checkbox menu item because line starts already with checkbox");
                }
            } else {
                Log.e(TAG, "SelectionStart is " + cursorPosition + ". Expected to be between 0 and " + text.length());
            }
        }
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.checkbox) {
            insertCheckbox();
            return true;
        } else if (itemId == R.id.link) {
            insertLink();
            return true;
        }
        return false;
    }

    private void insertCheckbox() {
        final CharSequence text = editText.getText();
        if (text == null) {
            editText.setMarkdownString(EListType.DASH.checkboxUncheckedWithTrailingSpace);
            editText.setSelection(EListType.DASH.checkboxUncheckedWithTrailingSpace.length());
        } else {
            final int originalCursorPosition = editText.getSelectionStart();
            final int startOfLine = getStartOfLine(text, originalCursorPosition);
            Log.i(TAG, "Inserting checkbox at position " + startOfLine);
            final CharSequence part1 = text.subSequence(0, startOfLine);
            final CharSequence part2 = text.subSequence(startOfLine, text.length());
            editText.setMarkdownString(TextUtils.concat(part1, EListType.DASH.checkboxUncheckedWithTrailingSpace, part2));
            editText.setSelection(originalCursorPosition + EListType.DASH.checkboxUncheckedWithTrailingSpace.length());
        }
    }

    private void insertLink() {
        final CharSequence text = editText.getText();
        final SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        final boolean textToFormatIsLink;
        final int start;
        int end;
        if (text == null) {
            start = end = 0;
            textToFormatIsLink = false;
        } else {
            start = text.length();
            end = start;
            textToFormatIsLink = TextUtils.indexOf(text.subSequence(start, end), "http") == 0;
        }

        if (textToFormatIsLink) {
            Log.i(TAG, "Inserting link description for position " + start + " to " + end);
            ssb.insert(end, ")");
            ssb.insert(start, "[](");
        } else {
            String clipboardURL = ClipboardUtil.INSTANCE.getClipboardURLorNull(editText.getContext());
            if (clipboardURL != null) {
                Log.i(TAG, "Inserting link from clipboard at position " + start + " to " + end + ": " + clipboardURL);
                ssb.insert(end, "](" + clipboardURL + ")");
                end += clipboardURL.length();
            } else {
                Log.i(TAG, "Inserting empty link for position " + start + " to " + end);
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
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Nothing to do here...
    }
}
