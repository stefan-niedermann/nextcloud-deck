package it.niedermann.android.markdown;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

/**
 * Can be used for editors and viewers as well.
 * Viewer can support basic edit features, like toggling checkboxes
 */
public interface MarkdownEditor {

    /**
     * The given {@link CharSequence} will be parsed and rendered
     */
    void setMarkdownString(CharSequence text);

    /**
     * @return the source {@link CharSequence} of the currently rendered markdown
     */
    CharSequence getText();

    Context getContext();

    /**
     * @param listener will be notified when something changed from within the current {@link MarkdownEditor}
     */
    void setTextChangedListener(@NonNull Consumer<String> listener);

    void setEnabled(boolean enabled);
}