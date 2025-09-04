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
import it.niedermann.nextcloud.deck.repository.BoardRepository;
import it.niedermann.nextcloud.deck.repository.LabelRepository;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class LabelsViewModel extends BaseViewModel {

    private final LabelRepository labelRepository;
    private final BoardRepository boardRepository;

    public LabelsViewModel(@NonNull Application application) {
        super(application);
        this.labelRepository = new LabelRepository(application);
        this.boardRepository = new BoardRepository(application);
    }

    public LiveData<List<Label>> getLabelsByBoardId(@NonNull Account account, long boardLocalId) {
        return new ReactiveLiveData<>(boardRepository.getFullBoardById(account, boardLocalId))
                .filter(Objects::nonNull)
                .map(FullBoard::getLabels);
    }

    public CompletableFuture<Label> updateLabel(@NonNull Label label) {
        return labelRepository.updateLabel(label);
    }

    public CompletableFuture<Label> createLabel(@NonNull Account account, @NonNull Label label, long localBoardId) {
        return labelRepository.createLabel(account.getId(), label, localBoardId);
    }

    public CompletableFuture<EmptyResponse> deleteLabel(@NonNull Label label) {
        return labelRepository.deleteLabel(label);
    }

    public CompletableFuture<Integer> countCardsWithLabel(long localLabelId) {
        return labelRepository.countCardsWithLabel(localLabelId);
    }
}
