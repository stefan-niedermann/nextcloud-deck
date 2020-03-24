package it.niedermann.nextcloud.deck.ui.helper.dnd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.deck.ui.stack.StackFragment;

// Public util
@SuppressWarnings("WeakerAccess")
public class DnDUtil {

    private DnDUtil() {
        // Util class
    }

    protected static StackFragment getStackFragment(@NonNull FragmentManager fm, @Nullable Long currentStackId) throws IllegalArgumentException {
        Fragment fragment = fm.findFragmentByTag("f" + currentStackId);

        if (fragment instanceof StackFragment) {
            return (StackFragment) fragment;
        }

        throw new IllegalArgumentException("fragment with tag \"f" + currentStackId + "\" is not a StackFragment");
    }
}
