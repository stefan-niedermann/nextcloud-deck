package it.niedermann.android.markdown;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

public interface MarkdownEditor {

    void setText(CharSequence textToSetOnPageFinished);

    void setTextChangedListener(@NonNull Consumer<String> listener);

    void setEnabled(boolean enabled);
}