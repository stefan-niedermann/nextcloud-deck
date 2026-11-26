package it.niedermann.nextcloud.deck.ui.board.edit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.DialogInterface;
import android.os.Looper;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;

@RunWith(RobolectricTestRunner.class)
public class EditBoardDialogFragmentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Account account;

    @Before
    public void setup() {
        account = new Account();
        account.setId(1L);
        account.setUserName("testuser");
    }

    @Test
    public void testAddBoardButtonDisabledWhenTitleIsEmpty() {
        try (final var activityController = Robolectric.buildActivity(TestActivity.class).setup()) {
            final var activity = activityController.get();

            final var dialogFragment = EditBoardDialogFragment.newInstance(account);
            dialogFragment.show(activity.getSupportFragmentManager(), "EditBoardDialogFragment");
            shadowOf(Looper.getMainLooper()).idle();

            final var dialog = (AlertDialog) dialogFragment.getDialog();
            assertNotNull(dialog);

            final var positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            assertNotNull(positiveButton);
            assertFalse("Add button should be disabled when title is empty", positiveButton.isEnabled());
        }
    }

    @Test
    public void testAddBoardButtonEnabledWhenTitleIsProvided() {
        try (final var activityController = Robolectric.buildActivity(TestActivity.class).setup()) {
            final var activity = activityController.get();

            final var dialogFragment = EditBoardDialogFragment.newInstance(account);
            dialogFragment.show(activity.getSupportFragmentManager(), "EditBoardDialogFragment");
            shadowOf(Looper.getMainLooper()).idle();

            final var dialog = (AlertDialog) dialogFragment.getDialog();
            assertNotNull(dialog);

            final var editText = dialog.<EditText>findViewById(R.id.input);
            assertNotNull(editText);

            final var positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            assertNotNull(positiveButton);

            editText.setText("New Board Title");
            shadowOf(Looper.getMainLooper()).idle();
            assertTrue("Add button should be enabled when title is provided", positiveButton.isEnabled());

            editText.setText("");
            shadowOf(Looper.getMainLooper()).idle();
            assertFalse("Add button should be disabled when title is cleared", positiveButton.isEnabled());

            editText.setText("   ");
            shadowOf(Looper.getMainLooper()).idle();
            assertFalse("Add button should be disabled when title is whitespace only", positiveButton.isEnabled());
        }
    }

    public static class TestActivity extends AppCompatActivity implements EditBoardListener {
        @Override
        public void onCreateBoard(@NonNull Account account, String title, int color) {}

        @Override
        public void onUpdateBoard(FullBoard fullBoard) {}

        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {}
    }
}
