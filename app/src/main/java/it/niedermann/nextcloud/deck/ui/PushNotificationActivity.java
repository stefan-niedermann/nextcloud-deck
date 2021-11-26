package it.niedermann.nextcloud.deck.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityPushNotificationBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

/**
 * Warning: Do not move this class to another package or folder!
 * The integration of the Nextcloud Android app <a href="https://github.com/nextcloud/android/blob/master/src/main/java/com/nextcloud/client/integrations/deck/DeckApiImpl.java#L42">assumes it to be at this location</a>.
 */
public class PushNotificationActivity extends AppCompatActivity {

    private ActivityPushNotificationBinding binding;
    private PushNotificationViewModel viewModel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onResume() {
        super.onResume();

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        final var intent = getIntent();
        if (intent == null) {
            throw new IllegalArgumentException("Could not retrieve intent");
        }

        binding = ActivityPushNotificationBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PushNotificationViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel.extractSubject(intent.getExtras()).ifPresent(binding.subject::setText);

        final var message = viewModel.extractMessage(intent.getExtras());
        if (message.isPresent()) {
            binding.message.setText(message.get());
            binding.message.setVisibility(View.VISIBLE);
        }

        binding.cancel.setOnClickListener((v) -> finish());
        viewModel.getAccount().observe(this, this::applyBrandToSubmitButton);
        executor.submit(() -> viewModel.getCardInformation(intent.getExtras(), new PushNotificationViewModel.PushNotificationCallback() {
            @Override
            public void onResponse(@NonNull PushNotificationViewModel.CardInformation cardInformation) {
                runOnUiThread(() -> openCardOnSubmit(cardInformation.account, cardInformation.localBoardId, cardInformation.localCardId));
            }

            @Override
            public void fallbackToBrowser(@NonNull Uri uri) {
                runOnUiThread(() -> PushNotificationActivity.this.fallbackToBrowser(uri));
            }

            @Override
            @SuppressLint("MissingSuperCall")
            public void onError(Throwable throwable) {
                runOnUiThread(() -> displayError(throwable));
            }
        }));
    }

    private void openCardOnSubmit(@NonNull Account account, long boardLocalId, long cardLocalId) {
        binding.submit.setOnClickListener((v) -> {
            DeckLog.info("Starting", EditActivity.class.getSimpleName(), "with [" + account + ", " + boardLocalId + ", " + cardLocalId + "]");
            startActivity(EditActivity.createEditCardIntent(this, account, boardLocalId, cardLocalId));
            finish();
        });
        binding.submit.setText(R.string.simple_open);
        applyBrandToSubmitButton(account.getColor());
        binding.submit.setEnabled(true);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void fallbackToBrowser(@NonNull Uri uri) {
        DeckLog.warn("Falling back to browser as notification handler.");
        binding.submit.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW, uri)));
        binding.submit.setText(R.string.open_in_browser);
        binding.submit.setEnabled(true);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void displayError(Throwable throwable) {
        ExceptionDialogFragment.newInstance(throwable, null)
                .show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
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
