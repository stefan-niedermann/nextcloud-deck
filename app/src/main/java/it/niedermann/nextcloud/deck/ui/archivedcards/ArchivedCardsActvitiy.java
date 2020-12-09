package it.niedermann.nextcloud.deck.ui.archivedcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.ActivityArchivedBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackViewModel;

public class ArchivedCardsActvitiy extends BrandedActivity {

    private static final String BUNDLE_KEY_ACCOUNT = "accountId";
    private static final String BUNDLE_KEY_BOARD_ID = "boardId";
    private static final String BUNDLE_KEY_CAN_EDIT = "canEdit";

    private ActivityArchivedBinding binding;
    private ArchivedCardsAdapter adapter;
    private MainViewModel viewModel;
    private PickStackViewModel pickStackViewModel;

    private Account account;
    private long boardId;
    private boolean canEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final Bundle args = getIntent().getExtras();
        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT) || !args.containsKey(BUNDLE_KEY_BOARD_ID)) {
            throw new IllegalArgumentException("Please provide at least " + BUNDLE_KEY_ACCOUNT + " and " + BUNDLE_KEY_BOARD_ID);
        }

        this.account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);
        this.boardId = args.getLong(BUNDLE_KEY_BOARD_ID);
        canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT);

        if (this.account == null) {
            throw new IllegalArgumentException(BUNDLE_KEY_ACCOUNT + " must not be null.");
        }
        if (this.boardId <= 0) {
            throw new IllegalArgumentException(BUNDLE_KEY_BOARD_ID + " must a positive long value.");
        }

        binding = ActivityArchivedBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        pickStackViewModel = new ViewModelProvider(this).get(PickStackViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel.setCurrentAccount(account);
        LiveDataHelper.observeOnce(viewModel.getFullBoardById(account.getId(), boardId), this, (fullBoard) -> {
            viewModel.setCurrentBoard(fullBoard.getBoard());

            adapter = new ArchivedCardsAdapter(this, getSupportFragmentManager(), viewModel, this);
            binding.recyclerView.setAdapter(adapter);

            viewModel.getArchivedFullCardsForBoard(account.getId(), boardId).observe(this, (fullCards) -> adapter.setCardList(fullCards));
        });

    }

    @Override
    public void applyBrand(int mainColor) {
        // Nothing to do...
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account, long boardId, boolean currentBoardHasEditPermission) {
        return new Intent(context, ArchivedCardsActvitiy.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account)
                .putExtra(BUNDLE_KEY_BOARD_ID, boardId)
                .putExtra(BUNDLE_KEY_CAN_EDIT, currentBoardHasEditPermission)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
