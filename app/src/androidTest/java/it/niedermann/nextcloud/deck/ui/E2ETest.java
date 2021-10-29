package it.niedermann.nextcloud.deck.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// @RunWith is required only if you use a mix of JUnit3 and JUnit4.
@RunWith(AndroidJUnit4.class)
@SmallTest
public class E2ETest {

//    @Rule
//    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCOUNT_MANAGER);
    @Rule
    public ActivityScenarioRule<ImportAccountActivity> rule = new ActivityScenarioRule<>(ImportAccountActivity.class);

    @Test
    public void sampleTest() {
        final var scenario = rule.getScenario();
        scenario.onActivity(activity -> {
            onView(withText("Task"));
            onView(withText("Halloween"));
        });
    }
}
