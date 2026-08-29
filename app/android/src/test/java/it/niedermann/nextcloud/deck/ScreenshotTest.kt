package it.niedermann.nextcloud.deck

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.repository.MockData
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Capabilities
import it.niedermann.nextcloud.deck.domain.state.SyncStatus
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.reactivestreams.FlowAdapters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(
    application = HiltTestApplication::class,
    sdk = [34],
    qualifiers = "xxxhdpi"
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ScreenshotTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @BindValue
    @JvmField
    val importAccountUseCase: ImportAccountUseCase = mock(ImportAccountUseCase::class.java).also {
        `when`(it.execute(any())).thenReturn(FlowAdapters.toFlowPublisher(Flowable.empty()))
    }
    @BindValue
    @JvmField
    val setCurrentAccountUseCase: SetCurrentAccountUseCase =
        mock(SetCurrentAccountUseCase::class.java).also {
            `when`(it.execute(any())).thenReturn(CompletableFuture.completedFuture(null))
        }
    @BindValue
    @JvmField
    val getAccountsUseCase: GetAccountsUseCase = mock(GetAccountsUseCase::class.java).also {
        `when`(it.execute()).thenReturn(FlowAdapters.toFlowPublisher(Flowable.empty()))
    }
    @BindValue
    @JvmField
    val getCurrentAccountUseCase: GetCurrentAccountUseCase =
        mock(GetCurrentAccountUseCase::class.java).also {
            `when`(it.execute()).thenReturn(CompletableFuture.completedFuture(null))
        }
    @BindValue
    @JvmField
    val getCurrentBoardUseCase: GetCurrentBoardUseCase =
        mock(GetCurrentBoardUseCase::class.java).also {
            `when`(it.execute(any())).thenReturn(CompletableFuture.completedFuture(null))
        }

    private fun getScreenshotPath(fileName: String): String {
        val base = if (File("app/android").exists()) "app/android" else "."
        return "$base/fastlane/metadata/android/en-US/images/phoneScreenshots/$fileName"
    }

    @Before
    fun init() {
        hiltRule.inject()
        File(getScreenshotPath("")).mkdirs()

        val account = Account(
            Account.ID(1),
            URL("https://nextcloud.example.com"),
            "jdoe",
            "token",
            "jdoe@nextcloud.example.com",
            MockData.MOCK_CAPABILITIES
        )
        val syncStatus = SyncStatus(account)

        `when`(importAccountUseCase.execute(any())).thenReturn(
            FlowAdapters.toFlowPublisher(Flowable.just(syncStatus))
        )
        `when`(setCurrentAccountUseCase.execute(any())).thenReturn(
            CompletableFuture.completedFuture(null)
        )
        `when`(getAccountsUseCase.execute()).thenReturn(
            FlowAdapters.toFlowPublisher(Flowable.just(listOf(account)))
        )
        `when`(getCurrentAccountUseCase.execute()).thenReturn(
            CompletableFuture.completedFuture(account.id())
        )
        `when`(getCurrentBoardUseCase.execute(any())).thenReturn(
            CompletableFuture.completedFuture(null)
        )
    }

    @Test
    fun captureScreenshots() {
        // Capture Login Screen
        composeTestRule.onNodeWithText("Server URL")
            .performTextInput("https://nextcloud.example.com")
        composeTestRule.onNodeWithText("Username").performTextInput("jdoe")
        composeTestRule.onNodeWithText("Password").performTextInput("password")
        composeTestRule.onRoot().captureRoboImage(getScreenshotPath("01_login.png"))

        // Perform Login
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for Board List
        // FIXME Wait until progress spinner disappears, mock cards and perform another screenshot
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(getScreenshotPath("02_board_list.png"))
    }
}
