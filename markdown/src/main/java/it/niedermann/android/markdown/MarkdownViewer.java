package it.niedermann.android.markdown;

import android.content.Context;

public interface MarkdownViewer {

    void setText(CharSequence text);

    CharSequence getText();

    Context getContext();
}
