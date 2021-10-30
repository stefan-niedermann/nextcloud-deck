package it.niedermann.nextcloud.deck.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import it.niedermann.nextcloud.deck.R;

public class E2ETest {

    public ActivityScenario<MainActivity> scenario;
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void sampleTest() {
        scenario = rule.getScenario();
        scenario.onActivity(activity -> {
            final var fieldMatcher = withId(R.id.filter);
            final var interaction = onView(fieldMatcher);
            final var matcher = isDisplayed();
            final var assertion = matches(matcher);
            interaction.check(assertion);
//            onView(withText("Halloween")).check(matches(isDisplayed()));
        });
    }

    @After
    public void cleanup() {
        scenario.close();
    }
}
