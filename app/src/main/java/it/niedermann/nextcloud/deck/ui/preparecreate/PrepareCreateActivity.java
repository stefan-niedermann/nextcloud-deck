package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
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
        final Account account = accountAdapter.getItem(binding.accountSelect.getSelectedItemPosition());
        if (account != null) {
            final long boardId = binding.boardSelect.getSelectedItemId();
            final long stackId = binding.stackSelect.getSelectedItemId();
            final String receivedClipData = getReceivedClipData(getIntent());
            if (receivedClipData == null) {
                startActivity(EditActivity.createNewCardIntent(this, account, boardId, stackId));
            } else {
                startActivity(EditActivity.createNewCardIntent(this, account, boardId, stackId, receivedClipData));
            }

            Application.saveCurrentAccountId(this, account.getId());
            Application.saveCurrentBoardId(this, account.getId(), boardId);
            Application.saveCurrentStackId(this, account.getId(), boardId, stackId);
            applyBrand(parseColor(account.getColor()), parseColor(account.getTextColor()));

            finish();
        } else {
            // TODO Use snackbar for better error handling
            DeckLog.error("Selected account at position " + binding.accountSelect.getSelectedItemPosition() + " is null.");
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    private static String getReceivedClipData(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        final ClipData clipData = intent.getClipData();
        if (clipData == null) {
            return null;
        }
        final int itemCount = clipData.getItemCount();
        if (itemCount <= 0) {
            return null;
        }
        final ClipData.Item item = clipData.getItemAt(0);
        if (item == null) {
            return null;
        }
        final CharSequence text = item.getText();
        return TextUtils.isEmpty(text) ? null : text.toString();
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
        binding.cancel.setTextColor(getSecondaryForegroundColorDependingOnTheme(this, mainColor));
    }
}