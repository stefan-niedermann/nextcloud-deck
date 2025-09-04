package it.niedermann.nextcloud.deck.remote.helpers.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import it.niedermann.nextcloud.deck.R;

public class ConnectivityUtil {

    private final ConnectivityManager connectivityManager;
    private final String prefKeyWifiOnly;
    private final SharedPreferences sharedPreferences;

    public ConnectivityUtil(@NonNull Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.prefKeyWifiOnly = context.getString(R.string.pref_key_wifi_only);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean hasInternetConnection() {
        if (connectivityManager != null) {
            if (sharedPreferences.getBoolean(prefKeyWifiOnly, false)) {
                final var networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo == null) {
                    return false;
                }
                return networkInfo.isConnected();
            } else {
                return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
            }
        }
        return false;
    }
}
