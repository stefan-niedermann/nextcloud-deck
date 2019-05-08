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


    @BindView(R.id.message)
    TextView message;

    public static final String KEY_THROWABLE = "T";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DeckLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++ 1");
        setContentView(R.layout.activity_exception);
        DeckLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++ 2");
        ButterKnife.bind(this);
        DeckLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++ 3");
        super.onCreate(savedInstanceState);

        DeckLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++ 4");

        this.message.setText(((Throwable) savedInstanceState.getSerializable(KEY_THROWABLE)).getMessage());
        DeckLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++ 5");
    }
}
