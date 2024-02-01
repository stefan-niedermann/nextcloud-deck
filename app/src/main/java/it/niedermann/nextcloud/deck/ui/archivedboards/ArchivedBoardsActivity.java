package it.niedermann.nextcloud.deck.ui.archivedboards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.sso.api.EmptyResponse;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ActivityArchivedBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.board.ArchiveBoardListener;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardListener;
import it.niedermann.nextcloud.deck.ui.board.edit.EditBoardListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;
import okhttp3.Headers;

public class ArchivedBoardsActivity extends AppCompatActivity implements Themed, DeleteBoardListener, EditBoardListener, ArchiveBoardListener {

    private static final String KEY_ACCOUNT = "account";
    private ArchivedBoardsViewModel archivedBoardsViewModel;
    private ActivityArchivedBinding binding;
    private ArchivedBoardsAdapter adapter;
    private Account account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final var args = getIntent().getExtras();

        if (args == null || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException("Provide at least " + KEY_ACCOUNT);
        }

        account = (Account) args.getSerializable(KEY_ACCOUNT);
        binding = ActivityArchivedBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        applyTheme(account.getColor());

        archivedBoardsViewModel = new SyncViewModel.Provider(this, getApplication(), account).get(ArchivedBoardsViewModel.class);

        adapter = new ArchivedBoardsAdapter(account, getSupportFragmentManager(), this::onArchive);
        binding.recyclerView.setAdapter(adapter);

        final var archivedBoards$ = new ReactiveLiveData<>(archivedBoardsViewModel.getArchivedBoards(account.getId()));

        archivedBoards$
                .filter(boards -> boards.size() == 0)
                .distinctUntilChanged()
                .observe(this, this::finish);

        archivedBoards$
                .filter(boards -> boards.size() > 0)
                .distinctUntilChanged()
                .observe(this, adapter::setBoards);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onBoardDeleted(Board board) {
        archivedBoardsViewModel.deleteBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                DeckLog.info("Successfully deleted board", board.getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    showExceptionDialog(throwable, account);
                }
            }
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        archivedBoardsViewModel.updateBoard(fullBoard, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response, Headers headers) {
                DeckLog.info("Successfully updated board", fullBoard.getBoard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                showExceptionDialog(throwable, account);
            }
        });
    }

    @Override
    public void onArchive(Board board) {
        archivedBoardsViewModel.dearchiveBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response, Headers headers) {
                DeckLog.info("Successfully dearchived board", response.getBoard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                showExceptionDialog(throwable, account);
            }
        });
    }

    @Override
    public void onClone(@NonNull Account account, @NonNull Board board) {
        throw new IllegalStateException("Cloning boards is not available at " + ArchivedBoardsActivity.class.getSimpleName());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @AnyThread
    private void showExceptionDialog(@NonNull Throwable throwable, @Nullable Account account) {
        ExceptionDialogFragment
                .newInstance(throwable, account)
                .show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.platform.themeStatusBar(this);
        utils.material.themeToolbar(binding.toolbar);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, ArchivedBoardsActivity.class)
                .putExtra(KEY_ACCOUNT, account)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
