package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProseMirrorMarkdownEditor extends WebViewMarkdownEditor {

    public ProseMirrorMarkdownEditor(@NonNull Context context) {
        super(context);
    }

    public ProseMirrorMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProseMirrorMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String getUrlToIndex() {
        return "file:///android_asset/web/simplemde/index.html";
    }

}