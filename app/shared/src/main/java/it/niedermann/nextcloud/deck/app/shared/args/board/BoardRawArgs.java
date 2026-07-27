package it.niedermann.nextcloud.deck.app.shared.args.board;

import java.net.URL;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;

public sealed interface BoardRawArgs {
    record CurrentBoardOfCurrentAccount() implements BoardRawArgs {
    }

    record RemoteAccount(String accountName, long cardRemoteId) implements BoardRawArgs {
    }

    record RemoteServer(URL server, long cardRemoteId) implements BoardRawArgs {
    }

    record ExplicitBoard(Account.ID accountId, Board.ID boardId) implements BoardRawArgs {
    }
}
