package it.niedermann.nextcloud.deck

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.remote.ApiProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScreenshotTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.v2.createAndroidComposeRule<MainActivity>()

    @Inject lateinit var apiProviderFactory: ApiProvider.Factory

    private fun getScreenshotPath(fileName: String): String {
        val base = if (File("app/android").exists()) "app/android" else "."
        return "$base/fastlane/metadata/android/en-US/images/phoneScreenshots/$fileName"
    }

    @Before
    fun init() {
        hiltRule.inject()
        File(getScreenshotPath("")).mkdirs()
        
        // Mock ApiProvider to avoid real network calls
        val apiProvider = mock(ApiProvider::class.java)
        `when`(apiProviderFactory.create(any<Account>())).thenReturn(apiProvider)
    }

    @Test
    fun captureScreenshots() {
        // Capture Login Screen
        composeTestRule.onNodeWithText("Server URL").performTextInput("https://nextcloud.example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("jdoe")
        composeTestRule.onNodeWithText("Password").performTextInput("password")
        composeTestRule.onRoot().captureRoboImage(getScreenshotPath("1_login.png"))

        // Perform Login
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for Board List
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(getScreenshotPath("2_board_list.png"))
    }
}
