package it.niedermann.nextcloud.deck.feature.view_board;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.SavedStateHandle;

public class ViewBoardViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;

    public ViewBoardViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
    }
}
