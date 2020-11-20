package it.niedermann.android.markdown;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

public interface MarkdownEditor {

    void setText(String textToSetOnPageFinished);

    void setTextChangedListener(@NonNull Consumer<String> listener);

    void setEnabled(boolean enabled);
}