package it.niedermann.nextcloud.deck.ui.deeplink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import it.niedermann.nextcloud.deck.ui.main.MainActivity;

public class DeepLinkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class)
                .setAction(Intent.ACTION_VIEW)
                .setData(getIntent().getData())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}
