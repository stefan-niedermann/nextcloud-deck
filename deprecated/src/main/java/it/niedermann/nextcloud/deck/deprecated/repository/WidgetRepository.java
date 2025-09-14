package it.niedermann.nextcloud.deck.deprecated.repository;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsAdapterItem;

public class WidgetRepository extends AbstractRepository {

    public WidgetRepository(@NonNull Context context) {
        super(context);
    }

    /**
     * Can be called from a configuration screen or a picker.
     * Creates a new entry in the database, if row with given widgetId does not yet exist.
     */
    @AnyThread
    public void addOrUpdateSingleCardWidget(int widgetId, long accountId, long boardId, long localCardId) {
        executor.submit(() -> dataBaseAdapter.createSingleCardWidget(widgetId, accountId, boardId, localCardId));
    }

    @WorkerThread
    public FullSingleCardWidgetModel getSingleCardWidgetModelDirectly(int appWidgetId) throws NoSuchElementException {
        final FullSingleCardWidgetModel model = dataBaseAdapter.getFullSingleCardWidgetModel(appWidgetId);
        if (model == null) {
            throw new NoSuchElementException("There is no " + FullSingleCardWidgetModel.class.getSimpleName() + " with the given appWidgetId " + appWidgetId);
        }
        return model;
    }

    @AnyThread
    public void deleteSingleCardWidgetModel(int widgetId) {
        executor.submit(() -> dataBaseAdapter.deleteSingleCardWidget(widgetId));
    }

    @WorkerThread
    public List<UpcomingCardsAdapterItem> getCardsForUpcomingCardsForWidget() {
        return dataBaseAdapter.getCardsForUpcomingCardForWidget();
    }

    @WorkerThread
    public LiveData<List<UpcomingCardsAdapterItem>> getCardsForUpcomingCards() {
        return dataBaseAdapter.getCardsForUpcomingCard();
    }

    @WorkerThread
    public List<FilterWidgetCard> getCardsForFilterWidget(@NonNull Integer filterWidgetId) {
        return dataBaseAdapter.getCardsForFilterWidget(filterWidgetId);
    }

    public boolean filterWidgetExists(int id) {
        return dataBaseAdapter.filterWidgetExists(id);
    }

    @AnyThread
    public void deleteFilterWidget(int filterWidgetId, @NonNull IResponseCallback<Boolean> callback) {
        executor.submit(() -> {
            try {
                dataBaseAdapter.deleteFilterWidgetDirectly(filterWidgetId);
                callback.onResponse(Boolean.TRUE, IResponseCallback.EMPTY_HEADERS);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    @AnyThread
    public void getFilterWidget(@NonNull Integer filterWidgetId, @NonNull IResponseCallback<FilterWidget> callback) {
        executor.submit(() -> {
            try {
                callback.onResponse(dataBaseAdapter.getFilterWidgetByIdDirectly(filterWidgetId), IResponseCallback.EMPTY_HEADERS);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    @AnyThread
    public void updateFilterWidget(@NonNull FilterWidget filterWidget, @NonNull ResponseCallback<Boolean> callback) {
        executor.submit(() -> {
            try {
                dataBaseAdapter.updateFilterWidgetDirectly(filterWidget);
                callback.onResponse(Boolean.TRUE, IResponseCallback.EMPTY_HEADERS);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    @AnyThread
    public void createFilterWidget(@NonNull FilterWidget filterWidget, @NonNull IResponseCallback<Integer> callback) {
        executor.submit(() -> {
            try {
                int filterWidgetId = dataBaseAdapter.createFilterWidgetDirectly(filterWidget);
                callback.onResponse(filterWidgetId, IResponseCallback.EMPTY_HEADERS);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }
}
