package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ActivityPushNotificationBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class PushNotificationActivity extends AppCompatActivity {

    private ActivityPushNotificationBinding binding;

    @Override
    protected void onResume() {
        // when app is running in background or is starting after force reset
        super.onResume();

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));
        binding = ActivityPushNotificationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        if (getIntent() != null) {
            binding.subject.setText(getIntent().getStringExtra("subject"));

            final String message = getIntent().getStringExtra("message");
            if (!TextUtils.isEmpty(message)) {
                binding.message.setText(message);
                binding.message.setVisibility(View.VISIBLE);
            }
            final String link = getIntent().getStringExtra("link");
            DeckLog.info("push: " + link);

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

        // TODO simply open the given URL until proper handling has been implemented
    }
}
