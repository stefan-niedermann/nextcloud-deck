package it.niedermann.nextcloud.deck.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

public class CallbackUtil {

    public static void runOnUiThread(Fragment fragment, Runnable runnable) {
        if (fragment.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            final var activity = fragment.getActivity();
            runOnUiThread(activity, runnable);
        }
    }

    public static void runOnUiThread(FragmentActivity activity, Runnable runnable) {
        if (activity == null) {
            throw new NullPointerException("Activity must not be null.");
        }

        if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            activity.runOnUiThread(runnable);
        }
    }
}
