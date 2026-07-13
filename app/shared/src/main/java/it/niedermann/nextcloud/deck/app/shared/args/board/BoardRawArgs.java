package it.niedermann.nextcloud.deck.app.shared.args.board;

import java.net.URL;

public sealed interface BoardRawArgs {
    record CurrentBoardOfCurrentAccount() implements BoardRawArgs {
    }

    record RemoteAccount(String accountName, long cardRemoteId) implements BoardRawArgs {
    }

    record RemoteServer(URL server, long cardRemoteId) implements BoardRawArgs {
    }
}
