package it.niedermann.nextcloud.deck.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.helper.VersionCheckHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class ExceptionUtil {

    private ExceptionUtil() {

    }

    public static String getDebugInfos(Context context, Throwable throwable) {
        List<Throwable> throwables = new ArrayList<>();
        throwables.add(throwable);
        return getDebugInfos(context, throwables);
    }

    public static String getDebugInfos(Context context, List<Throwable> throwables) {
        StringBuilder debugInfos = new StringBuilder(""
                + getAppVersions(context)
                + "\n\n---\n"
                + getDeviceInfos()
                + "\n\n---"
                + "\n\n");
        for (Throwable throwable : throwables) {
            debugInfos.append(getStacktraceOf(throwable));
        }
        return debugInfos.toString();
    }

    private static String getAppVersions(Context context) {
        String versions = ""
                + "App Version: " + BuildConfig.VERSION_NAME + "\n"
                + "App Version Code: " + BuildConfig.VERSION_CODE + "\n"
                + "App Flavor: " + BuildConfig.FLAVOR + "\n";

        try {
            versions += "\nFiles App Version Code: " + VersionCheckHelper.getNextcloudFilesVersionCode(context);
        } catch (PackageManager.NameNotFoundException e) {
            versions += "\nFiles App Version Code: " + e.getMessage();
            e.printStackTrace();
        }
        return versions;
    }

    private static String getDeviceInfos() {
        return ""
                + "\nOS Version: " + System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")"
                + "\nOS API Level: " + Build.VERSION.SDK_INT
                + "\nDevice: " + Build.DEVICE
                + "\nManufacturer: " + Build.MANUFACTURER
                + "\nModel (and Product): " + Build.MODEL + " (" + Build.PRODUCT + ")";
    }

    private static String getStacktraceOf(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static void handleHttpRequestFailedException(NextcloudHttpRequestFailedException exception, View targetView, Activity activity) {
        final String debugInfos = ExceptionUtil.getDebugInfos(activity, exception);
        switch (exception.getStatusCode()) {
            case 302: {
                Snackbar.make(targetView, R.string.server_misconfigured, Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> {
                            AlertDialog dialog = new BrandedAlertDialogBuilder(activity)
                                    .setTitle(R.string.server_misconfigured)
                                    .setMessage(activity.getString(R.string.server_misconfigured_explanation) + "\n\n\n" + debugInfos)
                                    .setPositiveButton(android.R.string.copy, (a, b) -> {
                                        copyToClipboard(activity, activity.getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                                        a.dismiss();
                                    })
                                    .setNegativeButton(R.string.simple_close, null)
                                    .create();
                            dialog.show();
                            ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(Typeface.MONOSPACE);
                        })
                        .show();
                break;
            }
            case 503: {
                // Handled by maintenance info box
                break;
            }
            default: {
                Snackbar.make(targetView, R.string.error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> {
                            AlertDialog dialog = new BrandedAlertDialogBuilder(activity)
                                    .setTitle(R.string.server_error)
                                    .setMessage(debugInfos)
                                    .setPositiveButton(android.R.string.copy, (a, b) -> {
                                        copyToClipboard(activity, activity.getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                                        a.dismiss();
                                    })
                                    .setNegativeButton(R.string.simple_close, null)
                                    .create();
                            dialog.show();
                            ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(Typeface.MONOSPACE);
                        })
                        .show();
                break;
            }
        }

    }
}
