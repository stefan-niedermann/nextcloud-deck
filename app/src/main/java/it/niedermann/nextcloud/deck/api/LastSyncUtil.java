package it.niedermann.nextcloud.deck.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import it.niedermann.nextcloud.deck.R;

public class LastSyncUtil {

    private static final String LAST_SYNC_KEY = "lS_";
    private static LastSyncUtil INSTANCE;
    private SharedPreferences lastSyncPref;


    private LastSyncUtil(Context applicationContext) {
        lastSyncPref = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.shared_preference_last_sync),
                Context.MODE_PRIVATE
        );

    }

    public static long getLastSync(long accountId){
        return INSTANCE.lastSyncPref.getLong(getSyncKeyForAccount(accountId), 0L);
    }

    public static Date getLastSyncDate(long accountId){
        return new Date(getLastSync(accountId));
    }

    public static void setLastSyncDate(long accountId, Date value){
        INSTANCE.lastSyncPref.edit().putLong(getSyncKeyForAccount(accountId), value.getTime()).apply();
    }

    public static void resetLastSyncDate(long accountId){
        INSTANCE.lastSyncPref.edit().remove(getSyncKeyForAccount(accountId)).apply();
    }

    public static void init(Context applicationContext) {
        INSTANCE = new LastSyncUtil(applicationContext);
    }

    private static String getSyncKeyForAccount(long accountId) {
        return LAST_SYNC_KEY + accountId;
    }

    public static void resetAll() {
        INSTANCE.lastSyncPref.edit().clear().apply();
    }
}
