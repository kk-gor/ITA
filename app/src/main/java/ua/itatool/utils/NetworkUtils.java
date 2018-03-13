package ua.itatool.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by djdf.crash on 11.03.2018.
 */

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
