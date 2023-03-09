package it.niedermann.nextcloud.deck.util;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

/**
 * @see <a href="https://github.com/nextcloud/android/pull/10962">Source</a>
 */
public class KeyboardUtils {
    private final static long SHOW_INPUT_DELAY_MILLIS = 100L;

    public static void showKeyboardForEditText(@NonNull EditText editText) {
        editText.requestFocus();
        // needs delay to account for focus animations
        editText.postDelayed(() -> {
            final var context = editText.getContext();
            if (context != null) {
                final var inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, SHOW_IMPLICIT);
            }
        }, SHOW_INPUT_DELAY_MILLIS);
    }
}