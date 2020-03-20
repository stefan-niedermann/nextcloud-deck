package it.niedermann.nextcloud.deck.ui.exception;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityExceptionBinding;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

public class ExceptionActivity extends AppCompatActivity {

    private String debugInfo;
    public static final String KEY_THROWABLE = "T";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityExceptionBinding binding = ActivityExceptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        super.onCreate(savedInstanceState);

        Throwable throwable = ((Throwable) getIntent().getSerializableExtra(KEY_THROWABLE));

        if (throwable == null) {
            throwable = new Exception("Could not get exception");
        }

        throwable.printStackTrace();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(R.string.error);
        binding.message.setText(throwable.getMessage());

        debugInfo = ExceptionUtil.getDebugInfos(this, throwable);

        binding.stacktrace.setText(debugInfo);

        binding.copy.setOnClickListener((v) -> copyStacktraceToClipboard());
        binding.close.setOnClickListener((v) -> finish());
    }

    private void copyStacktraceToClipboard() {
        final android.content.ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.simple_exception), "```\n" + debugInfo + "\n```");
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.could_not_copy_to_clipboard, Toast.LENGTH_LONG).show();
        }
    }
}
