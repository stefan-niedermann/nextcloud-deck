package it.niedermann.nextcloud.deck.ui.card;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;

public class NewCardViewModel extends SyncViewModel {

    public NewCardViewModel(@NonNull Application application, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        super(application, account);
    }

    public CompletableFuture<Account> getCurrentAccount() {
        return baseRepository.getCurrentAccountId().thenApplyAsync(baseRepository::readAccountDirectly);
    }

    public CompletableFuture<FullCard> createFullCard(long accountId, long boardId, long stackId, String content) {
        final var result = new CompletableFuture<FullCard>();

        supplyAsync(() -> baseRepository.readAccountDirectly(accountId))
                .thenAcceptAsync(account -> syncRepository.createFullCard(accountId, boardId, stackId, createFullCard(account.getServerDeckVersionAsObject(), content),
                        new IResponseCallback<>() {
                            @Override
                            public void onResponse(FullCard response) {
                                result.complete(response);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                IResponseCallback.super.onError(throwable);
                                result.completeExceptionally(throwable);
                            }
                        }));
        return result;
    }

    private FullCard createFullCard(@NonNull Version version, @NonNull String content) {
        if (TextUtils.isEmpty(content)) {
            throw new IllegalArgumentException("Content must not be empty.");
        }
        final var fullCard = new FullCard();
        final var card = new Card();
        final int maxLength = version.getCardTitleMaxLength();
        if (content.length() > maxLength) {
            card.setTitle(content.substring(0, maxLength).trim());
            card.setDescription(content.substring(maxLength).trim());
        } else {
            card.setTitle(content);
            card.setDescription(null);
        }
        fullCard.setCard(card);
        return fullCard;
    }

    public CompletionStage<Intent> createEditIntent(@NonNull Context context, long accountId, long boardId, long cardId) {
        return supplyAsync(() -> baseRepository.readAccountDirectly(accountId))
                .thenApplyAsync(account -> EditActivity.createEditCardIntent(context, account, boardId, cardId));
    }
}
