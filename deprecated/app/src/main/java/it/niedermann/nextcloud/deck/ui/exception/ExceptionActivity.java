package it.niedermann.nextcloud.deck.ui.exception;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityExceptionBinding;
import it.niedermann.nextcloud.deck.ui.exception.tips.TipsAdapter;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.exception.ExceptionUtil;

public class ExceptionActivity extends AppCompatActivity {

    private static final String KEY_THROWABLE = "throwable";
    private ActivityExceptionBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExceptionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        Throwable throwable = ((Throwable) getIntent().getSerializableExtra(KEY_THROWABLE));

        if (throwable == null) {
            throwable = new Exception("Could not get exception");
        }

        final var adapter = new TipsAdapter(this::startActivity);
        String debugInfo;

        try {
            debugInfo = "Full Crash:\n\n" + ExceptionUtil.getDebugInfos(this, throwable, BuildConfig.FLAVOR);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            debugInfo = "Full Crash:\n\n" + sw;
        }

        final var finalDebugInfo = debugInfo;

        binding.tips.setAdapter(adapter);
        binding.tips.setNestedScrollingEnabled(false);
        binding.toolbar.setTitle(R.string.error);
        binding.message.setText(throwable.getMessage());
        binding.stacktrace.setText(finalDebugInfo);
        binding.copy.setOnClickListener((v) -> ClipboardUtil.copyToClipboard(this, getString(R.string.simple_exception), "```\n" + finalDebugInfo + "\n```"));
        binding.close.setOnClickListener((v) -> finish());

        final var utils = ThemeUtils.defaultBrand(this);

        utils.material.colorMaterialButtonText(binding.close);
        utils.material.colorMaterialButtonPrimaryFilled(binding.copy);

        adapter.setThrowable(this, null, throwable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, Throwable throwable) {
        return new Intent(context, ExceptionActivity.class)
                .putExtra(KEY_THROWABLE, throwable)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
