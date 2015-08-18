package com.didekindroid.common.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * User: pedro
 * Date: 04/07/15
 * Time: 17:36
 */
public final class UIutils {

    private static final String TAG = UIutils.class.getCanonicalName();

    private UIutils()
    {
    }

    public static void makeToast(Context context, int resourceStringId)
    {
        Toast clickToast = new Toast(context).makeText(context, resourceStringId, Toast.LENGTH_LONG);
        clickToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        clickToast.show();
    }

    public static void makeToast(Context context, String toastMessage)
    {
        Toast clickToast = new Toast(context).makeText(context, toastMessage, Toast.LENGTH_LONG);
        clickToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        clickToast.show();
    }


    public static void updateIsRegistered(boolean isRegisteredUser, Context context)
    {
        Log.d(TAG, "updateIsRegistered()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (SharedPreferencesFiles.USER_PREF.toString(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SharedPreferencesKeys.IS_USER_REG.toString(), isRegisteredUser);
        editor.apply();

    }

    public static boolean isRegisteredUser(Activity activity)
    {
        Log.d(TAG, "isRegisteredUser()");

        SharedPreferences sharedPref = activity.getSharedPreferences
                (SharedPreferencesFiles.USER_PREF.toString(), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SharedPreferencesKeys.IS_USER_REG.toString(),false);
    }
//  ..............  INNER CLASSES ............

    private enum SharedPreferencesFiles {
        USER_PREF,;


        @Override
        public String toString()
        {
            return getClass().getCanonicalName().concat(this.name());
        }
    }

    private enum SharedPreferencesKeys {

        IS_USER_REG(SharedPreferencesFiles.USER_PREF),;

        private final SharedPreferencesFiles preferencesFile;

        SharedPreferencesKeys(SharedPreferencesFiles preferencesFile)
        {
            this.preferencesFile = preferencesFile;
        }
    }
}
