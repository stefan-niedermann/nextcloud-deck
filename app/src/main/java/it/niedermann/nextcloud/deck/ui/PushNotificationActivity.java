package it.niedermann.nextcloud.deck.ui;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
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
    private Intent intent;

    @Override
    protected void onResume() {
        super.onResume();

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        intent = getIntent();
        if (intent == null) {
            throw new IllegalArgumentException("Could not retrieve intent");
        }

        binding = ActivityPushNotificationBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PushNotificationViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.progress.setIndeterminate(true);
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
        DeckLog.info("Starting", EditActivity.class.getSimpleName(), "with [" + account + ", " + boardLocalId + ", " + cardLocalId + "]");

        startActivity(EditActivity.createEditCardIntent(this, account, boardLocalId, cardLocalId));
        finish();
    }

    private void fallbackToBrowser(@NonNull Uri uri) {
        DeckLog.warn("Falling back to browser as push notification handler:", uri);

        binding.submit.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW, uri)));

        viewModel.extractSubject(intent.getExtras()).ifPresent(binding.subject::setText);
        viewModel.extractMessage(intent.getExtras()).ifPresent(message -> {
            binding.message.setText(message);
            binding.message.setVisibility(View.VISIBLE);
        });

        binding.progressWrapper.setVisibility(View.GONE);
        binding.browserFallback.setVisibility(View.VISIBLE);
        binding.errorWrapper.setVisibility(View.GONE);
    }

    private void displayError(Throwable throwable) {
        DeckLog.error(throwable);

        binding.errorExplanation.setText(getString(R.string.push_notification_link_empty, getString(R.string.push_notification_link_empty_link)));
        binding.showError.setOnClickListener((v) -> ExceptionDialogFragment.newInstance(throwable, null)
                .show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));

        binding.progressWrapper.setVisibility(View.GONE);
        binding.browserFallback.setVisibility(View.GONE);
        binding.errorWrapper.setVisibility(View.VISIBLE);
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
            binding.progress.getProgressDrawable().setColorFilter(
                    getSecondaryForegroundColorDependingOnTheme(this, mainColor), PorterDuff.Mode.SRC_IN);
            binding.submit.setBackgroundColor(mainColor);
            binding.submit.setTextColor(ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(mainColor));
            binding.showError.setBackgroundColor(mainColor);
            binding.showError.setTextColor(ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(mainColor));
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
    }
}
