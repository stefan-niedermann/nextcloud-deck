package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util;

import androidx.lifecycle.MutableLiveData;

public class WrappedLiveData <T> extends MutableLiveData <T> {
    private RuntimeException error = null;

    public void throwError() throws RuntimeException{
        if (hasError()) {
            throw error;
        }
    }

    public boolean hasError() {
        return error!=null;
    }

    public void setError(RuntimeException e) {
        this.error = e;
    }

    public void setError(Throwable e) {
        this.error = new RuntimeException("LiveData failed: see cause!", e);
    }

    public RuntimeException getError() {
        return error;
    }

    public void postError(RuntimeException e){
        setError(e);
        postValue(null);
    }

    public void postError(Throwable e){
        setError(e);
        postValue(null);
    }
}
