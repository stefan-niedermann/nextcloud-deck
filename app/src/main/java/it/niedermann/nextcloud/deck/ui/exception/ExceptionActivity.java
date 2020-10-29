package it.niedermann.nextcloud.deck.ui.exception;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityExceptionBinding;
import it.niedermann.nextcloud.deck.ui.exception.tips.TipsAdapter;
import it.niedermann.nextcloud.exception.ExceptionUtil;

public class ExceptionActivity extends AppCompatActivity {

    private static final String KEY_THROWABLE = "throwable";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityExceptionBinding binding = ActivityExceptionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        Throwable throwable = ((Throwable) getIntent().getSerializableExtra(KEY_THROWABLE));

        if (throwable == null) {
            throwable = new Exception("Could not get exception");
        }

        final TipsAdapter adapter = new TipsAdapter(this::startActivity);
        final String debugInfo = "Full Crash:\n\n" + ExceptionUtil.INSTANCE.getDebugInfos(this, throwable, BuildConfig.FLAVOR);

        binding.tips.setAdapter(adapter);
        binding.tips.setNestedScrollingEnabled(false);
        binding.toolbar.setTitle(R.string.error);
        binding.message.setText(throwable.getMessage());
        binding.stacktrace.setText(debugInfo);
        binding.copy.setOnClickListener((v) -> ClipboardUtil.INSTANCE.copyToClipboard(this, getString(R.string.simple_exception), "```\n" + debugInfo + "\n```"));
        binding.close.setOnClickListener((v) -> finish());

        adapter.setThrowable(this, null, throwable);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, Throwable throwable) {
        return new Intent(context, ExceptionActivity.class)
                .putExtra(KEY_THROWABLE, throwable)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
