package com.didekindroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.didekin.serviceone.domain.DataPatterns;
import com.didekindroid.R;

import static android.widget.Toast.makeText;
import static com.didekindroid.R.color.deep_purple_100;

/**
 * User: pedro
 * Date: 04/07/15
 * Time: 17:36
 */
public final class UIutils {

    private static final String TAG = UIutils.class.getCanonicalName();
    public static final int APPBAR_ID = R.id.appbar;

    private UIutils()
    {
    }

    public static void doRuntimeException(Exception e, String tagClass)
    {
        Log.e(tagClass, e.getMessage());
        throw new RuntimeException(e);
    }

    public static void doToolBar(AppCompatActivity activity, int resourceIdView, boolean hasParentAc)
    {

        Log.d(TAG, "doToolBar()");

        Toolbar myToolbar = (Toolbar) activity.findViewById(resourceIdView);
        activity.setSupportActionBar(myToolbar);
        if (hasParentAc) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

/*
    When inflating anything to be displayed on the action bar (such as a SpinnerAdapter for
    list navigation in the toolbar), make sure you use the action bar’s themed context, retrieved
    via getSupportActionBar().getThemedContext().
    You must use the static methods in MenuItemCompat for any action-related calls on a MenuItem.
*/

    public static void doToolBar(AppCompatActivity activity, boolean hasParentAc)
    {
        Log.d(TAG, "doToolBar()");
        doToolBar(activity, APPBAR_ID, hasParentAc);
    }

    public static StringBuilder getErrorMsgBuilder(Context context)
    {
        return new StringBuilder(context.getResources().getText(R.string.error_validation_msg))
                .append(DataPatterns.LINE_BREAK.getRegexp());
    }

    public static boolean isRegisteredUser(Context context)
    {
        Log.d(TAG, "isRegisteredUser()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (SharedPrefFiles.USER_PREF.toString(), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SharedPrefFiles.IS_USER_REG, false);
    }

    public static void makeToast(Context context, int resourceStringId, int toastLength)
    {
        makeToast(context, context.getResources().getText(resourceStringId), toastLength);
        /*makeToast(context, context.getResources().getText(resourceStringId), Toast.LENGTH_LONG);*/
    }

    @SuppressWarnings("deprecation")
    public static void makeToast(Context context, CharSequence toastMessage, int toastLength)
    {
        Toast clickToast = makeText(context, toastMessage, toastLength);
        View toastView = clickToast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(deep_purple_100));
        TextView textView = new TextView(context);
        textView.setTextSize(context.getResources().getDimension(R.dimen.text_decription_widget));
        textView.setText(toastMessage);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        ((ViewGroup) toastView).removeAllViews();
        ((ViewGroup) toastView).addView(textView, ViewGroup.LayoutParams.WRAP_CONTENT);
        clickToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        clickToast.show();
    }

    public static void updateIsRegistered(boolean isRegisteredUser, Context context)
    {
        Log.d(TAG, "updateIsRegistered()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (SharedPrefFiles.USER_PREF.toString(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SharedPrefFiles.IS_USER_REG, isRegisteredUser);
        editor.apply();
    }

//  ..............  INNER CLASSES ............

    enum SharedPrefFiles {

        USER_PREF,;

        private static final String IS_USER_REG = "isRegisteredUser";

        @Override
        public String toString()
        {
            return getClass().getCanonicalName().concat(".").concat(this.name());
        }
    }
}
