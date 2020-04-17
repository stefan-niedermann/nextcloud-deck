package it.niedermann.nextcloud.deck.ui.exception;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityExceptionBinding;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class ExceptionActivity extends AppCompatActivity {

    private String debugInfo;
    public static final String KEY_THROWABLE = "T";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final ActivityExceptionBinding binding = ActivityExceptionBinding.inflate(getLayoutInflater());
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

        binding.copy.setOnClickListener((v) -> copyToClipboard(this, getString(R.string.simple_exception), "```\n" + debugInfo + "\n```"));
        binding.close.setOnClickListener((v) -> finish());
    }
}
