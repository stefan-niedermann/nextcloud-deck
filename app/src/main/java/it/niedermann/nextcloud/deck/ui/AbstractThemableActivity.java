package it.niedermann.nextcloud.deck.ui;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.Application;

public abstract class AbstractThemableActivity extends AppCompatActivity implements Application.NextcloudTheme {

    @Override
    protected void onResume() {
        super.onResume();
        Application.registerThemableComponent(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.deregisterThemableComponent(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Application.deregisterThemableComponent(this);
    }

    @CallSuper
    @Override
    public void applyNextcloudTheme(int mainColor, int textColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mainColor);
        }
    }
}
