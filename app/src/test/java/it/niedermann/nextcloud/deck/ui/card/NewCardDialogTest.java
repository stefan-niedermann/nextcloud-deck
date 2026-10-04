package it.niedermann.nextcloud.deck.ui.card;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.robolectric.Shadows.shadowOf;

import android.content.DialogInterface;
import android.os.Looper;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;

@RunWith(RobolectricTestRunner.class)
public class NewCardDialogTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Account account;

    @Before
    public void setup() {
        account = new Account();
        account.setId(1L);
        account.setUserName("testuser");
        account.setName("testuser");
    }

    @Test
    public void testAddCardButtonsDisabledWhenTitleIsEmpty() {
        try (var mockedAccountImporter = Mockito.mockStatic(AccountImporter.class)) {
            mockedAccountImporter.when(() -> AccountImporter.getSingleSignOnAccount(any(), any()))
                    .thenReturn(Mockito.mock(SingleSignOnAccount.class));

            try (final var activityController = Robolectric.buildActivity(TestActivity.class).setup()) {
                final var activity = activityController.get();

                final var dialogFragment = NewCardDialog.newInstance(account, 1L, 1L);
                dialogFragment.show(activity.getSupportFragmentManager(), "NewCardDialog");
                shadowOf(Looper.getMainLooper()).idle();

                final var dialog = (AlertDialog) dialogFragment.getDialog();
                assertNotNull(dialog);

                final var positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final var negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                assertNotNull(positiveButton);
                assertNotNull(negativeButton);

                assertFalse("Save button should be disabled when title is empty", positiveButton.isEnabled());
                assertFalse("Edit button should be disabled when title is empty", negativeButton.isEnabled());
            }
        }
    }

    @Test
    public void testAddCardButtonsEnabledWhenTitleIsProvided() {
        try (var mockedAccountImporter = Mockito.mockStatic(AccountImporter.class)) {
            mockedAccountImporter.when(() -> AccountImporter.getSingleSignOnAccount(any(), any()))
                    .thenReturn(Mockito.mock(SingleSignOnAccount.class));

            try (final var activityController = Robolectric.buildActivity(TestActivity.class).setup()) {
                final var activity = activityController.get();

                final var dialogFragment = NewCardDialog.newInstance(account, 1L, 1L);
                dialogFragment.show(activity.getSupportFragmentManager(), "NewCardDialog");
                shadowOf(Looper.getMainLooper()).idle();

                final var dialog = (AlertDialog) dialogFragment.getDialog();
                assertNotNull(dialog);

                final var editText = dialog.<EditText>findViewById(R.id.input);
                assertNotNull(editText);

                final var positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final var negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                assertNotNull(positiveButton);
                assertNotNull(negativeButton);

                editText.setText("New Card Title");
                shadowOf(Looper.getMainLooper()).idle();
                assertTrue("Save button should be enabled when title is provided", positiveButton.isEnabled());
                assertTrue("Edit button should be enabled when title is provided", negativeButton.isEnabled());

                editText.setText("");
                shadowOf(Looper.getMainLooper()).idle();
                assertFalse("Save button should be disabled when title is cleared", positiveButton.isEnabled());
                assertFalse("Edit button should be disabled when title is cleared", negativeButton.isEnabled());

                editText.setText("   ");
                shadowOf(Looper.getMainLooper()).idle();
                assertFalse("Save button should be disabled when title is whitespace only", positiveButton.isEnabled());
                assertFalse("Edit button should be disabled when title is whitespace only", negativeButton.isEnabled());
            }
        }
    }

    public static class TestActivity extends AppCompatActivity implements CreateCardListener {
        @Override
        public void onCardCreated(@NonNull FullCard fullCard) {}

        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {}
    }
}
