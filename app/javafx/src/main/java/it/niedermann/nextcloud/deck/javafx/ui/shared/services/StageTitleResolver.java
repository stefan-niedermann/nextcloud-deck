package it.niedermann.nextcloud.deck.javafx.ui.shared.services;

import java.util.Optional;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import jakarta.inject.Inject;

@FxScope
public class StageTitleResolver {

    private final GetAccountUseCase getAccountUseCase;
    private final GetAccountsUseCase getAccountsUseCase;
    private final GetBoardUseCase getBoardUseCase;
    private final GetCardUseCase getCardUseCase;
    private final GetColumnUseCase getColumnUseCase;

    @Inject
    public StageTitleResolver(GetAccountUseCase getAccountUseCase,
                              GetAccountsUseCase getAccountsUseCase,
                              GetBoardUseCase getBoardUseCase,
                              GetCardUseCase getCardUseCase,
                              GetColumnUseCase getColumnUseCase) {
        this.getAccountUseCase = getAccountUseCase;
        this.getAccountsUseCase = getAccountsUseCase;
        this.getBoardUseCase = getBoardUseCase;
        this.getCardUseCase = getCardUseCase;
        this.getColumnUseCase = getColumnUseCase;
    }

    public Flowable<String> resolve(Account.ID accountId) {
        return resolve(accountId, null, null);
    }

    public Flowable<String> resolve(Board.ID boardId) {
        return resolve(null, boardId, null);
    }

    public Flowable<String> resolve(Card.ID cardId) {
        return resolve(null, null, cardId);
    }

    public Flowable<String> resolve(Account.ID accountId, Board.ID boardId) {
        return resolve(accountId, boardId, null);
    }

    public Flowable<String> resolve(Account.ID accountId, Card.ID cardId) {
        return resolve(accountId, null, cardId);
    }

    public Flowable<String> resolve(Account.ID accountId, Board.ID boardId, Card.ID cardId) {
        final Flowable<Optional<Card>> card$ = cardId != null
                ? Flowable.fromPublisher(getCardUseCase.execute(cardId)).map(Optional::of)
                : Flowable.just(Optional.empty());

        final Flowable<Optional<Board.ID>> resolvedBoardId$ = card$
                .switchMap(card -> {
                    if (boardId != null) {
                        return Flowable.just(Optional.of(boardId));
                    }
                    if (card.isPresent()) {
                        return Flowable.fromPublisher(getColumnUseCase.execute(card.get().columnId()))
                                .map(column -> Optional.of(column.boardId()));
                    }
                    return Flowable.just(Optional.empty());
                });

        final Flowable<Optional<Board>> board$ = resolvedBoardId$
                .switchMap(id -> id
                        .map(value -> Flowable.fromPublisher(getBoardUseCase.execute(value)).map(Optional::of))
                        .orElse(Flowable.just(Optional.empty())));

        final Flowable<Optional<Account.ID>> resolvedAccountId$ = resolvedBoardId$
                .switchMap(bId -> {
                    if (accountId != null) {
                        return Flowable.just(Optional.of(accountId));
                    }
                    if (bId.isPresent()) {
                        return Flowable.fromPublisher(getAccountsUseCase.execute())
                                .concatMap(accounts -> Flowable.fromIterable(accounts)
                                        .concatMap(account -> Flowable.fromPublisher(getBoardUseCase.execute(bId.get()))
                                                .map(_ -> account.id())
                                                .onErrorResumeNext(_ -> Flowable.empty()))
                                        .firstElement()
                                        .toFlowable()
                                        .map(Optional::of)
                                        .defaultIfEmpty(Optional.empty()));
                    }
                    return Flowable.just(Optional.empty());
                });

        final var title$ = Flowable.combineLatest(card$, board$, (c, b) -> {
            if (c.isPresent() && b.isPresent()) {
                return c.get().title() + " - " + b.get().title();
            } else if (c.isPresent()) {
                return c.get().title();
            } else if (b.isPresent()) {
                return b.get().title();
            } else {
                return "Deck";
            }
        });

        final var suffix$ = Flowable.fromPublisher(getAccountsUseCase.execute())
                .switchMap(accounts -> {
                    if (accounts.size() > 1) {
                        return resolvedAccountId$.switchMap(id -> id
                                .map(value -> Flowable.fromPublisher(getAccountUseCase.execute(value)).map(account -> " - " + account.displayName()))
                                .orElse(Flowable.just("")));
                    } else {
                        return Flowable.just("");
                    }
                });

        return Flowable.combineLatest(title$, suffix$, (title, suffix) -> title + suffix);
    }
}
