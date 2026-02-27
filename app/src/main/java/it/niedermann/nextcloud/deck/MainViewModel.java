package it.niedermann.nextcloud.deck;

import static androidx.lifecycle.LiveDataReactiveStreams.fromPublisher;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.feature.shared.util.Repositories;

public class MainViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    long accountId = 0;
    long boardId = 0;
    final AtomicInteger boardIndex = new AtomicInteger(0);
    final LiveData<MainNavInfo> boardsAndUpcomingCardsCountMock;

    public MainViewModel(@NonNull Application application) {
        super(application);
        accountRepository = Repositories.getAccountRepository();
        boardRepository = Repositories.getBoardRepository();
        boardsAndUpcomingCardsCountMock = fromPublisher(
                Flowable.combineLatest(
                        boardRepository
                                .getBoards(accountId)
                                .map(boards -> boards
                                        .stream()
                                        .map(board -> new MainNavInfo.BoardInfo(
                                                board.getId(),
                                                board.getTitle(),
                                                Color.valueOf(board.getColor() == null ? Color.RED : board.getColor()),
                                                true,
                                                true
                                        ))
                                        .collect(Collectors.toUnmodifiableMap(
                                                boardInfo -> boardIndex.getAndIncrement(),
                                                boardInfo -> boardInfo))),
                        boardRepository.getUpcomingCardsView(boardId),
                        MainNavInfo::new
                )
        );
    }

    public LiveData<Boolean> hasAccounts() {
        return fromPublisher(accountRepository.hasAccounts());
    }

    public LiveData<MainNavInfo> getBoardsAndUpcomingCardsCount() {
        return this.boardsAndUpcomingCardsCountMock;
    }

    public record MainNavInfo(
            @NonNull Map<Integer, BoardInfo> boardOverview,
            int upcomingCardsCount
    ) {

        record BoardInfo(
                long id,
                @NonNull String title,
                @NonNull Color color,
                boolean permissionShare,
                boolean permissionManage
        ) {

        }
    }
}
