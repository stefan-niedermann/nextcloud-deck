package it.niedermann.nextcloud.deck.ui;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.google.android.material.card.MaterialCardView;

import org.junit.Test;

public class E2ETest {

    private UiDevice mDevice;

    @Test
    public void setUp() throws UiObjectNotFoundException {
        setupNextcloudAccount("http://localhost:8080", "Test", "Test");
        importAccountIntoDeck();
        verfiyCardsPresent();
    }

    private void setupNextcloudAccount(String url, String username, String password) throws UiObjectNotFoundException {
        final var CALC_PACKAGE = "com.nextcloud.android.beta";
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());

        // Launch a simple calculator app
        final var context = getInstrumentation().getContext();
        final var intent = context.getPackageManager()
                .getLaunchIntentForPackage(CALC_PACKAGE)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Clear out any previous instances
        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(CALC_PACKAGE).depth(0)), 30);

        mDevice.findObject(new UiSelector().text("Log in")).click();
        mDevice.findObject(new UiSelector().focused(true)).setText(url);
        mDevice.pressEnter();
        mDevice.findObject(new UiSelector().text("Log in")).click();

        mDevice.wait(Until.findObject(By.clazz(WebView.class)), 30);

        final var usernameInput = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(EditText.class));

        usernameInput.waitForExists(30);
        usernameInput.setText(username);

        final var passwordInput = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(EditText.class));

        passwordInput.waitForExists(30);
        passwordInput.setText(password);

        mDevice.findObject(new UiSelector().text("Log in")).click();

        mDevice.findObject(new UiSelector().text("Grant access")).click();
    }

    private void importAccountIntoDeck() throws UiObjectNotFoundException {
        final var CALC_PACKAGE = "it.niedermann.nextcloud.deck.dev";
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());

        // Launch a simple calculator app
        Context context = getInstrumentation().getContext();
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(CALC_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Clear out any previous instances
        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(CALC_PACKAGE).depth(0)), 30);

        final var accountButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));

        accountButton.waitForExists(30);
        accountButton.click();

        final var radioAccount = mDevice.findObject(new UiSelector()
                .clickable(true)
                .instance(0));

        radioAccount.waitForExists(30);
        radioAccount.click();

        final var okButton = mDevice.findObject(new UiSelector().text("OK"));

        okButton.waitForExists(30);
        okButton.click();

        final var allowButton = mDevice.findObject(new UiSelector().text("Allow"));

        allowButton.waitForExists(30);
        allowButton.click();

        final var welcomeText = mDevice.findObject(new UiSelector().description("Filter"));
        welcomeText.waitForExists(30);
//        mDevice.wait
    }

    private void verfiyCardsPresent() {
        final var CALC_PACKAGE = "it.niedermann.nextcloud.deck.dev";
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());

        // Launch a simple calculator app
        Context context = getInstrumentation().getContext();
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(CALC_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Clear out any previous instances
        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(CALC_PACKAGE).depth(0)), 30);

        final var accountButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(MaterialCardView.class));

        accountButton.waitForExists(30);
    }
}
