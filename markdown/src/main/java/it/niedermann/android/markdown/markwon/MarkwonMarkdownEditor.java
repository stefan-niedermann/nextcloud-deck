package it.niedermann.android.markdown.markwon;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import it.niedermann.android.markdown.MarkdownEditor;
import it.niedermann.android.markdown.markwon.handler.BlockQuoteEditHandler;
import it.niedermann.android.markdown.markwon.handler.CodeBlockEditHandler;
import it.niedermann.android.markdown.markwon.handler.CodeEditHandler;
import it.niedermann.android.markdown.markwon.handler.HeadingEditHandler;
import it.niedermann.android.markdown.markwon.handler.StrikethroughEditHandler;

public class MarkwonMarkdownEditor extends AppCompatEditText implements MarkdownEditor {

    private static final String CHECKBOX_UNCHECKED_MINUS = "- [ ]";
    private static final String CHECKBOX_UNCHECKED_MINUS_TRAILING_SPACE = CHECKBOX_UNCHECKED_MINUS + " ";
    private static final String CHECKBOX_UNCHECKED_STAR = "* [ ]";
    private static final String CHECKBOX_UNCHECKED_STAR_TRAILING_SPACE = CHECKBOX_UNCHECKED_STAR + " ";
    private static final String CHECKBOX_CHECKED_MINUS = "- [x]";
    private static final String CHECKBOX_CHECKED_STAR = "* [x]";

    private static final int lengthCheckbox = 6;

    private final MutableLiveData<CharSequence> unrenderedText$ = new MutableLiveData<>();

    public MarkwonMarkdownEditor(@NonNull Context context) {
        this(context, null);
    }

    public MarkwonMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MarkwonMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Markwon markwon = MarkwonMarkdownUtil.initMarkwonEditor(context).build();
        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .useEditHandler(new EmphasisEditHandler())
                .useEditHandler(new StrongEmphasisEditHandler())
                .useEditHandler(new StrikethroughEditHandler())
                .useEditHandler(new CodeEditHandler())
                .useEditHandler(new CodeBlockEditHandler())
                .useEditHandler(new BlockQuoteEditHandler())
                .useEditHandler(new HeadingEditHandler())
                .build();

        MarkwonEditorTextWatcher originalWatcher = MarkwonEditorTextWatcher.withPreRender(editor, Executors.newSingleThreadExecutor(), this);

        // intercept behavior of the original MarkwonEditorTextWatcher.
        addTextChangedListener(new TextWatcher() {
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
//                unrenderedText$.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                originalWatcher.afterTextChanged(s);
            }
        });
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        setText(text);
        unrenderedText$.setValue(text == null ? "" : text.toString());
    }

    @Override
    public LiveData<CharSequence> getMarkdownString() {
        return unrenderedText$;
    }

    private boolean autoContinueCheckboxListsOnEnter(@NonNull CharSequence originalSequence, int start, int count) {
        final CharSequence s = originalSequence.subSequence(0, originalSequence.length());
        final int startOfLine = getStartOfLine(s, start);
        final String line = s.subSequence(startOfLine, start).toString();

        if (line.equals(CHECKBOX_UNCHECKED_MINUS_TRAILING_SPACE) || line.equals(CHECKBOX_UNCHECKED_STAR_TRAILING_SPACE)) {
            setSelection(startOfLine + 1);
            setNewText(new StringBuilder(s).replace(startOfLine, startOfLine + lengthCheckbox + 1, "\n"), startOfLine + 1);
            return true;
        } else if (lineStartsWithCheckbox(line, false)) {
            setNewText(new StringBuilder(s).insert(start + count, CHECKBOX_UNCHECKED_MINUS_TRAILING_SPACE), start + lengthCheckbox + 1);
            return true;
        } else if (lineStartsWithCheckbox(line, true)) {
            setNewText(new StringBuilder(s).insert(start + count, CHECKBOX_UNCHECKED_STAR_TRAILING_SPACE), start + lengthCheckbox + 1);
            return true;
        }
        return false;
    }

    private void setNewText(@NonNull StringBuilder newText, int selection) {
        setMarkdownString(newText.toString());
        unrenderedText$.setValue(newText.toString());
        setSelection(selection);
    }

    private static int getStartOfLine(@NonNull CharSequence s, int cursorPosition) {
        int startOfLine = cursorPosition;
        while (startOfLine > 0 && s.charAt(startOfLine - 1) != '\n') {
            startOfLine--;
        }
        return startOfLine;
    }

    private static boolean lineStartsWithCheckbox(@NonNull String line, boolean starAsLeadingCharacter) {
        return starAsLeadingCharacter
                ? line.startsWith(CHECKBOX_UNCHECKED_STAR) || line.startsWith(CHECKBOX_CHECKED_STAR)
                : line.startsWith(CHECKBOX_UNCHECKED_MINUS) || line.startsWith(CHECKBOX_CHECKED_MINUS);
    }
}
