package it.niedermann.nextcloud.deck.app.shared.args;

import java.util.concurrent.CompletableFuture;

public interface ArgsResolver<TArgs, TParsedArgs> {

    /// @implSpec It is safe to assume that at least one account exists at time calling
    CompletableFuture<TParsedArgs> resolve(TArgs args);

}