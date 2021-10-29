import static org.hamcrest.MatcherAssert.assertThat;

import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

// @RunWith is required only if you use a mix of JUnit3 and JUnit4.
@RunWith(AndroidJUnit4.class)
@SmallTest
public class E2ETest {

    @Test
    public void sampleTest() {
        assertThat("Test fail on purpose", false);
    }
}
