package it.niedermann.nextcloud.deck.ui.archivedboards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Collections;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityArchivedBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.board.ArchiveBoardListener;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardListener;
import it.niedermann.nextcloud.deck.ui.board.EditBoardListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class ArchivedBoardsActvitiy extends AppCompatActivity implements DeleteBoardListener, EditBoardListener, ArchiveBoardListener {

    private static final String BUNDLE_KEY_ACCOUNT = "accountId";

    private MainViewModel viewModel;
    private ActivityArchivedBinding binding;
    private ArchivedBoardsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final Bundle args = getIntent().getExtras();
        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT)) {
            throw new IllegalArgumentException("Please provide at least " + BUNDLE_KEY_ACCOUNT);
        }

        final Account account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);

        if (account == null) {
            throw new IllegalArgumentException(BUNDLE_KEY_ACCOUNT + " must not be null.");
        }

        binding = ActivityArchivedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.setCurrentAccount(account);

        adapter = new ArchivedBoardsAdapter(viewModel.isCurrentAccountIsSupportedVersion(), getSupportFragmentManager(), this::onArchive);
        binding.recyclerView.setAdapter(adapter);

        viewModel.getBoards(account.getId(), true).observe(this, (boards) -> {
            viewModel.setCurrentAccountHasArchivedBoards(boards != null && boards.size() > 0);
            adapter.setBoards(boards == null ? Collections.emptyList() : boards);
        });

    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, ArchivedBoardsActvitiy.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onBoardDeleted(Board board) {
        viewModel.deleteBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.info("Successfully deleted board", board.getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, viewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                }
            }
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        viewModel.updateBoard(fullBoard, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response) {
                DeckLog.info("Successfully updated board", fullBoard.getBoard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, viewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
            }
        });
    }

    @Override
    public void onArchive(Board board) {
        viewModel.dearchiveBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response) {
                DeckLog.info("Successfully dearchived board", response.getBoard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, viewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
            }
        });
    }

    @Override
    public void onClone(Board board) {
        throw new IllegalStateException("Cloning boards is not available at " + ArchivedBoardsActvitiy.class.getSimpleName());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }
}
