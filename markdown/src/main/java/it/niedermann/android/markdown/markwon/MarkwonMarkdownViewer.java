package it.niedermann.android.markdown.markwon;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import io.noties.markwon.Markwon;
import it.niedermann.android.markdown.MarkdownEditor;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.initMarkwon;

public class MarkwonMarkdownViewer extends AppCompatTextView implements MarkdownEditor {

    private Markwon markwon;
    private final MutableLiveData<CharSequence> unrenderedText$ = new MutableLiveData<>();

    public MarkwonMarkdownViewer(@NonNull Context context) {
        super(context);
        this.markwon = initMarkwon(context);
    }

    public MarkwonMarkdownViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.markwon = initMarkwon(context);
    }

    public MarkwonMarkdownViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.markwon = initMarkwon(context);
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        this.unrenderedText$.setValue(text);
        if (TextUtils.isEmpty(text)) {
            setText(text);
        } else {
            this.markwon.setMarkdown(this, text.toString());
        }
    }

    @Override
    public void setMentions(@NonNull Map<String, String> mentions) {
        this.markwon = initMarkwon(getContext(), mentions);
        setMarkdownString(this.unrenderedText$.getValue());
    }

    @Override
    public LiveData<CharSequence> getMarkdownString() {
        return distinctUntilChanged(this.unrenderedText$);
    }
}
