package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.databinding.ActivityPushNotificationBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class PushNotificationActivity extends AppCompatActivity {

    // Provided by Files app NotificationJob
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_LINK = "link";

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
}
