package it.niedermann.nextcloud.deck.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

public class CallbackUtil {

    public static void runOnUiThread(Fragment fragment, Runnable runnable) {
        runOnUiThreadOrElse(fragment, runnable, null);
    }

    public static void runOnUiThreadOrElse(Fragment fragment, Runnable runnable, Runnable elseRunnable) {
        if (fragment.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            final var activity = fragment.getActivity();
            runOnUiThreadOrElse(activity, runnable, elseRunnable);
        } else {
            if (elseRunnable != null) {
                elseRunnable.run();
            }
        }
    }

    public static void runOnUiThread(FragmentActivity activity, Runnable runnable) {
        runOnUiThreadOrElse(activity, runnable, null);
    }

    public static void runOnUiThreadOrElse(FragmentActivity activity, Runnable runnable, Runnable elseRunnable) {
        if (activity == null) {
            throw new NullPointerException("Activity must not be null.");
        }

        if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            activity.runOnUiThread(runnable);
        } else {
            if (elseRunnable != null) {
                elseRunnable.run();
            }
        }
    }
}
