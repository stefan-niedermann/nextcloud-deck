package it.niedermann.nextcloud.deck.ui.board.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class EditBoardViewModel extends BaseViewModel {

    public EditBoardViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<FullBoard> getFullBoardById(long accountId, long localId) {
        return baseRepository.getFullBoardById(accountId, localId);
    }

    public LiveData<Integer> getAccountColor(long accountId) {
        return baseRepository.getAccountColor(accountId);
    }
}
