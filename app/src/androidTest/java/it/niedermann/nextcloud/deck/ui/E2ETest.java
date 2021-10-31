package it.niedermann.nextcloud.deck.ui;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Intent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
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
    public void e2e() throws UiObjectNotFoundException {
        mDevice = UiDevice.getInstance(getInstrumentation());
        launch("com.nextcloud.android.beta");
        configureNextcloudAccount("nextcloud.knecht.rocks", "stefan", "stefaniststinkt");

        launch("it.niedermann.nextcloud.deck.dev");
        importAccountIntoDeck();

        verifyCardsPresent();
    }


    private void configureNextcloudAccount(String url, String username, String password) throws UiObjectNotFoundException {
        final var loginButton1 = mDevice.findObject(new UiSelector().text("Log in"));
        loginButton1.waitForExists(30);
        screenshot("setup-2");
        loginButton1.click();

        mDevice.findObject(new UiSelector().focused(true)).setText(url);
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
        usernameInput.setText(username);

        final var passwordInput = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(EditText.class));

        passwordInput.waitForExists(30);
        screenshot("setup-7");
        passwordInput.setText(password);

        mDevice.findObject(new UiSelector().text("Log in")).click();
        screenshot("setup-8");

        mDevice.findObject(new UiSelector().text("Grant access")).click();
        screenshot("setup-9");
    }

    private void importAccountIntoDeck() throws UiObjectNotFoundException {
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
    }

    private void verifyCardsPresent() {
        final var accountButton = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(MaterialCardView.class));

        accountButton.waitForExists(30);
        screenshot("deck-validate-1");
    }

    private void launch(@NonNull String packageName) {
        final var context = getInstrumentation().getContext();
        context.startActivity(context
                .getPackageManager()
                .getLaunchIntentForPackage(packageName)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        mDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), 30);
        screenshot("launch-" + packageName + ".png");
    }

    private void screenshot(@NonNull String name) {
        mDevice.takeScreenshot(new File("/sdcard/screenshots/" + name + ".png"));
    }
}
