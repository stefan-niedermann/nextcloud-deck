package it.niedermann.android.markdown.markwon.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;

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
                final boolean isPlainList = !isCheckboxList && lineStartsWithList(line, listType);
                if (isPlainList || isCheckboxList) {
                    customText = isPlainList ? listType.listSymbolWithTrailingSpace : listType.checkboxUncheckedWithTrailingSpace;
                    isInsert = true;
                    sequenceStart = start + count;
                }
            }
        }
    }

    private static int getStartOfLine(@NonNull CharSequence s, int cursorPosition) {
        int startOfLine = cursorPosition;
        while (startOfLine > 0 && s.charAt(startOfLine - 1) != '\n') {
            startOfLine--;
        }
        return startOfLine;
    }

    private static String getListItemIfIsEmpty(@NonNull String line) {
        for (EListType listType : EListType.values()) {
            if (line.equals(listType.checkboxUncheckedWithTrailingSpace)) {
                return listType.checkboxUncheckedWithTrailingSpace;
            } else if (line.equals(listType.listSymbolWithTrailingSpace)) {
                return listType.listSymbolWithTrailingSpace;
            }
        }
        return null;
    }

    private static boolean lineStartsWithCheckbox(@NonNull String line, @NonNull EListType listType) {
        return line.startsWith(listType.checkboxUnchecked) || line.startsWith(listType.checkboxChecked);
    }

    private static boolean lineStartsWithList(@NonNull String line, @NonNull EListType listType) {
        return line.startsWith(listType.listSymbol);
    }

    enum EListType {
        STAR('*'),
        DASH('-'),
        PLUS('+');

        final String listSymbol;
        final String listSymbolWithTrailingSpace;
        final String checkboxChecked;
        final String checkboxUnchecked;
        final String checkboxUncheckedWithTrailingSpace;

        EListType(char listSymbol) {
            this.listSymbol = String.valueOf(listSymbol);
            this.listSymbolWithTrailingSpace = listSymbol + " ";
            this.checkboxChecked = listSymbolWithTrailingSpace + "[x]";
            this.checkboxUnchecked = listSymbolWithTrailingSpace + "[ ]";
            this.checkboxUncheckedWithTrailingSpace = checkboxUnchecked + " ";
        }
    }
}
