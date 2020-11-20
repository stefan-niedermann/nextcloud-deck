package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yydcdut.markdown.MarkdownProcessor;
import com.yydcdut.markdown.MarkdownTextView;
import com.yydcdut.markdown.syntax.text.TextFactory;

import it.niedermann.android.markdown.rxmarkdown.MarkDownUtil;

public class RxMarkdownViewer extends FrameLayout implements MarkdownViewer {

    private MarkdownProcessor markdownProcessor;
    private final MarkdownTextView textView;

    public RxMarkdownViewer(Context context) {
        super(context);
        textView = new MarkdownTextView(context);
        init(context);
    }

    public RxMarkdownViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        textView = new MarkdownTextView(context, attrs);
        init(context);
    }

    public RxMarkdownViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textView = new MarkdownTextView(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        addView(textView);
        markdownProcessor = new MarkdownProcessor(context);
        markdownProcessor.config(MarkDownUtil.getMarkDownConfiguration(context).build());
        markdownProcessor.factory(TextFactory.create());
    }

    @Override
    public void setText(CharSequence text) {
        textView.setText(markdownProcessor.parse(text));
    }

    @Override
    public CharSequence getText() {
        return textView.getText();
    }
}
