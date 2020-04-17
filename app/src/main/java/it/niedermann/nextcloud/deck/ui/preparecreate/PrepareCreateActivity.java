package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ActivityPrepareCreateBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.ImportAccountActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static android.graphics.Color.parseColor;
import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class PrepareCreateActivity extends BrandedActivity {

    private ActivityPrepareCreateBinding binding;

    private SyncManager syncManager;

    private boolean brandingEnabled;

    private long lastAccountId;
    private long lastBoardId;
    private long lastStackId;

    private ArrayAdapter<Account> accountAdapter;
    private ArrayAdapter<Board> boardAdapter;
    private ArrayAdapter<FullStack> stackAdapter;

    @Nullable
    private LiveData<List<Board>> boardsLiveData;
    @NonNull
    private Observer<List<Board>> boardsObserver = (boards) -> {
        boardAdapter.clear();
        boardAdapter.addAll(boards);
        binding.boardSelect.setEnabled(true);

        if (boards.size() > 0) {
            binding.boardSelect.setEnabled(true);

            for (Board board : boards) {
                if (board.getLocalId() == lastBoardId) {
                    binding.boardSelect.setSelection(boardAdapter.getPosition(board));
                    break;
                }
            }
        } else {
            binding.boardSelect.setEnabled(false);
            binding.submit.setEnabled(false);
        }
    };

    @Nullable
    private LiveData<List<FullStack>> stacksLiveData;
    @NonNull
    private Observer<List<FullStack>> stacksObserver = (fullStacks) -> {
        stackAdapter.clear();
        stackAdapter.addAll(fullStacks);

        if (fullStacks.size() > 0) {
            binding.stackSelect.setEnabled(true);
            binding.submit.setEnabled(true);

            for (FullStack fullStack : fullStacks) {
                if (fullStack.getLocalId() == lastStackId) {
                    binding.stackSelect.setSelection(stackAdapter.getPosition(fullStack));
                    break;
                }
            }
        } else {
            binding.stackSelect.setEnabled(false);
            binding.submit.setEnabled(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        brandingEnabled = Application.isBrandingEnabled(this);

        binding = ActivityPrepareCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        accountAdapter = new AccountAdapter(this);
        binding.accountSelect.setAdapter(accountAdapter);
        binding.accountSelect.setEnabled(false);
        boardAdapter = new BoardAdapter(this);
        binding.boardSelect.setAdapter(boardAdapter);
        binding.stackSelect.setEnabled(false);
        stackAdapter = new StackAdapter(this);
        binding.stackSelect.setAdapter(stackAdapter);
        binding.stackSelect.setEnabled(false);

        syncManager = new SyncManager(this);

        switchMap(syncManager.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return syncManager.readAccounts();
            } else {
                startActivityForResult(new Intent(this, ImportAccountActivity.class), ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT);
                return null;
            }
        }).observe(this, (List<Account> accounts) -> {
            if (accounts == null || accounts.size() == 0) {
                throw new IllegalStateException("hasAccounts() returns true, but readAccounts() returns null or has no entry");
            }

            lastAccountId = Application.readCurrentAccountId(this);
            lastBoardId = Application.readCurrentBoardId(this, lastAccountId);
            lastStackId = Application.readCurrentStackId(this, lastAccountId, lastBoardId);

            accountAdapter.clear();
            accountAdapter.addAll(accounts);
            binding.accountSelect.setEnabled(true);

            for (Account account : accounts) {
                if (account.getId() == lastAccountId) {
                    binding.accountSelect.setSelection(accountAdapter.getPosition(account));
                    break;
                }
            }
        });

        binding.accountSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) -> {
            applyTemporaryBrand(accountAdapter.getItem(position));
            updateLiveDataSource(boardsLiveData, boardsObserver, syncManager.getBoardsWithEditPermission(parent.getSelectedItemId()));
        });

        binding.boardSelect.setOnItemSelectedListener((SelectedListener) (parent, view, position, id) ->
                updateLiveDataSource(stacksLiveData, stacksObserver, syncManager.getStacksForBoard(binding.accountSelect.getSelectedItemId(), parent.getSelectedItemId())));

        binding.cancel.setOnClickListener((v) -> finish());
        binding.submit.setOnClickListener((v) -> onSubmit());
    }

    /**
     * Updates the source of the given liveData and de- and reregisters the given observer.
     */
    private <T> void updateLiveDataSource(@Nullable LiveData<T> liveData, Observer<T> observer, LiveData<T> newSource) {
        if (liveData != null) {
            liveData.removeObserver(observer);
        }
        liveData = newSource;
        liveData.observe(PrepareCreateActivity.this, observer);
    }

    /**
     * Starts EditActivity and passes parameters.
     */
    private void onSubmit() {
        final long accountId = binding.accountSelect.getSelectedItemId();
        final long boardId = binding.boardSelect.getSelectedItemId();
        final long stackId = binding.stackSelect.getSelectedItemId();

        Intent intent = new Intent(getApplicationContext(), EditActivity.class);

        intent.putExtra(BUNDLE_KEY_ACCOUNT_ID, accountId);
        intent.putExtra(BUNDLE_KEY_BOARD_ID, boardId);
        intent.putExtra(BUNDLE_KEY_STACK_ID, stackId);
        intent.putExtra(BUNDLE_KEY_LOCAL_ID, NO_LOCAL_ID);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        Application.saveCurrentAccountId(this, accountId);
        Application.saveCurrentBoardId(this, accountId, boardId);
        Application.saveCurrentStackId(this, accountId, boardId, stackId);

        Account selectedAccount = accountAdapter.getItem(binding.accountSelect.getSelectedItemPosition());
        if (selectedAccount != null) {
            applyBrand(parseColor(selectedAccount.getColor()), parseColor(selectedAccount.getTextColor()));
        }

        finish();
    }

    private void applyTemporaryBrand(@Nullable Account account) {
        try {
            if (account != null && brandingEnabled) {
                applyBrand(parseColor(account.getColor()), parseColor(account.getTextColor()));
            }
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
        binding.submit.setBackgroundColor(mainColor);
        binding.submit.setTextColor(textColor);
    }
}