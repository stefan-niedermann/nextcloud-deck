package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityPushNotificationBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static android.graphics.Color.parseColor;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class PushNotificationActivity extends AppCompatActivity {

    private ActivityPushNotificationBinding binding;

    // Provided by Files app NotificationJob
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_LINK = "link";
    private static final String KEY_CARD_REMOTE_ID = "objectId";
    private static final String KEY_ACCOUNT = "account";

    @Override
    protected void onResume() {
        super.onResume();

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        if (getIntent() == null) {
            throw new IllegalArgumentException("Could not retrieve intent");
        }

        binding = ActivityPushNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.subject.setText(getIntent().getStringExtra(KEY_SUBJECT));

        final String message = getIntent().getStringExtra(KEY_MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            binding.message.setText(message);
            binding.message.setVisibility(View.VISIBLE);
        }

        final String link = getIntent().getStringExtra(KEY_LINK);

        binding.cancel.setOnClickListener((v) -> finish());

        final SyncManager accountReadingSyncManager = new SyncManager(this);
        final String cardRemoteIdString = getIntent().getStringExtra(KEY_CARD_REMOTE_ID);
        final String accountString = getIntent().getStringExtra(KEY_ACCOUNT);

        DeckLog.verbose("cardRemoteIdString = " + cardRemoteIdString);
        if (cardRemoteIdString != null) {
            try {
                final int cardRemoteId = Integer.parseInt(cardRemoteIdString);
                observeOnce(accountReadingSyncManager.readAccount(accountString), this, (account -> {
                    if (account != null) {
                        SingleAccountHelper.setCurrentAccount(this, account.getName());
                        final SyncManager syncManager = new SyncManager(this);
                        DeckLog.verbose("account: " + account);
                        observeOnce(syncManager.getLocalBoardIdByCardRemoteIdAndAccount(cardRemoteId, account), PushNotificationActivity.this, (boardLocalId -> {
                            DeckLog.verbose("BoardLocalId " + boardLocalId);
                            if (boardLocalId != null) {
                                observeOnce(syncManager.synchronizeCardByRemoteId(cardRemoteId, account), PushNotificationActivity.this, (fullCard -> {
                                    DeckLog.verbose("FullCard: " + fullCard);
                                    if (fullCard != null) {
                                        runOnUiThread(() -> {
                                            binding.submit.setOnClickListener((v) -> launchEditActivity(account, boardLocalId, fullCard.getLocalId()));
                                            binding.submit.setText(R.string.simple_open);
                                            applyBrandToSubmitButton(account);
                                            binding.submit.setEnabled(true);
                                            binding.progress.setVisibility(View.INVISIBLE);
                                        });
                                    } else {
                                        DeckLog.warn("Something went wrong while synchronizing the card " + cardRemoteId + " (cardRemoteId). Given fullCard is null.");
                                        applyBrandToSubmitButton(account);
                                        fallbackToBrowser(link);
                                    }
                                }));
                            } else {
                                DeckLog.warn("Given localBoardId for cardRemoteId " + cardRemoteId + " is null.");
                                applyBrandToSubmitButton(account);
                                fallbackToBrowser(link);
                            }
                        }));
                    } else {
                        DeckLog.warn("Given account for " + accountString + " is null.");
                        fallbackToBrowser(link);
                    }
                }));
            } catch (NumberFormatException e) {
                DeckLog.logError(e);
                fallbackToBrowser(link);
            }
        } else {
            DeckLog.warn(KEY_CARD_REMOTE_ID + " is null.");
            fallbackToBrowser(link);
        }
    }

    /**
     * If anything goes wrong and we cannot open the card directly, we fall back to open the given link in the webbrowser
     */
    private void fallbackToBrowser(String link) {
        DeckLog.warn("Falling back to browser as notification handler.");
        runOnUiThread(() -> {
            try {
                final Uri uri = Uri.parse(link);
                binding.submit.setOnClickListener((v) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(browserIntent);
                });
                binding.submit.setText(R.string.open_in_browser);
                binding.submit.setEnabled(true);
                binding.progress.setVisibility(View.INVISIBLE);
            } catch (Throwable t) {
                DeckLog.logError(t);
            }
        });
    }

    @UiThread
    private void launchEditActivity(@NonNull Account account, Long boardId, Long cardId) {
        DeckLog.info("starting " + EditActivity.class.getSimpleName() + " with [" + account + ", " + boardId + ", " + cardId + "]");
        startActivity(EditActivity.createEditCardIntent(this, account, boardId, cardId));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    public void applyBrandToSubmitButton(@NonNull Account account) {
        try {
            binding.submit.setBackgroundColor(parseColor(account.getColor()));
            binding.submit.setTextColor(parseColor(account.getTextColor()));
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
    }
}
