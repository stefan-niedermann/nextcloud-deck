package it.niedermann.nextcloud.deck.ui.pickstack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityPickStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.ImportAccountActivity;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class PickStackActivity extends AppCompatActivity {

    private ActivityPickStackBinding binding;

    private SyncManager syncManager;

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
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);

        binding = ActivityPickStackBinding.inflate(getLayoutInflater());
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

        binding.accountSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateBoardsSource(parent.getSelectedItemId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do here
            }
        });

        binding.boardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItem = parent.getSelectedItem();
                if (selectedItem instanceof Board) {
                    Board board = (Board) selectedItem;
                    updateStacksSource(board.getAccountId(), board.getLocalId());
                } else {
                    DeckLog.logError(new IllegalArgumentException("parent.getSelectedItem() did not return an instance of " + FullStack.class.getCanonicalName() + " but " + selectedItem.getClass().getCanonicalName()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do here
            }
        });
        binding.cancel.setOnClickListener((v) -> finish());
        binding.submit.setOnClickListener((v) -> onSubmit());
    }

    private void updateBoardsSource(long accountId) {
        if (boardsLiveData != null) {
            boardsLiveData.removeObserver(boardsObserver);
        }
        boardsLiveData = syncManager.getBoards(accountId);
        boardsLiveData.observe(this, boardsObserver);
    }

    private void updateStacksSource(long accountId, long boardId) {
        if (stacksLiveData != null) {
            stacksLiveData.removeObserver(stacksObserver);
        }
        stacksLiveData = syncManager.getStacksForBoard(accountId, boardId);
        stacksLiveData.observe(this, stacksObserver);
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

        finish();
    }
}