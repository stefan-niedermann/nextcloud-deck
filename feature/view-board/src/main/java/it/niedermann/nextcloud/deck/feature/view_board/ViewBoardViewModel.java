package it.niedermann.nextcloud.deck.feature.view_board;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.SavedStateHandle;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.feature.shared.util.Repositories;


public class ViewBoardViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final BoardRepository boardRepository;

    public ViewBoardViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        this.boardRepository = Repositories.getBoardRepository();
    }

    public LiveData<Board> getBoard(long boardId) {
        return LiveDataReactiveStreams.fromPublisher(boardRepository.getBoard(boardId));
    }
}
