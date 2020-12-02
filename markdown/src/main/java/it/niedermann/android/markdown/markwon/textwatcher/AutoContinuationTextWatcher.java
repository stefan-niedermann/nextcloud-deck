package it.niedermann.android.markdown.markwon.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;

/**
 * Automatically continues lists and checkbox lists when pressing enter
 */
public class AutoContinuationTextWatcher implements TextWatcher {

    private static final String TAG = AutoContinuationTextWatcher.class.getSimpleName();

    private static final String LIST_DASH = "-";
    private static final String LIST_STAR = "*";
    private static final String LIST_PLUS = "+";
    private static final String LIST_DASH_TRAILING = LIST_DASH + " ";
    private static final String LIST_STAR_TRAILING = LIST_STAR + " ";
    private static final String LIST_PLUS_TRAILING = LIST_PLUS + " ";
    private static final String CHECKBOX_CHECKED_DASH = "- [x]";
    private static final String CHECKBOX_CHECKED_STAR = "* [x]";
    private static final String CHECKBOX_CHECKED_PLUS = "+ [x]";
    private static final String CHECKBOX_UNCHECKED_DASH = "- [ ]";
    private static final String CHECKBOX_UNCHECKED_STAR = "* [ ]";
    private static final String CHECKBOX_UNCHECKED_PLUS = "+ [ ]";
    private static final String CHECKBOX_UNCHECKED_DASH_TRAILING_SPACE = CHECKBOX_UNCHECKED_DASH + " ";
    private static final String CHECKBOX_UNCHECKED_STAR_TRAILING_SPACE = CHECKBOX_UNCHECKED_STAR + " ";
    private static final String CHECKBOX_UNCHECKED_PLUS_TRAILING_SPACE = CHECKBOX_UNCHECKED_PLUS + " ";

    private static final int LIST_TRAILING_SPACE_LENGTH = 2;
    private static final int CHECKBOX_TRAILING_SPACE_LENGTH = 6;

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
        if (count == 1 && s.charAt(start) == '\n') {
            autoContinueCheckboxListsOnEnter(s, start, count);
        }
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
        final CharSequence s = originalSequence.subSequence(0, originalSequence.length());
        final int startOfLine = getStartOfLine(s, start);
        final String line = s.subSequence(startOfLine, start).toString();

        if (line.equals(CHECKBOX_UNCHECKED_DASH_TRAILING_SPACE) || line.equals(CHECKBOX_UNCHECKED_STAR_TRAILING_SPACE)) {
            editText.setSelection(startOfLine + 1);
            setNewText(new StringBuilder(s).replace(startOfLine, startOfLine + CHECKBOX_TRAILING_SPACE_LENGTH + 1, "\n"), startOfLine + 1);
            return CHECKBOX_TRAILING_SPACE_LENGTH * -1;
        } else if (line.equals(LIST_DASH_TRAILING) || line.equals(LIST_PLUS_TRAILING) || line.equals(LIST_STAR_TRAILING)) {
            editText.setSelection(startOfLine + 1);
            setNewText(new StringBuilder(s).replace(startOfLine, startOfLine + LIST_TRAILING_SPACE_LENGTH + 1, "\n"), startOfLine + 1);
            return CHECKBOX_TRAILING_SPACE_LENGTH;
        } else if (lineStartsWithCheckbox(line, EListType.DASH)) {
            setNewText(new StringBuilder(s).insert(start + count, CHECKBOX_UNCHECKED_DASH_TRAILING_SPACE), start + CHECKBOX_TRAILING_SPACE_LENGTH + 1);
            return CHECKBOX_TRAILING_SPACE_LENGTH;
        } else if (lineStartsWithCheckbox(line, EListType.STAR)) {
            setNewText(new StringBuilder(s).insert(start + count, CHECKBOX_UNCHECKED_STAR_TRAILING_SPACE), start + CHECKBOX_TRAILING_SPACE_LENGTH + 1);
            return CHECKBOX_TRAILING_SPACE_LENGTH;
        } else if (lineStartsWithCheckbox(line, EListType.PLUS)) {
            setNewText(new StringBuilder(s).insert(start + count, CHECKBOX_UNCHECKED_PLUS_TRAILING_SPACE), start + CHECKBOX_TRAILING_SPACE_LENGTH + 1);
            return CHECKBOX_TRAILING_SPACE_LENGTH;
        } else if (lineStartsWithList(line, EListType.DASH)) {
            setNewText(new StringBuilder(s).insert(start + count, LIST_DASH_TRAILING), start + LIST_TRAILING_SPACE_LENGTH + 1);
            return LIST_TRAILING_SPACE_LENGTH;
        } else if (lineStartsWithList(line, EListType.STAR)) {
            setNewText(new StringBuilder(s).insert(start + count, LIST_STAR_TRAILING), start + LIST_TRAILING_SPACE_LENGTH + 1);
            return LIST_TRAILING_SPACE_LENGTH;
        } else if (lineStartsWithList(line, EListType.PLUS)) {
            setNewText(new StringBuilder(s).insert(start + count, LIST_PLUS_TRAILING), start + LIST_TRAILING_SPACE_LENGTH + 1);
            return LIST_TRAILING_SPACE_LENGTH;
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

    private static boolean lineStartsWithCheckbox(@NonNull String line, @NonNull EListType listType) {
        switch (listType) {
            case STAR:
                return line.startsWith(CHECKBOX_UNCHECKED_STAR) || line.startsWith(CHECKBOX_CHECKED_STAR);
            case DASH:
                return line.startsWith(CHECKBOX_UNCHECKED_DASH) || line.startsWith(CHECKBOX_CHECKED_DASH);
            case PLUS:
                return line.startsWith(CHECKBOX_UNCHECKED_PLUS) || line.startsWith(CHECKBOX_CHECKED_PLUS);
            default:
                Log.w(TAG, "List type " + listType + " is not supported.");
                return false;
        }
    }

    private static boolean lineStartsWithList(@NonNull String line, @NonNull EListType listType) {
        switch (listType) {
            case STAR:
                return line.startsWith(LIST_STAR);
            case DASH:
                return line.startsWith(LIST_DASH);
            case PLUS:
                return line.startsWith(LIST_PLUS);
            default:
                Log.w(TAG, "List type " + listType + " is not supported.");
                return false;
        }
    }

    private enum EListType {
        STAR,
        DASH,
        PLUS
    }
}
