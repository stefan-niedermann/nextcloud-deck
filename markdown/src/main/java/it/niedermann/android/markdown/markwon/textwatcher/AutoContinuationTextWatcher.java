package it.niedermann.android.markdown.markwon.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;
import it.niedermann.android.markdown.markwon.model.EListType;

import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.getListItemIfIsEmpty;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.getOrderedListNumber;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.getStartOfLine;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.lineStartsWithCheckbox;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.lineStartsWithUnorderedList;

/**
 * Automatically continues lists and checkbox lists when pressing enter
 */
public class AutoContinuationTextWatcher implements TextWatcher {

    private final MarkwonEditorTextWatcher originalWatcher;
    private final MarkwonMarkdownEditor editText;

    private CharSequence customText = null;
    private boolean isInsert = true;
    private int sequenceStart = 0;

    public AutoContinuationTextWatcher(@NonNull MarkwonEditor editor, @NonNull MarkwonMarkdownEditor editText) {
        this.editText = editText;
        originalWatcher = MarkwonEditorTextWatcher.withPreRender(editor, Executors.newSingleThreadExecutor(), editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        originalWatcher.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 1 && s.charAt(start) == '\n') {
            handleNewlineInserted(s, start, count);
        }
        originalWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (customText != null) {
            final CharSequence customText = this.customText;
            this.customText = null;
            if (isInsert) {
                insertCustomText(s, customText);
            } else {
                deleteCustomText(s, customText);
            }
        } else {
            originalWatcher.afterTextChanged(s);
        }
        editText.setMarkdownStringModel(s);
    }

    private void deleteCustomText(Editable s, CharSequence customText) {
        s.replace(sequenceStart, sequenceStart + customText.length() + 1, "\n");
        editText.setSelection(sequenceStart + 1);
    }

    private void insertCustomText(Editable s, CharSequence customText) {
        s.insert(sequenceStart, customText);
    }

    private void handleNewlineInserted(CharSequence originalSequence, int start, int count) {
        final CharSequence s = originalSequence.subSequence(0, originalSequence.length());
        final int startOfLine = getStartOfLine(s, start);
        final String line = s.subSequence(startOfLine, start).toString();

        final String emptyListString = getListItemIfIsEmpty(line);
        if (emptyListString != null) {
            customText = emptyListString;
            isInsert = false;
            sequenceStart = startOfLine;
        } else {
            for (EListType listType : EListType.values()) {
                final boolean isCheckboxList = lineStartsWithCheckbox(line, listType);
                final boolean isPlainList = !isCheckboxList && lineStartsWithUnorderedList(line, listType);
                if (isPlainList || isCheckboxList) {
                    customText = isPlainList ? listType.listSymbolWithTrailingSpace : listType.checkboxUncheckedWithTrailingSpace;
                    isInsert = true;
                    sequenceStart = start + count;
                    return;
                }
            }
            final int orderedListNumber = getOrderedListNumber(line);
            if (orderedListNumber >= 0) {
                customText = (orderedListNumber + 1) + ". ";
                isInsert = true;
                sequenceStart = start + count;
            }
        }
    }
}
