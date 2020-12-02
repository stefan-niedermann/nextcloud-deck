package it.niedermann.android.markdown.markwon.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;

import static it.niedermann.android.markdown.markwon.textwatcher.AutoContinuationTextWatcher.EListType.LENGTH_CHECKBOX_WITH_TRAILING_SPACE;
import static it.niedermann.android.markdown.markwon.textwatcher.AutoContinuationTextWatcher.EListType.LENGTH_LIST_WITH_TRAILING_SPACE;

/**
 * Automatically continues lists and checkbox lists when pressing enter
 */
public class AutoContinuationTextWatcher implements TextWatcher {

    private final MarkwonEditorTextWatcher originalWatcher;
    private final MarkwonMarkdownEditor editText;

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
        final int numberOfInsertedCharactersBeforeLine = autoContinueCheckboxListsOnEnter(s, start, count);
        // TODO how to use this information for the originalWatcher?
        originalWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        originalWatcher.afterTextChanged(s);
    }

    /**
     * @return added characters in front of the current line (can be negative)
     */
    private int autoContinueCheckboxListsOnEnter(@NonNull CharSequence originalSequence, int start, int count) {
        if (count != 1 || originalSequence.charAt(start) != '\n') {
            return 0;
        }
        final CharSequence s = originalSequence.subSequence(0, originalSequence.length());
        final int startOfLine = getStartOfLine(s, start);
        final String line = s.subSequence(startOfLine, start).toString();

        if (lineStartsWithEmptyUncheckedCheckbox(line)) {
            editText.setSelection(startOfLine + 1);
            setNewText(new StringBuilder(s).replace(startOfLine, startOfLine + LENGTH_CHECKBOX_WITH_TRAILING_SPACE + 1, "\n"), startOfLine + 1);
            return LENGTH_CHECKBOX_WITH_TRAILING_SPACE * -1;
        } else if (lineStartsWithEmptyList(line)) {
            editText.setSelection(startOfLine + 1);
            setNewText(new StringBuilder(s).replace(startOfLine, startOfLine + LENGTH_LIST_WITH_TRAILING_SPACE + 1, "\n"), startOfLine + 1);
            return LENGTH_LIST_WITH_TRAILING_SPACE * -1;
        } else {
            for (EListType listType : EListType.values()) {
                if (lineStartsWithCheckbox(line, listType)) {
                    setNewText(new StringBuilder(s).insert(start + count, listType.checkboxUncheckedWithTrailingSpace), start + LENGTH_CHECKBOX_WITH_TRAILING_SPACE + 1);
                    return LENGTH_CHECKBOX_WITH_TRAILING_SPACE;
                }
            }
            for (EListType listType : EListType.values()) {
                if (lineStartsWithList(line, listType)) {
                    setNewText(new StringBuilder(s).insert(start + count, listType.listSymbolWithTrailingSpace), start + LENGTH_LIST_WITH_TRAILING_SPACE + 1);
                    return LENGTH_LIST_WITH_TRAILING_SPACE;
                }
            }
        }
        return 0;
    }

    private void setNewText(@NonNull StringBuilder newText, int selection) {
        editText.setMarkdownString(newText.toString());
        editText.setSelection(selection);
    }

    private static int getStartOfLine(@NonNull CharSequence s, int cursorPosition) {
        int startOfLine = cursorPosition;
        while (startOfLine > 0 && s.charAt(startOfLine - 1) != '\n') {
            startOfLine--;
        }
        return startOfLine;
    }

    private static boolean lineStartsWithEmptyUncheckedCheckbox(@NonNull String line) {
        for (EListType listType : EListType.values()) {
            if (line.equals(listType.checkboxUncheckedWithTrailingSpace)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lineStartsWithEmptyList(@NonNull String line) {
        for (EListType listType : EListType.values()) {
            if (line.equals(listType.listSymbolWithTrailingSpace)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lineStartsWithCheckbox(@NonNull String line, @NonNull EListType listType) {
        return line.startsWith(String.valueOf(listType.checkboxUnchecked)) || line.startsWith(listType.checkboxChecked);
    }

    private static boolean lineStartsWithList(@NonNull String line, @NonNull EListType listType) {
        return line.startsWith(String.valueOf(listType.listSymbol));
    }

    enum EListType {
        STAR('*'),
        DASH('-'),
        PLUS('+');

        static final int LENGTH_LIST_WITH_TRAILING_SPACE = 2;
        static final int LENGTH_CHECKBOX_WITH_TRAILING_SPACE = 6;

        final char listSymbol;
        final String listSymbolWithTrailingSpace;
        final String checkboxChecked;
        final String checkboxUnchecked;
        final String checkboxUncheckedWithTrailingSpace;

        EListType(char listSymbol) {
            this.listSymbol = listSymbol;
            this.listSymbolWithTrailingSpace = listSymbol + " ";
            this.checkboxChecked = listSymbolWithTrailingSpace + "[x]";
            this.checkboxUnchecked = listSymbolWithTrailingSpace + "[ ]";
            this.checkboxUncheckedWithTrailingSpace = checkboxUnchecked + " ";
        }
    }
}
