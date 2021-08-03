package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiveDataHelper {

    private LiveDataHelper() {
        throw new UnsupportedOperationException("This class must not be instantiated.");
    }

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static <T> LiveData<T> interceptLiveData(LiveData<T> data, DataChangeProcessor<T> onDataChange) {
        MediatorLiveData<T> ret = new MediatorLiveData<>();

        ret.addSource(data, changedData ->
                executor.submit(() -> {
                    onDataChange.onDataChanged(changedData);
                    ret.postValue(changedData);
                })
        );
        return distinctUntilChanged(ret);
    }


    public static <I, O> LiveData<O> postCustomValue(LiveData<I> data, DataTransformator<I, O> transformator) {
        final MediatorLiveData<O> ret = new MediatorLiveData<>();
        ret.addSource(data, changedData -> executor.submit(() -> ret.postValue(transformator.transform(changedData))));
        return distinctUntilChanged(ret);
    }

    public static <I> MediatorLiveData<I> of(I oneShot) {
        return new MediatorLiveData<>() {
            @Override
            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
                super.observe(owner, observer);
                executor.submit(() -> postValue(oneShot));
            }
        };
    }

    public static <I, O> LiveData<O> postSingleValue(LiveData<I> data, DataTransformator<I, O> transformator) {
        final MediatorLiveData<O> ret = new MediatorLiveData<>();
        ret.addSource(data, changedData -> executor.submit(() -> ret.postValue(transformator.transform(changedData))));
        return distinctUntilChanged(ret);
    }

    public static <T> void observeOnce(LiveData<T> liveData, LifecycleOwner owner, Observer<T> observer) {
        final Observer<T> tempObserver = new Observer<>() {
            @Override
            public void onChanged(T result) {
                liveData.removeObserver(this);
                observer.onChanged(result);
            }
        };
        liveData.observe(owner, tempObserver);
    }

    public interface DataChangeProcessor<T> {
        void onDataChanged(T data);
    }

    public interface DataTransformator<I, O> {
        O transform(I data);
    }
}