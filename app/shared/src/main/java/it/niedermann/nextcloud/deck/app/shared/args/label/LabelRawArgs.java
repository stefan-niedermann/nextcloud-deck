package it.niedermann.nextcloud.deck.app.shared.args.label;

import it.niedermann.nextcloud.deck.domain.model.Label;

public sealed interface LabelRawArgs {
    record LocalLabel(Label.ID labelId) implements LabelRawArgs {
    }

    record RemoteLabel(Label.RemoteID labelRemoteId) implements LabelRawArgs {
    }
}
