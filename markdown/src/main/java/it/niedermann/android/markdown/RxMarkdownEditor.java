package it.niedermann.android.markdown;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.yydcdut.markdown.MarkdownEditText;
import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.syntax.edit.EditFactory;

import it.niedermann.android.markdown.rxmarkdown.MarkDownUtil;

public class RxMarkdownEditor extends FrameLayout implements MarkdownEditor {

    private MarkdownProcessor markdownProcessor;
    private final MarkdownEditText editText;
    private Consumer<String> listener;

    public RxMarkdownEditor(Context context) {
        super(context);
        editText = new MarkdownEditText(context);
        init(context);
    }

    public RxMarkdownEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        editText = new MarkdownEditText(context, attrs);
        init(context);
    }

    public RxMarkdownEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        editText = new MarkdownEditText(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        addView(editText);
        markdownProcessor = new MarkdownProcessor(context);
        markdownProcessor.config(MarkDownUtil.getMarkDownConfiguration(context).build());
        markdownProcessor.factory(EditFactory.create());
        markdownProcessor.live(editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final CharSequence text = getText();
                listener.accept(text == null ? null : text.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        editText.setText(markdownProcessor.parse(text));
    }

    @Override
    public CharSequence getText() {
        return editText.getText();
    }

    @Override
    public void setTextChangedListener(@NonNull Consumer<String> listener) {
        this.listener = listener;
    }
}
