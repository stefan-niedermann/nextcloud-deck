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

import it.niedermann.nextcloud.deck.BuildConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class E2ETest {

    private static final String TAG = E2ETest.class.getSimpleName();

    private UiDevice mDevice;

    private static final int TIMEOUT = 60_000;

    private static final String APP_NEXTCLOUD = "com.nextcloud.android.beta";
    private static final String APP_DECK = BuildConfig.APPLICATION_ID;

    private static final String SERVER_URL = "http://172.17.0.1:8080";
    private static final String SERVER_USERNAME = "Test";
    private static final String SERVER_PASSWORD = "Test";

    @Before
    public void before() {
        mDevice = UiDevice.getInstance(getInstrumentation());
        mDevice.pressHome();
    }

    @After
    public void after() {
        mDevice.pressHome();
    }

    @Test
    public void test_00_configureNextcloudAccount() throws UiObjectNotFoundException {
        launch(APP_NEXTCLOUD);

        final var loginButton = mDevice.findObject(new UiSelector().textContains("Log in"));
        loginButton.waitForExists(TIMEOUT);
        log("Login Button exists. Clicking on it...");
        loginButton.click();
        log("Login Button clicked.");

        final var urlInput = mDevice.findObject(new UiSelector().focused(true));
        urlInput.waitForExists(TIMEOUT);
        log("URL input exists.");
        log("Entering URL...");
        urlInput.setText(SERVER_URL);
        log("URL entered.");

        log("Pressing enter...");
        mDevice.pressEnter();
        log("Enter pressed.");

        log("Waiting for WebView...");
        mDevice.wait(Until.findObject(By.clazz(WebView.class)), TIMEOUT);
        log("WebView exists.");

        final var webViewLoginButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        log("Waiting for WebView Login Button...");
        webViewLoginButton.waitForExists(TIMEOUT);
        log("WebView Login Button exists. Clicking on it...");
        webViewLoginButton.click();

        final var usernameInput = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(EditText.class));
        log("Waiting for Username Input...");
        usernameInput.waitForExists(TIMEOUT);
        log("Username Input exists. Setting text...");
        usernameInput.setText(SERVER_USERNAME);
        log("Username has been set.");

        final var passwordInput = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(EditText.class));
        log("Waiting for Password Input...");
        passwordInput.waitForExists(TIMEOUT);
        log("Password Input exists. Setting text...");
        passwordInput.setText(SERVER_PASSWORD);

        final var webViewSubmitButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        log("Waiting for WebView Submit Button...");
        webViewSubmitButton.waitForExists(TIMEOUT);
        log("WebView Submit Button exists. Clicking on it...");
        webViewSubmitButton.click();

        final var webViewGrantAccessButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        log("Waiting for WebView Grant Access Button...");
        webViewGrantAccessButton.waitForExists(TIMEOUT);
        log("WebView Grant Access Button exists. Clicking on it...");
        webViewGrantAccessButton.click();
    }

    @Test
    public void test_01_importAccountIntoDeck() throws UiObjectNotFoundException, InterruptedException {
        launch(APP_DECK);

        final var accountButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        accountButton.waitForExists(TIMEOUT);
        accountButton.click();

        mDevice.waitForWindowUpdate(null, TIMEOUT);

        final var radioAccount = mDevice.findObject(new UiSelector()
                .clickable(true)
                .instance(0));
        radioAccount.waitForExists(TIMEOUT);
        radioAccount.click();

        mDevice.waitForWindowUpdate(null, TIMEOUT);

        Thread.sleep(10_000);
        final var okButton = mDevice.findObject(new UiSelector()
        .className(Button.class)
        .instance(2));
        log("Waiting for OK Button...");
        okButton.waitForExists(TIMEOUT);
        log("OK Button exists. Clicking on it...");
        okButton.click();
        log("OK Button clicked");

        mDevice.waitForWindowUpdate(null, TIMEOUT);

        final var allowButton = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(Button.class));
        log("Waiting for Allow Button...");
        allowButton.waitForExists(TIMEOUT);
        log("Allow Button exists. Clicking on it...");
        allowButton.click();
        log("Allow Button clicked");

        log("Waiting for finished import...");
        final var welcomeText = mDevice.findObject(new UiSelector().description("Filter"));
        welcomeText.waitForExists(TIMEOUT);
        log("Import finished.");
    }

    @Test
    public void test_02_verifyCardsPresent() throws UiObjectNotFoundException {
        launch(APP_DECK);

        final var taskCard = mDevice.findObject(new UiSelector()
                .textContains("Task 3"));
        taskCard.waitForExists(TIMEOUT);
        System.out.println("Found: " + taskCard.getText());
    }
    
    private void log(@NonNull String message) {
        Log.e(TAG, message);
    }

    private void launch(@NonNull String packageName) {
        final var context = getInstrumentation().getContext();
        context.startActivity(context
                .getPackageManager()
                .getLaunchIntentForPackage(packageName)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        mDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT);
    }
}
