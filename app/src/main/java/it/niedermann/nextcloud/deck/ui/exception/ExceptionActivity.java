package it.niedermann.nextcloud.deck.ui.exception;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;

public class ExceptionActivity extends AppCompatActivity {

    Throwable throwable;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.stacktrace)
    TextView stacktrace;
    @BindString(R.string.error)
    String title;
    @BindString(R.string.simple_exception)
    String exception;
    @BindString(R.string.copied_to_clipboard)
    String copiedToClipboard;

    public static final String KEY_THROWABLE = "T";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_exception);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        throwable = ((Throwable) getIntent().getSerializableExtra(KEY_THROWABLE));
        throwable.printStackTrace();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        this.message.setText(throwable.getMessage());
        this.stacktrace.setText("Version: " + BuildConfig.VERSION_NAME + "\n\n" + getStacktraceOf(throwable));
    }

    private String getStacktraceOf(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


    @OnClick(R.id.copy)
    void copyStacktraceToClipboard() {
        final android.content.ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(exception, this.stacktrace.getText());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, copiedToClipboard, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.close)
    void close() {
        finish();
    }
}
