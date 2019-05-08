package it.niedermann.nextcloud.deck.ui.exception;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

public class ExceptionActivity extends AppCompatActivity {

    Throwable throwable;

    @BindView(R.id.message)
    TextView message;

    public static final String KEY_THROWABLE = "T";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_exception);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        throwable = ((Throwable) getIntent().getSerializableExtra(KEY_THROWABLE));
        getSupportActionBar().setTitle(throwable.getMessage());
        this.message.setText(throwable.getMessage());
        DeckLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++ 5");
    }
}
