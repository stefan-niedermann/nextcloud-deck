package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util;

import androidx.lifecycle.MutableLiveData;

public class WrappedLiveData <T> extends MutableLiveData <T> {
    private Exception error = null;

    public void throwError() throws Exception{
        if (hasError()) {
            throw error;
        }
    }

    public boolean hasError() {
        return error!=null;
    }

    public void setError(Exception e) {
        this.error = e;
    }
}
