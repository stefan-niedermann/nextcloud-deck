package it.niedermann.nextcloud.deck.reminders;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.databinding.ActivityDueReminderBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;

public class DueReminderActivity extends AppCompatActivity {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    private ActivityDueReminderBinding binding;
    private long cardLocalId;
    @Nullable
    private Account account;
    @Nullable
    private Board board;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));
        showOnLockscreen();

        binding = ActivityDueReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cardLocalId = getIntent().getLongExtra(DueReminderReceiver.EXTRA_CARD_LOCAL_ID, -1L);
        if (cardLocalId <= 0L) {
            finish();
            return;
        }

        binding.dismiss.setOnClickListener((v) -> finish());
        binding.openCard.setOnClickListener((v) -> openCard());
        binding.markComplete.setOnClickListener((v) -> markComplete());

        loadReminder();
    }

    @NonNull
    static Intent createIntent(@NonNull Context context, long cardLocalId) {
        return new Intent(context, DueReminderActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(DueReminderReceiver.EXTRA_CARD_LOCAL_ID, cardLocalId);
    }

    private void showOnLockscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void loadReminder() {
        setLoading(true);
        final var appContext = getApplicationContext();
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit(() -> {
            final var dataBaseAdapter = new DataBaseAdapter(appContext);
            final FullCard fullCard = dataBaseAdapter.getFullCardByLocalIdDirectly(cardLocalId);
            if (fullCard == null || fullCard.getCard().getDone() != null || fullCard.getCard().getDueDate() == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.local_due_reminder_unavailable, Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            final Account loadedAccount = dataBaseAdapter.getAccountByIdDirectly(fullCard.getAccountId());
            final Board loadedBoard = dataBaseAdapter.getBoardByLocalCardIdDirectly(cardLocalId);
            final Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(fullCard.getCard().getStackId());
            runOnUiThread(() -> render(fullCard, loadedAccount, loadedBoard, stack));
        });
    }

    private void render(@NonNull FullCard fullCard,
                        @Nullable Account loadedAccount,
                        @Nullable Board loadedBoard,
                        @Nullable Stack stack) {
        if (loadedAccount == null || loadedBoard == null || stack == null) {
            Toast.makeText(this, R.string.local_due_reminder_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        account = loadedAccount;
        board = loadedBoard;
        setLoading(false);
        final var card = fullCard.getCard();
        final String title = card.getTitle() == null || card.getTitle().trim().isEmpty()
                ? getString(R.string.local_due_reminder_untitled_card)
                : card.getTitle();
        final String dueAt = card.getDueDate()
                .atZone(ZoneId.systemDefault())
                .format(DATE_TIME_FORMATTER);

        binding.cardTitle.setText(title);
        binding.dueAt.setText(dueAt);
        binding.board.setText(loadedBoard.getTitle());
        binding.stack.setText(stack.getTitle());
        binding.account.setText(getString(R.string.local_due_reminder_account, loadedAccount.getUserName(), loadedAccount.getUrl()));
        binding.boardColor.setBackgroundColor(loadedBoard.getColor() == null ? getColor(R.color.defaultBrand) : loadedBoard.getColor());
    }

    private void markComplete() {
        setLoading(true);
        final var appContext = getApplicationContext();
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit(() -> {
            DueReminderScheduler.markComplete(appContext, cardLocalId);
            NotificationManagerCompat.from(appContext).cancel(DueReminderScheduler.notificationId(cardLocalId));
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.local_due_reminder_completed, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void openCard() {
        if (account == null || board == null) {
            return;
        }

        startActivity(EditActivity.createEditCardIntent(this, account, board.getLocalId(), cardLocalId));
        finish();
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.markComplete.setEnabled(!loading);
        binding.openCard.setEnabled(!loading && account != null && board != null);
        binding.dismiss.setEnabled(!loading);
    }
}
