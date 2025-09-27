package it.niedermann.nextcloud.deck.setup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.feature_shared.util.LiveDataWrapper;
import it.niedermann.nextcloud.deck.repository.AccountRepository;

public class ImportStateWrapper extends LiveDataWrapper<AccountRepository.ImportState> {

    protected ImportStateWrapper() {
        this(null, null);
    }

    protected ImportStateWrapper(@Nullable AccountRepository.ImportState value, @Nullable Throwable error) {
        super(value, error);
    }

    protected ImportStateWrapper(@NonNull AccountRepository.ImportState value) {
        super(value);
    }

    protected ImportStateWrapper(@NonNull Throwable error) {
        super(error);
    }

}