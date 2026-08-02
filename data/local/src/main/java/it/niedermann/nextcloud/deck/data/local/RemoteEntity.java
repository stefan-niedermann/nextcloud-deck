package it.niedermann.nextcloud.deck.data.local;

import java.time.OffsetDateTime;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;

public interface RemoteEntity<RID> {
    Long localId();
    Account.ID accountId();
    RID remoteId();
    DBStatus status();
    OffsetDateTime lastModified();
    OffsetDateTime lastModifiedLocal();
    String etag();
}
