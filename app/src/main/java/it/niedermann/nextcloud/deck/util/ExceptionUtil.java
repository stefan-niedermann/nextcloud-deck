package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.helper.VersionCheckHelper;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;

public class ExceptionUtil {

    private ExceptionUtil() {

    }

    public static String getDebugInfos(@NonNull Context context, Throwable throwable, @Nullable Account account) {
        List<Throwable> throwables = new ArrayList<>();
        throwables.add(throwable);
        return getDebugInfos(context, throwables, account);
    }

    public static String getDebugInfos(@NonNull Context context, @NonNull List<Throwable> throwables, @Nullable Account account) {
        StringBuilder debugInfos = new StringBuilder(""
                + getAppVersions(context, account)
                + "\n\n---\n"
                + getDeviceInfos()
                + "\n\n---"
                + "\n\n");
        for (Throwable throwable : throwables) {
            debugInfos.append(getStacktraceOf(throwable));
        }
        return debugInfos.toString();
    }

    private static String getAppVersions(Context context, @Nullable Account account) {
        String versions = ""
                + "App Version: " + BuildConfig.VERSION_NAME + "\n"
                + "App Version Code: " + BuildConfig.VERSION_CODE + "\n"
                + "App Flavor: " + BuildConfig.FLAVOR + "\n";

        if (account != null) {
            versions += "\n";
            versions += "Server Version: " + account.getServerDeckVersion() + "\n";
        }

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
}
