package it.niedermann.nextcloud.deck.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.helper.VersionCheckHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ExceptionUtil {

    private ExceptionUtil() {

    }

    public static String getDebugInfos(Activity activity, Throwable throwable) {
        List<Throwable> throwables = new ArrayList<>();
        throwables.add(throwable);
        return getDebugInfos(activity, throwables);
    }

    public static String getDebugInfos(Activity activity, List<Throwable> throwables) {
        StringBuilder debugInfos = new StringBuilder(""
                + getAppVersions(activity)
                + "\n\n---\n"
                + getDeviceInfos()
                + "\n\n---"
                + "\n\n");
        for (Throwable throwable : throwables) {
            debugInfos.append(getStacktraceOf(throwable));
        }
        return debugInfos.toString();
    }

    private static String getAppVersions(Activity activity) {
        String versions = "";
        try {
            PackageInfo pInfo = activity.getApplicationContext().getPackageManager().getPackageInfo(activity.getApplicationContext().getPackageName(), 0);
            versions += "App Version: " + pInfo.versionName;
            versions += "\nApp Version Code: " + pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versions += "\nApp Version: " + e.getMessage();
            e.printStackTrace();
        }

        try {
            versions += "\nFiles App Version Code: " + VersionCheckHelper.getNextcloudFilesVersionCode(activity);
        } catch (PackageManager.NameNotFoundException e) {
            versions += "\nFiles App Version Code: " + e.getMessage();
            e.printStackTrace();
        }
        return versions;
    }

    private static String getDeviceInfos() {
        return ""
                + "\nOS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")"
                + "\nOS API Level: " + android.os.Build.VERSION.SDK_INT
                + "\nDevice: " + android.os.Build.DEVICE
                + "\nModel (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
    }

    private static String getStacktraceOf(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static void handleHttpRequestFailedException(NextcloudHttpRequestFailedException exception, View targetView, Activity activity) {
        switch (exception.getStatusCode()) {
            case 302: {
                Snackbar.make(targetView, R.string.server_misconfigured, Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> {
                            String debugInfos = ExceptionUtil.getDebugInfos(activity, exception);
                            AlertDialog dialog = new AlertDialog.Builder(activity)
                                    .setTitle(R.string.server_misconfigured)
                                    .setMessage(activity.getString(R.string.server_misconfigured_explanation) + debugInfos)
                                    .setPositiveButton(android.R.string.copy, (a, b) -> {
                                        final ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText(activity.getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                                        Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
                                        Toast.makeText(activity.getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
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
                Snackbar.make(targetView, R.string.server_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> {
                            String debugInfos = ExceptionUtil.getDebugInfos(activity, exception);
                            AlertDialog dialog = new AlertDialog.Builder(activity)
                                    .setTitle(R.string.server_error)
                                    .setMessage(activity.getString(R.string.server_error_explanation) + debugInfos)
                                    .setPositiveButton(android.R.string.copy, (a, b) -> {
                                        final ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText(activity.getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                                        Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
                                        Toast.makeText(activity.getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                                        a.dismiss();
                                    })
                                    .setNegativeButton(R.string.simple_close, null)
                                    .create();
                            dialog.show();
                            ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(Typeface.MONOSPACE);
                        })
                        .show();
            }
        }

    }
}
