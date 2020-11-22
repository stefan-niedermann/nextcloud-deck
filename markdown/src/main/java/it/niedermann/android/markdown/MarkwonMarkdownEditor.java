package it.niedermann.android.markdown;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.util.Consumer;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.simple.ext.SimpleExtPlugin;
import it.niedermann.android.markdown.markwon.handler.BlockQuoteEditHandler;
import it.niedermann.android.markdown.markwon.handler.CodeBlockEditHandler;
import it.niedermann.android.markdown.markwon.handler.CodeEditHandler;
import it.niedermann.android.markdown.markwon.handler.StrikethroughEditHandler;

public class MarkwonMarkdownEditor extends AppCompatEditText implements MarkdownEditor {

    @Nullable
    protected Consumer<String> listener;

    public MarkwonMarkdownEditor(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MarkwonMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarkwonMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        final Markwon markwon = Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TaskListPlugin.create(context))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(ImagesPlugin.create())
                .usePlugin(GlideImagesPlugin.create(context))
                .usePlugin(SimpleExtPlugin.create())
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .build();
        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .useEditHandler(new EmphasisEditHandler())
                .useEditHandler(new StrongEmphasisEditHandler())
                .useEditHandler(new StrikethroughEditHandler())
                .useEditHandler(new CodeEditHandler())
                .useEditHandler(new CodeBlockEditHandler())
                .useEditHandler(new BlockQuoteEditHandler())
                .useEditHandler(new HeadingEditHandler())
                .build();
        addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(editor, Executors.newCachedThreadPool(), this));
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (listener != null) {
                    listener.accept(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        setText(text);
    }

    @Override
    public void setTextChangedListener(@NonNull Consumer<String> listener) {
        this.listener = listener;
    }

}
