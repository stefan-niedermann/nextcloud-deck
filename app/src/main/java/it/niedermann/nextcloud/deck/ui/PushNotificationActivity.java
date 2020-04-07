package it.niedermann.nextcloud.deck.ui;

import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.DeckLog;

public class PushNotificationActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        // when app is running in background or is starting after force reset
        super.onResume();

        // todo check if getIntent really exists
        String account = getIntent().getStringExtra("account");
        DeckLog.info("push: " + account);

        int nid = getIntent().getIntExtra("nid", -1);
        DeckLog.info("push: " + nid);

        // TODO simply open the given URL until proper handling has been implemented
    }
}
