package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.helper.VersionCheckHelper;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.DeckLog;
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

    @UiThread
    public static void handleNextcloudFilesAppNotInstalledException(@NonNull Context context, @NonNull NextcloudFilesAppNotInstalledException exception) {
        UiExceptionManager.showDialogForException(context, exception);
        DeckLog.warn("=============================================================");
        DeckLog.warn("Nextcloud app is not installed. Cannot choose account");
        exception.printStackTrace();
    }

    @UiThread
    public static void handleHttpRequestFailedException(@NonNull NextcloudHttpRequestFailedException exception, @NonNull View targetView, @NonNull Context context) {
        final String debugInfos = ExceptionUtil.getDebugInfos(context, exception);
        switch (exception.getStatusCode()) {
            case 302: {
                Snackbar.make(targetView, R.string.server_misconfigured, Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> {
                            AlertDialog dialog = new BrandedAlertDialogBuilder(context)
                                    .setTitle(R.string.server_misconfigured)
                                    .setMessage(context.getString(R.string.server_misconfigured_explanation) + "\n\n\n" + debugInfos)
                                    .setPositiveButton(android.R.string.copy, (a, b) -> {
                                        copyToClipboard(context, context.getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                                        a.dismiss();
                                    })
                                    .setNeutralButton(R.string.simple_close, null)
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
                            AlertDialog dialog = new BrandedAlertDialogBuilder(context)
                                    .setTitle(R.string.server_error)
                                    .setMessage(debugInfos)
                                    .setPositiveButton(android.R.string.copy, (a, b) -> {
                                        copyToClipboard(context, context.getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                                        a.dismiss();
                                    })
                                    .setNeutralButton(R.string.simple_close, null)
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
