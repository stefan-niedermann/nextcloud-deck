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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityPushNotificationBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import kotlin.Triple;

/**
 * Warning: Do not move this class to another package or folder!
 * The integration of the Nextcloud Android app <a href="https://github.com/nextcloud/android/blob/master/src/main/java/com/nextcloud/client/integrations/deck/DeckApiImpl.java#L42">assumes it to be at this location</a>.
 */
public class PushNotificationActivity extends AppCompatActivity {

    private ActivityPushNotificationBinding binding;
    private PushNotificationViewModel viewModel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Provided by Files app NotificationJob
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_LINK = "link";
    private static final String KEY_ACCOUNT = "account";
    // Optional
    private static final String KEY_CARD_REMOTE_ID = "objectId";

    @Override
    protected void onResume() {
        super.onResume();

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        final Intent intent = getIntent();
        if (intent == null) {
            throw new IllegalArgumentException("Could not retrieve intent");
        }

        binding = ActivityPushNotificationBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PushNotificationViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.subject.setText(intent.getStringExtra(KEY_SUBJECT));

        final String message = intent.getStringExtra(KEY_MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            binding.message.setText(message);
            binding.message.setVisibility(View.VISIBLE);
        }

        binding.cancel.setOnClickListener((v) -> finish());
        viewModel.getAccount().observe(this, this::applyBrandToSubmitButton);
        executor.submit(() -> viewModel.getCardInformation(
                intent.getStringExtra(KEY_ACCOUNT),
                intent.getStringExtra(KEY_CARD_REMOTE_ID),
                new IResponseCallback<>() {
                    @Override
                    public void onResponse(Triple<Account, Long, Long> response) {
                        runOnUiThread(() -> openCardOnSubmit(response.getFirst(), response.getSecond(), response.getThird()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        IResponseCallback.super.onError(throwable);
                        runOnUiThread(() -> fallbackToBrowser(intent.getStringExtra(KEY_LINK)));
                        final String params = "Error while receiving push notification:\n"
                                + KEY_SUBJECT + ": [" + intent.getStringExtra(KEY_SUBJECT) + "]\n"
                                + KEY_MESSAGE + ": [" + intent.getStringExtra(KEY_MESSAGE) + "]\n"
                                + KEY_LINK + ": [" + intent.getStringExtra(KEY_LINK) + "]\n"
                                + KEY_CARD_REMOTE_ID + ": [" + intent.getStringExtra(KEY_CARD_REMOTE_ID) + "]\n"
                                + KEY_ACCOUNT + ": [" + intent.getStringExtra(KEY_ACCOUNT) + "]";
                        ExceptionDialogFragment.newInstance(new Exception(params, throwable), null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    }
                }));
    }

    private void openCardOnSubmit(@NonNull Account account, long boardLocalId, long cardLocalId) {
        binding.submit.setOnClickListener((v) -> launchEditActivity(account, boardLocalId, cardLocalId));
        binding.submit.setText(R.string.simple_open);
        applyBrandToSubmitButton(account.getColor());
        binding.submit.setEnabled(true);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    /**
     * If anything goes wrong and we cannot open the card directly, we fall back to open the given link in the webbrowser
     */
    private void fallbackToBrowser(String link) {
        DeckLog.warn("Falling back to browser as notification handler.");
        try {
            final var uri = Uri.parse(link);
            binding.submit.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW, uri)));
            binding.submit.setText(R.string.open_in_browser);
            binding.submit.setEnabled(true);
            binding.progress.setVisibility(View.INVISIBLE);
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
    }

    @UiThread
    private void launchEditActivity(@NonNull Account account, Long boardId, Long cardId) {
        DeckLog.info("starting", EditActivity.class.getSimpleName(), "with [" + account + ", " + boardId + ", " + cardId + "]");
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
    public void applyBrandToSubmitButton(@ColorInt int mainColor) {
        try {
            binding.submit.setBackgroundColor(mainColor);
            binding.submit.setTextColor(ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(mainColor));
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
    }
}
