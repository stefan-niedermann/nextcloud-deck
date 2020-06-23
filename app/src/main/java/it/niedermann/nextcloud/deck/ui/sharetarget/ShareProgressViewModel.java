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
    private MutableLiveData<List<Throwable>> exceptions = new MutableLiveData<>(new ArrayList<>());
    @NonNull
    private MutableLiveData<Integer> max = new MutableLiveData<>(0);
    @NonNull
    private MutableLiveData<Integer> progress = new MutableLiveData<>(0);

    public void postMax(int max) {
        this.max.postValue(max);
    }

    public LiveData<Integer> getMax() {
        return this.max;
    }

    public Integer getMaxValue() {
        return this.max.getValue();
    }

    public void increaseProgress() {
        Integer currentValue = this.progress.getValue();
        if (currentValue == null) {
            this.progress.postValue(0);
        } else {
            this.progress.postValue(currentValue + 1);
        }
    }

    public LiveData<Integer> getProgress() {
        return this.progress;
    }

    public void addException(Throwable exception) {
        DeckLog.logError(exception);
        List<Throwable> exceptionList = this.exceptions.getValue();
        if (exceptionList == null) {
            exceptionList = new ArrayList<>();
        }
        exceptionList.add(exception);
        this.exceptions.postValue(exceptionList);
        increaseProgress();
    }

    public LiveData<List<Throwable>> getExceptions() {
        return this.exceptions;
    }
}
