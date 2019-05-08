package it.niedermann.nextcloud.deck.ui.exception;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;

public class ExceptionActivity extends AppCompatActivity {


    @BindView(R.id.message)
    TextView message;

    public static final String KEY_THROWABLE = "T";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_exception);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        
        this.message.setText(((Throwable) savedInstanceState.getSerializable(KEY_THROWABLE)).getMessage());
    }
}
