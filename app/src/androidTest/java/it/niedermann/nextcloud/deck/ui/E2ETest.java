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
    private static final String APP_DECK = "it.niedermann.nextcloud.deck.dev";

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

        log("SYSTEM ENV VAR APP_ID: " + BuildConfig.APPLICATION_ID);
        log("SYSTEM ENV VAR URL: " + System.getenv("NEXTCLOUD_URL"));
        log("SYSTEM ENV VAR USER: " + System.getenv("NEXTCLOUD_USER"));
        log("SYSTEM ENV VAR PASSWORD: " + System.getenv("NEXTCLOUD_PASSWORD"));

        log("FIRST");
        final var loginButton = mDevice.findObject(new UiSelector().textContains("Log in"));
        loginButton.waitForExists(TIMEOUT);
        log("LOGINBUTTON EXISTS. CLICKING ON IT...");
        loginButton.click();
        log("LOGINBUTTON CLICKED");

        final var urlInput = mDevice.findObject(new UiSelector().focused(true));
        urlInput.waitForExists(TIMEOUT);
        log("URL INPUT IS PRESENT");
        log("ENTERING URL...");
        urlInput.setText(SERVER_URL);
        log("URL ENTERED.");

        log("PRESSING NOW ENTER...");
        mDevice.pressEnter();
        log("ENTER PRESSED.");

        log("WAITING FOR WEBVIEW...");
        mDevice.wait(Until.findObject(By.clazz(WebView.class)), TIMEOUT);
        log("WEBVIEW IS PRESENT");

        final var webViewLoginButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        log("WAITING FOR WEBVIEW LOGIN BUTTON TO BE PRESENT...");
        webViewLoginButton.waitForExists(TIMEOUT);
        log("WEBVIEW LOGIN BUTTON IS PRESENT. CLICKING ON IT...");
        webViewLoginButton.click();

        final var usernameInput = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(EditText.class));
        log("WAITING FOR USERNAME INPUT TO BE PRESENT...");
        usernameInput.waitForExists(TIMEOUT);
        log("USERNAME INPUT IS PRESENT. SETTING TEXT...");
        usernameInput.setText(SERVER_USERNAME);
        log("USERNAME HAS BEEN SET.");

        final var passwordInput = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(EditText.class));
        log("WAITING FOR USERNAME INPUT TO BE PRESENT...");
        passwordInput.waitForExists(TIMEOUT);
        log("USERNAME INPUT IS PRESENT. SETTING TEXT...");
        passwordInput.setText(SERVER_PASSWORD);

        log("THIRD");

        final var webViewSubmitButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        log("WAITING FOR WEBVIEW SUBMIT BUTTON TO BE PRESENT...");
        webViewSubmitButton.waitForExists(TIMEOUT);
        log("WEBVIEW SUBMIT BUTTON IS PRESENT. CLICKING ON IT...");
        webViewSubmitButton.click();

        final var webViewGrantAccessButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));
        log("WAITING FOR WEBVIEW GRANT ACCESS BUTTON TO BE PRESENT...");
        webViewGrantAccessButton.waitForExists(TIMEOUT);
        log("WEBVIEW GRANT ACCESS BUTTON IS PRESENT. CLICKING ON IT...");
        webViewGrantAccessButton.click();
    }

    @Test
    public void test_01_importAccountIntoDeck() throws UiObjectNotFoundException {
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

        final var okButton = mDevice.findObject(new UiSelector().text("OK"));
        okButton.waitForExists(TIMEOUT);
        okButton.click();

        mDevice.waitForWindowUpdate(null, TIMEOUT);

        final var allowButton = mDevice.findObject(new UiSelector().text("Allow"));
        allowButton.waitForExists(TIMEOUT);
        allowButton.click();

        final var welcomeText = mDevice.findObject(new UiSelector().description("Filter"));
        welcomeText.waitForExists(TIMEOUT);
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
