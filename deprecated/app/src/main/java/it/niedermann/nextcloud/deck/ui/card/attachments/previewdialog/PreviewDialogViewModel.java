package it.niedermann.nextcloud.deck.ui.card.attachments.previewdialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.RequestBuilder;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

public class PreviewDialogViewModel extends ViewModel {

    @NonNull
    private final MutableLiveData<String> title$ = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<RequestBuilder<?>> imageBuilder$ = new MutableLiveData<>();
    private MutableLiveData<Boolean> result$ = new MutableLiveData<>();

    /**
     * Call this before observing {@link #getResult()} to prepare the {@link PreviewDialog}.
     */
    public void prepareDialog(@Nullable String title, @Nullable RequestBuilder<?> imageBuilder) {
        this.result$ = new MutableLiveData<>();
        this.title$.setValue(title);
        this.imageBuilder$.setValue(imageBuilder);
    }

    /**
     * This will be a new instance after each call of {@link #prepareDialog(String, RequestBuilder)}.
     *
     * @return {@link Boolean#TRUE} if a positive action has been submitted, {@link Boolean#FALSE} if the dialog has been canceled.
     */
    public LiveData<Boolean> getResult() {
        return this.result$;
    }

    protected LiveData<String> getTitle() {
        return distinctUntilChanged(this.title$);
    }

    protected LiveData<RequestBuilder<?>> getImageBuilder() {
        return distinctUntilChanged(this.imageBuilder$);
    }

    protected void setResult(boolean submittedPositive) {
        result$.setValue(submittedPositive);
    }
}
