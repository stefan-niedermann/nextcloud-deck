package it.niedermann.nextcloud.deck.ui.archivedboards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.databinding.ActivityArchivedCardsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class ArchivedBoardsActvitiy extends BrandedActivity {

    private static final String BUNDLE_KEY_ACCOUNT = "accountId";

    private ActivityArchivedCardsBinding binding;
    private ArchivedBoardsAdapter adapter;
    private SyncManager syncManager;

    private Account account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final Bundle args = getIntent().getExtras();
        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT)) {
            throw new IllegalArgumentException("Please provide at least " + BUNDLE_KEY_ACCOUNT);
        }

        this.account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);

        if (this.account == null) {
            throw new IllegalArgumentException(BUNDLE_KEY_ACCOUNT + " must not be null.");
        }

        binding = ActivityArchivedCardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        syncManager = new SyncManager(this);

        adapter = new ArchivedBoardsAdapter();
        binding.recyclerView.setAdapter(adapter);

        syncManager.getBoards(account.getId(), true).observe(this, (boards) -> adapter.setBoards(boards));

    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, ArchivedBoardsActvitiy.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
