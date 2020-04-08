package it.niedermann.android.crosstabdnd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

// Public util
@SuppressWarnings("WeakerAccess")
public class DragAndDropUtil {

    private DragAndDropUtil() {
        // Util class
    }

    protected static <T> T getTabFragment(@NonNull FragmentManager fm, @Nullable Long currentStackId) throws IllegalArgumentException {
        return (T) fm.findFragmentByTag("f" + currentStackId);
    }
}
