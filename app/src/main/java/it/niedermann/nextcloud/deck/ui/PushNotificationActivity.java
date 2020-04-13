package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityPushNotificationBinding;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class PushNotificationActivity extends AppCompatActivity {

    // Provided by Files app NotificationJob
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_LINK = "link";
    private static final String KEY_CARD_REMOTE_ID = "objectId";
    private static final String KEY_ACCOUNT = "account";

    @Override
    protected void onResume() {
        // when app is running in background or is starting after force reset
        super.onResume();

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        if (getIntent() == null) {
            throw new IllegalArgumentException("Could not retrieve intent");
        }

        final ActivityPushNotificationBinding binding = ActivityPushNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final SyncManager syncManager = new SyncManager(this);
        final String cardRemoteIdString = getIntent().getStringExtra(KEY_CARD_REMOTE_ID);
        final String accountString = getIntent().getStringExtra(KEY_ACCOUNT);

        DeckLog.info("[PUSH] " + "cardRemoteIdString = "+ cardRemoteIdString);
        if (cardRemoteIdString != null) {
            try {
                final int cardRemoteId = Integer.parseInt(cardRemoteIdString);
                syncManager.readAccount(accountString).observe(this, account -> {
                    DeckLog.info("[PUSH] " + "account: " + account);
                    syncManager.synchronizeCardByRemoteId(cardRemoteId, new IResponseCallback<FullCard>(account) {
                        @Override
                        public void onResponse(FullCard response) {
                            DeckLog.info("[PUSH] " + "FullCard: " + response);
                            syncManager.getLocalBoardIdByCardRemoteIdAndAccount(cardRemoteId, account).observe(PushNotificationActivity.this, boardLocalId -> {
                                DeckLog.info("[PUSH] " + "BoardLocalId " + boardLocalId);
                                launchEditActivity(account.getId(), response.getLocalId(), boardLocalId);
                            });
                        }
                    });
                });
            } catch (NumberFormatException e) {
                // card id cannot be read, fallback to default
                DeckLog.logError(e);
            }
        }

        binding.subject.setText(getIntent().getStringExtra(KEY_SUBJECT));

        final String message = getIntent().getStringExtra(KEY_MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            binding.message.setText(message);
            binding.message.setVisibility(View.VISIBLE);
        }

        final String link = getIntent().getStringExtra(KEY_LINK);
        if (link != null) {
            binding.submit.setOnClickListener((v) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            });
        } else {
            binding.submit.setEnabled(false);
        }

        binding.cancel.setOnClickListener((v) -> finish());
    }

    private void launchEditActivity(Long accountId, Long cardId, Long boardId) {
        DeckLog.info("[PUSH] " + "starting activity with [" + accountId + ", " + cardId + ", " + boardId + "]");
        Intent intent = new Intent(this, EditActivity.class)
                .putExtra(BUNDLE_KEY_ACCOUNT_ID, accountId)
                .putExtra(BUNDLE_KEY_LOCAL_ID, cardId)
                .putExtra(BUNDLE_KEY_BOARD_ID, boardId)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
