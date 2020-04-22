package it.niedermann.nextcloud.deck.ui.exception;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityExceptionBinding;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class ExceptionActivity extends AppCompatActivity {

    private static final String KEY_THROWABLE = "throwable";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final ActivityExceptionBinding binding = ActivityExceptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        super.onCreate(savedInstanceState);

        Throwable throwable = ((Throwable) getIntent().getSerializableExtra(KEY_THROWABLE));

        if (throwable == null) {
            throwable = new Exception("Could not get exception");
        }

        DeckLog.logError(throwable);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(R.string.error);
        binding.message.setText(throwable.getMessage());

        final String debugInfo = ExceptionUtil.getDebugInfos(this, throwable);

        binding.stacktrace.setText(debugInfo);

        binding.copy.setOnClickListener((v) -> copyToClipboard(this, getString(R.string.simple_exception), "```\n" + debugInfo + "\n```"));
        binding.close.setOnClickListener((v) -> finish());
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, Throwable throwable) {
        return new Intent(context, ExceptionActivity.class)
                .putExtra(KEY_THROWABLE, throwable)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
