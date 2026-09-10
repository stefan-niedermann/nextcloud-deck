package it.niedermann.nextcloud.deck.app.shared.args.column;

import it.niedermann.nextcloud.deck.domain.model.Column;

public sealed interface ColumnRawArgs {
    record LocalColumn(Column.ID columnId) implements ColumnRawArgs {
    }

    record RemoteColumn(Column.RemoteID columnRemoteId) implements ColumnRawArgs {
    }
}
