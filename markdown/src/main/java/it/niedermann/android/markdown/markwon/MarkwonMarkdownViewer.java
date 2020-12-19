package it.niedermann.android.markdown.markwon;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import it.niedermann.android.markdown.MarkdownEditor;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.initMarkwonViewer;
import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.searchAndColor;

public class MarkwonMarkdownViewer extends AppCompatTextView implements MarkdownEditor {

    private static final String TAG = MarkwonMarkdownViewer.class.getSimpleName();

    private Markwon markwon;
    private final MutableLiveData<CharSequence> unrenderedText$ = new MutableLiveData<>();
    private final ExecutorService renderService;

    public MarkwonMarkdownViewer(@NonNull Context context) {
        this(context, null);
    }

    public MarkwonMarkdownViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarkwonMarkdownViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.markwon = MarkwonMarkdownUtil.initMarkwonViewer(context).build();
        this.renderService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        final CharSequence previousText = this.unrenderedText$.getValue();
        this.unrenderedText$.setValue(text);
        if (TextUtils.isEmpty(text)) {
            setText(text);
        } else {
            if (!text.equals(previousText)) {
                setText(text);
                this.renderService.execute(() -> {
                    final Spanned markdown = this.markwon.toMarkdown(text.toString());
                    post(() -> this.markwon.setParsedMarkdown(this, markdown));
                });
            }

        }
    }

    @Override
    public void setSearchText(@Nullable CharSequence searchText) {
        this.renderService.execute(() -> {
            final Editable content = new SpannableStringBuilder(getText());
            searchAndColor(content, searchText, getContext(), 0, Color.BLUE, Color.YELLOW);
            post(() -> setText(content, BufferType.SPANNABLE));
        });
    }

    @Override
    public void setMarkdownString(CharSequence text, @NonNull Map<String, String> mentions) {
        this.markwon = initMarkwonViewer(getContext(), mentions).build();
        setMarkdownString(text);
    }

    @Override
    public LiveData<CharSequence> getMarkdownString() {
        return distinctUntilChanged(this.unrenderedText$);
    }
}
