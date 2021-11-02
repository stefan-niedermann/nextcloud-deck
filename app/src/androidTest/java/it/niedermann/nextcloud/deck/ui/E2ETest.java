package it.niedermann.nextcloud.deck.ui;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class E2ETest {

    private UiDevice mDevice;

    private static final String TAG = E2ETest.class.getSimpleName();

    private static final String APP_NEXTCLOUD = "com.nextcloud.android.beta";
    private static final String APP_DECK = "it.niedermann.nextcloud.deck.dev";

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String SERVER_USERNAME = "Test";
    private static final String SERVER_PASSWORD = "Test";

    @Before
    public void before() {
        mDevice = UiDevice.getInstance(getInstrumentation());
    }

    @After
    public void after() {
        mDevice.pressHome();
    }

    @Test
    public void test_00_configureNextcloudAccount() throws UiObjectNotFoundException {
        Log.i(TAG, "START test_00_configureNextcloudAccount");

        launch(APP_NEXTCLOUD);

        final var loginButton1 = mDevice.findObject(new UiSelector().text("Log in"));
        loginButton1.waitForExists(30);
        screenshot("setup-2");
        loginButton1.click();

        mDevice.findObject(new UiSelector().focused(true)).setText(SERVER_URL);
        screenshot("setup-3");
        mDevice.pressEnter();
        screenshot("setup-4");
        mDevice.findObject(new UiSelector().text("Log in")).click();

        mDevice.wait(Until.findObject(By.clazz(WebView.class)), 30);
        screenshot("setup-5");

        final var usernameInput = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(EditText.class));

        usernameInput.waitForExists(30);
        screenshot("setup-6");
        usernameInput.setText(SERVER_USERNAME);

        final var passwordInput = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(EditText.class));

        passwordInput.waitForExists(30);
        screenshot("setup-7");
        passwordInput.setText(SERVER_PASSWORD);

        mDevice.findObject(new UiSelector().text("Log in")).click();
        screenshot("setup-8");

        mDevice.findObject(new UiSelector().text("Grant access")).click();
        screenshot("setup-9");

        Log.i(TAG, "END test_00_configureNextcloudAccount");
    }

    @Test
    public void test_01_importAccountIntoDeck() throws UiObjectNotFoundException {
        Log.i(TAG, "START test_01_importAccountIntoDeck");
        launch(APP_DECK);

        final var accountButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));

        accountButton.waitForExists(30);
        screenshot("deck-1");
        accountButton.click();

        final var radioAccount = mDevice.findObject(new UiSelector()
                .clickable(true)
                .instance(0));

        radioAccount.waitForExists(30);
        screenshot("deck-2");
        radioAccount.click();

        final var okButton = mDevice.findObject(new UiSelector().text("OK"));

        okButton.waitForExists(30);
        screenshot("deck-3");
        okButton.click();

        final var allowButton = mDevice.findObject(new UiSelector().text("Allow"));

        allowButton.waitForExists(30);
        screenshot("deck-4");
        allowButton.click();

        final var welcomeText = mDevice.findObject(new UiSelector().description("Filter"));
        welcomeText.waitForExists(30);
        screenshot("deck-5");
        Log.i(TAG, "END test_01_importAccountIntoDeck");
    }

    @Test
    public void test_02_verifyCardsPresent() throws UiObjectNotFoundException {
        Log.i(TAG, "START test_02_verifyCardsPresent");
        launch(APP_DECK);

        final var taskCard = mDevice.findObject(new UiSelector()
                .textContains("task1234444"));

        taskCard.waitForExists(30);
        Log.i(TAG, taskCard.getText());
        screenshot("deck-validate-1");
        Log.i(TAG, "END test_02_verifyCardsPresent");
    }

    private void launch(@NonNull String packageName) {
        Log.i(TAG, "... LAUNCH " + packageName);
        final var context = getInstrumentation().getContext();
        context.startActivity(context
                .getPackageManager()
                .getLaunchIntentForPackage(packageName)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        mDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), 30);
        screenshot("launch-" + packageName + ".png");
    }

    private void screenshot(@NonNull String name) {
//        try {
//            Runtime.getRuntime().exec("screencap -p " + "/sdcard/screenshots" + name).waitFor();
            // This throws an exception because the file system is read only.
//            mDevice.takeScreenshot(new File(getInstrumentation().getContext().getFilesDir() + "/screenshots/" + name + ".png"));
//        } catch (Throwable ignored) {
//
//        }
    }
}
