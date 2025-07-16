package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.repository.BoardsRepository;
import it.niedermann.nextcloud.deck.repository.LabelsRepository;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class LabelsViewModel extends BaseViewModel {

    private final LabelsRepository labelsRepository;
    private final BoardsRepository boardsRepository;

    public LabelsViewModel(@NonNull Application application) {
        super(application);
        this.labelsRepository = new LabelsRepository(application);
        this.boardsRepository = new BoardsRepository(application);
    }

    public LiveData<List<Label>> getLabelsByBoardId(@NonNull Account account, long boardLocalId) {
        return new ReactiveLiveData<>(boardsRepository.getFullBoardById(account, boardLocalId))
                .filter(Objects::nonNull)
                .map(FullBoard::getLabels);
    }

    public CompletableFuture<Label> updateLabel(@NonNull Label label) {
        return labelsRepository.updateLabel(label);
    }

    public CompletableFuture<Label> createLabel(@NonNull Account account, @NonNull Label label, long localBoardId) {
        return labelsRepository.createLabel(account.getId(), label, localBoardId);
    }

    public CompletableFuture<EmptyResponse> deleteLabel(@NonNull Label label) {
        return labelsRepository.deleteLabel(label);
    }

    public CompletableFuture<Integer> countCardsWithLabel(long localLabelId) {
        return labelsRepository.countCardsWithLabel(localLabelId);
    }
}
