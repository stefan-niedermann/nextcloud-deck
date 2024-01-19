package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;

public class LabelsViewModel extends SyncViewModel {

    public LabelsViewModel(@NonNull Application application, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        super(application, account);
    }

    public LiveData<FullBoard> getFullBoardById(Long boardLocalId) {
        return new ReactiveLiveData<>(baseRepository.getFullBoardById(account.getId(), boardLocalId));
    }

    public void updateLabel(@NonNull Label label, @NonNull IResponseCallback<Label> callback) {
        syncRepository.updateLabel(label, callback);
    }

    public void createLabel(@NonNull Label label, long localBoardId, @NonNull IResponseCallback<Label> callback) {
        syncRepository.createLabel(account.getId(), label, localBoardId, callback);
    }

    public void deleteLabel(@NonNull Label label, @NonNull IResponseCallback<EmptyResponse> callback) {
        syncRepository.deleteLabel(label, callback);
    }

    public void countCardsWithLabel(long localLabelId, @NonNull IResponseCallback<Integer> callback) {
        baseRepository.countCardsWithLabel(localLabelId, callback);
    }
}
