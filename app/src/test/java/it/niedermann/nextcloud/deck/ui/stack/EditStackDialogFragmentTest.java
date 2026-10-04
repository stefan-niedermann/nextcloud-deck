package it.niedermann.nextcloud.deck.ui.stack;

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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.R;

@RunWith(RobolectricTestRunner.class)
public class EditStackDialogFragmentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void testAddStackButtonDisabledWhenTitleIsEmpty() {
        try (final var activityController = Robolectric.buildActivity(TestActivity.class).setup()) {
            final var activity = activityController.get();

            final var dialogFragment = EditStackDialogFragment.newInstance(1L, 1L);
            dialogFragment.show(activity.getSupportFragmentManager(), "EditStackDialogFragment");
            shadowOf(Looper.getMainLooper()).idle();

            final var dialog = (AlertDialog) dialogFragment.getDialog();
            assertNotNull(dialog);

            final var positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            assertNotNull(positiveButton);
            assertFalse("Add list button should be disabled when title is empty", positiveButton.isEnabled());
        }
    }

    @Test
    public void testAddStackButtonEnabledWhenTitleIsProvided() {
        try (final var activityController = Robolectric.buildActivity(TestActivity.class).setup()) {
            final var activity = activityController.get();

            final var dialogFragment = EditStackDialogFragment.newInstance(1L, 1L);
            dialogFragment.show(activity.getSupportFragmentManager(), "EditStackDialogFragment");
            shadowOf(Looper.getMainLooper()).idle();

            final var dialog = (AlertDialog) dialogFragment.getDialog();
            assertNotNull(dialog);

            final var editText = dialog.<EditText>findViewById(R.id.input);
            assertNotNull(editText);

            final var positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            assertNotNull(positiveButton);

            editText.setText("New List Title");
            shadowOf(Looper.getMainLooper()).idle();
            assertTrue("Add list button should be enabled when title is provided", positiveButton.isEnabled());

            editText.setText("");
            shadowOf(Looper.getMainLooper()).idle();
            assertFalse("Add list button should be disabled when title is cleared", positiveButton.isEnabled());

            editText.setText("   ");
            shadowOf(Looper.getMainLooper()).idle();
            assertFalse("Add list button should be disabled when title is whitespace only", positiveButton.isEnabled());
        }
    }

    public static class TestActivity extends AppCompatActivity implements EditStackListener {
        @Override
        public void onCreateStack(long accountId, long boardId, @NonNull String title) {}

        @Override
        public void onUpdateStack(long stackId, @NonNull String title) {}

        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {}
    }
}
