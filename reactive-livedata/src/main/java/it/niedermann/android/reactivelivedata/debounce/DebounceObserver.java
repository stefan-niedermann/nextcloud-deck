package it.niedermann.android.reactivelivedata.debounce;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class DebounceObserver<T> implements Observer<T> {
    private final MediatorLiveData<T> mediator;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final long timeout;
    private final ChronoUnit timeUnit;
    private T lastEmittedValue = null;
    private Instant lastEmit = Instant.now();
    private boolean firstEmit = true;
    private Future<?> scheduledRecheck;

    public DebounceObserver(@NonNull MediatorLiveData<T> mediator, long timeout, @NonNull ChronoUnit timeUnit) {
        this.mediator = mediator;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public void onChanged(T value) {
        final var now = Instant.now();

        if (firstEmit) {
            firstEmit = false;
            emitValue(value, now);
        } else {
            if (lastEmit.isBefore(now.minus(timeout, timeUnit))) {
                emitValue(value, now);
            } else {
                scheduleRecheck(value, getRemainingTimeToNextTimeout(now, lastEmit));
            }
        }
    }

    private void emitValue(T value, @NonNull Instant lastEmit) {
        cancelScheduledRecheck();
        mediator.postValue(value);
        this.lastEmit = lastEmit;
    }

    private Duration getRemainingTimeToNextTimeout(@NonNull Instant now, @NonNull Instant lastEmit) {
        final var millisSinceLastEmit = now.toEpochMilli() - lastEmit.toEpochMilli();
        final var millisToNextEmit = Duration.of(timeout, timeUnit).toMillis() - millisSinceLastEmit;
        return Duration.ofMillis(millisToNextEmit);
    }

    private void cancelScheduledRecheck() {
        if (scheduledRecheck != null) {
            scheduledRecheck.cancel(true);
        }
    }

    private synchronized void scheduleRecheck(T newValue, @NonNull Duration sleep) {
        cancelScheduledRecheck();
        scheduledRecheck = executor.submit(() -> {
            try {
                Thread.sleep(sleep.toMillis());
                if (!Objects.equals(lastEmittedValue, newValue)) {
                    mediator.postValue(newValue);
                    lastEmittedValue = newValue;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}