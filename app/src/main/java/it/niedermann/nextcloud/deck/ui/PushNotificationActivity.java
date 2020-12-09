package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityPushNotificationBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.ProjectUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class PushNotificationActivity extends AppCompatActivity {

    private ActivityPushNotificationBinding binding;
    private PushNotificationViewModel viewModel;

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
        viewModel = new ViewModelProvider(this).get(PushNotificationViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.subject.setText(getIntent().getStringExtra(KEY_SUBJECT));

        final String message = getIntent().getStringExtra(KEY_MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            binding.message.setText(message);
            binding.message.setVisibility(View.VISIBLE);
        }

        final String link = getIntent().getStringExtra(KEY_LINK);
        long[] ids = ProjectUtil.extractBoardIdAndCardIdFromUrl(link);

        binding.cancel.setOnClickListener((v) -> finish());

        final String cardRemoteIdString = getIntent().getStringExtra(KEY_CARD_REMOTE_ID);
        final String accountString = getIntent().getStringExtra(KEY_ACCOUNT);

        DeckLog.verbose("cardRemoteIdString = " + cardRemoteIdString);
        if (ids.length == 2) {
            if (cardRemoteIdString != null) {
                try {
                    final int cardRemoteId = Integer.parseInt(cardRemoteIdString);
                    observeOnce(viewModel.readAccount(accountString), this, (account -> {
                        if (account != null) {
                            viewModel.setAccount(account.getName());
                            DeckLog.verbose("account: " + account);
                            observeOnce(viewModel.getBoardByRemoteId(account.getId(), ids[0]), PushNotificationActivity.this, (board -> {
                                DeckLog.verbose("BoardLocalId " + board);
                                if (board != null) {
                                    observeOnce(viewModel.getCardByRemoteID(account.getId(), cardRemoteId), PushNotificationActivity.this, (card -> {
                                        DeckLog.verbose("Card: " + card);
                                        if (card != null) {
                                            viewModel.synchronizeCard(new IResponseCallback<Boolean>(account) {
                                                @Override
                                                public void onResponse(Boolean response) {
                                                    openCardOnSubmit(account, board.getLocalId(), card.getLocalId());
                                                }

                                                @Override
                                                public void onError(Throwable throwable) {
                                                    super.onError(throwable);
                                                    openCardOnSubmit(account, board.getLocalId(), card.getLocalId());
                                                }
                                            }, card);
                                        } else {
                                            DeckLog.info("Card is not yet available locally. Synchronize board with localId " + board);

                                            viewModel.synchronizeBoard(new IResponseCallback<Boolean>(account) {
                                                @Override
                                                public void onResponse(Boolean response) {
                                                    runOnUiThread(() -> {
                                                        observeOnce(viewModel.getCardByRemoteID(account.getId(), cardRemoteId), PushNotificationActivity.this, (card -> {
                                                            DeckLog.verbose("Card: " + card);
                                                            if (card != null) {
                                                                openCardOnSubmit(account, board.getLocalId(), card.getLocalId());
                                                            } else {
                                                                DeckLog.warn("Something went wrong while synchronizing the card " + cardRemoteId + " (cardRemoteId). Given fullCard is null.");
                                                                applyBrandToSubmitButton(account);
                                                                fallbackToBrowser(link);
                                                            }
                                                        }));
                                                    });
                                                }

                                                @Override
                                                public void onError(Throwable throwable) {
                                                    super.onError(throwable);
                                                    DeckLog.warn("Something went wrong while synchronizing the board with localId " + board + ".");
                                                    applyBrandToSubmitButton(account);
                                                    fallbackToBrowser(link);
                                                }
                                            }, board.getLocalId());
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
        } else {
            DeckLog.warn("Link does not contain two IDs (expected one board id and one card id): " + link);
            fallbackToBrowser(link);
        }
    }

    private void openCardOnSubmit(@NonNull Account account, long boardLocalId, long cardlocalId) {
        runOnUiThread(() -> {
            binding.submit.setOnClickListener((v) -> launchEditActivity(account, boardLocalId, cardlocalId));
            binding.submit.setText(R.string.simple_open);
            applyBrandToSubmitButton(account);
            binding.submit.setEnabled(true);
            binding.progress.setVisibility(View.INVISIBLE);
        });
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

    // TODO implement Branded interface
    // TODO apply branding based on board color
    public void applyBrandToSubmitButton(@NonNull Account account) {
        @ColorInt final int mainColor = account.getColor();
        try {
            binding.submit.setBackgroundColor(mainColor);
            binding.submit.setTextColor(ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(mainColor));
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
    }
}
