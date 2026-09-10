package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateLabel;
import it.niedermann.nextcloud.deck.domain.model.Label;

public interface LabelRepository {

    CompletableFuture<Void> createLabel(CreateLabel label);

    CompletableFuture<Void> updateLabel(Label label);

    Flow.Publisher<Set<Label>> getNotDeletedLabels(Board.ID boardId);

    Flow.Publisher<Set<Label>> getLabel(Label.ID labelId);

    Flow.Publisher<Collection<Label>> find(String userText);

    CompletableFuture<Label.ID> findLabelByRemoteId(Account.ID accountId, Label.RemoteID remoteId);

    CompletableFuture<Void> deleteLabel(Label.ID labelId);
}
