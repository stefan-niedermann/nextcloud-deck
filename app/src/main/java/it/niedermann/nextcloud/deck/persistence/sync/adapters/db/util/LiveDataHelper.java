package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

public class LiveDataHelper {

    public interface DataChangePocessor<T>{
        void onDataChanged(T data);
    }

    public interface DataTransformator<I, O>{
        O transform(I data);
    }

    public interface LiveDataWrapper<T>{
        T getData();

        default void postResult(MutableLiveData<T> liveData){
            liveData.postValue(getData());
        }
    }

    public static <T> MediatorLiveData<T> interceptLiveData(LiveData<T> data, DataChangePocessor<T> onDataChange) {
        MediatorLiveData<T> ret = new MediatorLiveData<>();

        ret.addSource(data, changedData ->
            doAsync(() -> {
                onDataChange.onDataChanged(changedData);
                ret.postValue(changedData);
            })
        );
        return onlyIfChanged(ret);
    }


    public static <I, O> MediatorLiveData<O> postCustomValue(LiveData<I> data, DataTransformator<I, O> transformator) {
        MediatorLiveData<O> ret = new MediatorLiveData<>();

        ret.addSource(data, changedData -> doAsync(() ->ret.postValue(transformator.transform(changedData))));
        return onlyIfChanged(ret);
    }

    public static <T> MediatorLiveData<T> onlyIfChanged(LiveData<T> data) {
        MediatorLiveData<T> ret = new MediatorLiveData<>();

        ret.addSource(data, new Observer<T>() {
            T lastObject = null;
            @Override
            public void onChanged(@Nullable T newData) {
                boolean hasValueChanged =
                        (lastObject != null && newData == null) ||
                        (lastObject == null && newData != null) ||
                        (lastObject != null && !lastObject.equals(newData)) ||
                        (newData != null && !newData.equals(lastObject))                        ;


                lastObject = newData;
                if (hasValueChanged){
                    ret.postValue(newData);
                }

            }
        });
        return ret;
    }

    public static <T> LiveData<T> wrapInLiveData(final LiveDataWrapper<T> liveDataWrapper) {
        final MutableLiveData<T> liveData = new MutableLiveData();

        doAsync(() -> liveDataWrapper.postResult(liveData));

        return liveData;
    }

    private static void doAsync(Runnable r) {
        new Thread(r).start();
    }
}