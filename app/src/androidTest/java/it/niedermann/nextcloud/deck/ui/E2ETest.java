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

import java.io.File;

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

        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-1.png"));

        final var loginButton1 = mDevice.findObject(new UiSelector().text("Log in"));

        loginButton1.waitForExists(30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-2.png"));
        loginButton1.click();

        mDevice.findObject(new UiSelector().focused(true)).setText(url);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-3.png"));
        mDevice.pressEnter();
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-4.png"));
        mDevice.findObject(new UiSelector().text("Log in")).click();

        mDevice.wait(Until.findObject(By.clazz(WebView.class)), 30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-5.png"));

        final var usernameInput = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(EditText.class));

        usernameInput.waitForExists(30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-6.png"));
        usernameInput.setText(username);

        final var passwordInput = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(EditText.class));

        passwordInput.waitForExists(30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-7.png"));
        passwordInput.setText(password);

        mDevice.findObject(new UiSelector().text("Log in")).click();
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-8.png"));

        mDevice.findObject(new UiSelector().text("Grant access")).click();
        mDevice.takeScreenshot(new File("/sdcard/screenshots/setup-9.png"));
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
        mDevice.takeScreenshot(new File("/sdcard/screenshots/1.png"));
        accountButton.click();

        final var radioAccount = mDevice.findObject(new UiSelector()
                .clickable(true)
                .instance(0));

        radioAccount.waitForExists(30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/2.png"));
        radioAccount.click();

        final var okButton = mDevice.findObject(new UiSelector().text("OK"));

        okButton.waitForExists(30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/3.png"));
        okButton.click();

        final var allowButton = mDevice.findObject(new UiSelector().text("Allow"));

        allowButton.waitForExists(30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/4.png"));
        allowButton.click();

        final var welcomeText = mDevice.findObject(new UiSelector().description("Filter"));
        welcomeText.waitForExists(30);
        mDevice.takeScreenshot(new File("/sdcard/screenshots/5.png"));
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
