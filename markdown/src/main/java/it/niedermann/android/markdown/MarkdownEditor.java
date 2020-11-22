package it.niedermann.android.markdown;

import androidx.lifecycle.LiveData;

/**
 * Can be used for editors and viewers as well.
 * Viewer can support basic edit features, like toggling checkboxes
 */
public interface MarkdownEditor {

    /**
     * The given {@link String} will be parsed and rendered
     */
    void setMarkdownString(CharSequence text);

    /**
     * @return the source {@link String} of the currently rendered markdown
     */
    LiveData<CharSequence> getMarkdownString();

    void setEnabled(boolean enabled);
}