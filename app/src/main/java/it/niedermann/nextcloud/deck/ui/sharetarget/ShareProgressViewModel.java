package it.niedermann.nextcloud.deck.ui.sharetarget;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;

public class ShareProgressViewModel extends ViewModel {

    @NonNull
    public String targetCardTitle = "";
    @NonNull
    private final MutableLiveData<List<Throwable>> exceptions = new MutableLiveData<>(new ArrayList<>());
    @NonNull
    private final MutableLiveData<List<String>> duplicateAttachments = new MutableLiveData<>(new ArrayList<>());
    @NonNull
    private final MutableLiveData<Integer> max = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Integer> progress = new MutableLiveData<>(0);

    public void setMax(int max) {
        this.max.setValue(max);
    }

    public LiveData<Integer> getMax() {
        return this.max;
    }

    public Integer getMaxValue() {
        return this.max.getValue();
    }

    public void increaseProgress() {
        final var currentValue = this.progress.getValue();
        if (currentValue == null) {
            this.progress.setValue(0);
        } else {
            this.progress.setValue(currentValue + 1);
        }
    }

    public LiveData<Integer> getProgress() {
        return this.progress;
    }

    public void addDuplicateAttachment(String fileName) {
        var fileNames = this.duplicateAttachments.getValue();
        if (fileNames == null) {
            fileNames = new ArrayList<>();
        }
        fileNames.add(fileName);
        this.duplicateAttachments.setValue(fileNames);
        increaseProgress();
    }

    public boolean hasAlreadyDuplicateAttachments() {
        final var duplicateAttachments = this.duplicateAttachments.getValue();
        if (duplicateAttachments == null) {
            return false;
        }
        return duplicateAttachments.size() > 0;
    }

    public LiveData<List<String>> getDuplicateAttachments() {
        return this.duplicateAttachments;
    }

    public void addException(Throwable exception) {
        DeckLog.logError(exception);
        var exceptionList = this.exceptions.getValue();
        if (exceptionList == null) {
            exceptionList = new ArrayList<>();
        }
        exceptionList.add(exception);
        this.exceptions.setValue(exceptionList);
        increaseProgress();
    }

    public boolean hasExceptions() {
        final var exceptions = this.exceptions.getValue();
        if (exceptions == null) {
            return false;
        }
        return exceptions.size() > 0;
    }

    public LiveData<List<Throwable>> getExceptions() {
        return this.exceptions;
    }
}
