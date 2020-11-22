package it.niedermann.android.markdown;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Consumer;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.simple.ext.SimpleExtPlugin;

public class MarkwonMarkdownViewer extends AppCompatTextView implements MarkdownEditor {

    private final Markwon markwon;
    private CharSequence unrenderedText;
    @Nullable
    protected Consumer<String> listener;

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

    private static Markwon initMarkwon(@NonNull Context context) {
        return Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TaskListPlugin.create(context))
                .usePlugin(ImagesPlugin.create())
                .usePlugin(GlideImagesPlugin.create(context))
                .usePlugin(SimpleExtPlugin.create())
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .build();
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        unrenderedText = text;
        if (TextUtils.isEmpty(text)) {
            setText(text);
        } else {
            markwon.setMarkdown(this, text.toString());
        }
    }

    @Override
    public CharSequence getText() {
        return unrenderedText;
    }

    @Override
    public void setTextChangedListener(@NonNull Consumer<String> listener) {
        this.listener = listener;
    }
}
