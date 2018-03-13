package ua.itatool;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by djdf.crash on 10.03.2018.
 */

public class App extends Application {
    private static SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        if (mSharedPreferences == null){
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }
        super.onCreate();
    }

    public static SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
