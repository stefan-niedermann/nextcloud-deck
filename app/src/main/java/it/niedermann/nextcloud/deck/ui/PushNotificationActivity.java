package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.DeckLog;

public class PushNotificationActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Intent receivedIntent = getIntent();
        String receivedAction;
        String receivedType;
        receivedAction = receivedIntent.getAction();
        receivedType = receivedIntent.getType();
        DeckLog.info(receivedAction);
        DeckLog.info(receivedType);

        // TODO simply open the given URL until proper handling has been implemented
    }
}
