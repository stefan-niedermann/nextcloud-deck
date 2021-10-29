package it.niedermann.nextcloud.deck.ui;

import android.app.Instrumentation;
import android.view.KeyEvent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

// @RunWith is required only if you use a mix of JUnit3 and JUnit4.
@RunWith(AndroidJUnit4.class)
@SmallTest
public class E2ETest {

    @Rule
    public ActivityScenarioRule<ImportAccountActivity> rule = new ActivityScenarioRule<>(ImportAccountActivity.class);

    @Test
    public void sampleTest() {
        final var scenario = rule.getScenario();
        scenario.onActivity(activity -> {
            final var latch = new CountDownLatch(1);
            activity.binding.addButton.performClick();
            new Thread(() -> {
                try {
                    final var inst = new Instrumentation();
                    Thread.sleep(1_000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    Thread.sleep(1_000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_SPACE);
                    Thread.sleep(1_000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    Thread.sleep(1_000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    Thread.sleep(1_000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
                    Thread.sleep(1_000);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
