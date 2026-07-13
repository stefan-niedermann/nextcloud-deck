package it.niedermann.nextcloud.deck.app.shared.args;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;

public interface ArgsResolver<TArgs, TParsedArgs> {

    CompletableFuture<TParsedArgs> resolve(TArgs args) throws ArgsResolveException;

    abstract class ArgsResolveException extends RuntimeException {
    }

    /// No account is configured at all
    class NoAccountConfiguredException extends ArgsResolveException {

    }

    /// Args are not specific enough to identify one matching account
    class MultipleAccountsOnRequestedInstanceException extends ArgsResolveException {

        private final Collection<Account> matchingAccounts;

        protected MultipleAccountsOnRequestedInstanceException(Collection<Account> matchingAccounts) {
            this.matchingAccounts = matchingAccounts;
        }

        public Collection<Account> getMatchingAccounts() {
            return this.matchingAccounts;
        }
    }
}