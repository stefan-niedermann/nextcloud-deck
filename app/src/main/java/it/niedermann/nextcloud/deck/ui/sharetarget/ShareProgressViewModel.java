package it.niedermann.nextcloud.deck.ui.sharetarget;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;

public class ShareProgressViewModel extends ViewModel {

    public String targetCardTitle = "";
    @NonNull
    private MutableLiveData<List<Throwable>> exceptions = new MutableLiveData<>(new ArrayList<>());
    @NonNull
    private MutableLiveData<List<String>> alreadyExistingAttachments = new MutableLiveData<>(new ArrayList<>());
    @NonNull
    private MutableLiveData<Integer> max = new MutableLiveData<>();
    @NonNull
    private MutableLiveData<Integer> progress = new MutableLiveData<>(0);

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
        final Integer currentValue = this.progress.getValue();
        if (currentValue == null) {
            this.progress.setValue(0);
        } else {
            this.progress.setValue(currentValue + 1);
        }
    }

    public LiveData<Integer> getProgress() {
        return this.progress;
    }

    public void addAlreadyExistingAttachment(String fileName) {
        List<String> fileNames = this.alreadyExistingAttachments.getValue();
        if (fileNames == null) {
            fileNames = new ArrayList<>();
        }
        fileNames.add(fileName);
        this.alreadyExistingAttachments.setValue(fileNames);
        increaseProgress();
    }

    public boolean hasAlreadyExistingAttachments() {
        List<String> alreadyExistingAttachments = this.alreadyExistingAttachments.getValue();
        if (alreadyExistingAttachments == null) {
            return false;
        }
        return alreadyExistingAttachments.size() > 0;
    }

    public LiveData<List<String>> getAlreadyExistingAttachments() {
        return this.alreadyExistingAttachments;
    }

    public void addException(Throwable exception) {
        DeckLog.logError(exception);
        List<Throwable> exceptionList = this.exceptions.getValue();
        if (exceptionList == null) {
            exceptionList = new ArrayList<>();
        }
        exceptionList.add(exception);
        this.exceptions.setValue(exceptionList);
        increaseProgress();
    }

    public boolean hasExceptions() {
        List<Throwable> exceptions = this.exceptions.getValue();
        if (exceptions == null) {
            return false;
        }
        return exceptions.size() > 0;
    }

    public LiveData<List<Throwable>> getExceptions() {
        return this.exceptions;
    }
}
