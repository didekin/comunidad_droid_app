package com.didekindroid.uiutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.LoginAc;

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

    public static void callLogin(Activity activity)
    {
        Log.d(TAG,"callLogin()");
        Intent intent = new Intent(activity, LoginAc.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static StringBuilder getErrorMsgBuilder(Context context)
    {
        return new StringBuilder(context.getResources().getText(R.string.error_validation_msg))
                .append(CommonPatterns.LINE_BREAK.literal);
    }

    public static boolean isRegisteredUser(Context context)
    {
        Log.d(TAG, "isRegisteredUser()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (SharedPreferencesFiles.USER_PREF.toString(), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SharedPreferencesKeys.IS_USER_REG.toString(), false);
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