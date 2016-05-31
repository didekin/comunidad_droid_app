package com.didekindroid.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.didekin.common.exception.ErrorBean;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static android.widget.Toast.makeText;
import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;
import static com.didekin.common.exception.DidekinExceptionMsg.TOKEN_NULL;
import static com.didekindroid.R.color.deep_purple_100;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.SharedPrefFiles.app_preferences_file;
import static java.text.DateFormat.MEDIUM;
import static java.util.Locale.getDefault;

/**
 * User: pedro
 * Date: 04/07/15
 * Time: 17:36
 */
public final class UIutils {

    private static final String TAG = UIutils.class.getCanonicalName();
    public static final int APPBAR_ID = R.id.appbar;
    public static final Locale SPAIN_LOCALE = new Locale("es", "ES");

    private UIutils()
    {
    }

//    ===========================  AUTHENTICATION ==============================

    public static String checkBearerToken() throws UiException
    {
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();

        if (bearerAccessTkHeader == null) { // No token in cache.
            ErrorBean errorBean = new ErrorBean(TOKEN_NULL.getHttpMessage(), TOKEN_NULL.getHttpStatus());
            throw new UiException(errorBean);
        }
        return bearerAccessTkHeader;
    }

//    ================================ DATA FORMATS ==========================================

    public static String formatTimeStampToString(Timestamp timestamp)   // TEstar.
    {
        return DateFormat.getDateInstance(MEDIUM, getDefault()).format(timestamp);
    }

    public static String formatTimeToString(long time)
    {
        return DateFormat.getDateInstance(MEDIUM, getDefault()).format(new Date(time));
    }

    public static String formatDoubleZeroDecimal(Double myDouble, Context context)
    {
        DecimalFormat myFormatter = new DecimalFormat(context.getString(R.string.decimal_zero_regexp));
        return myFormatter.format(myDouble);
    }

    public static String formatDoubleTwoDecimals(Double myDouble, Context context)
    {
        DecimalFormat myFormatter = new DecimalFormat(context.getString(R.string.decimal_two_regexp));
        return myFormatter.format(myDouble);
    }

    public static int getIntFromStringDecimal(String stringDecimal) throws ParseException
    {
        return NumberFormat.getIntegerInstance().parse(stringDecimal).intValue();
    }

    public static String getStringFromInteger(int number)
    {
        return NumberFormat.getIntegerInstance().format(number);
    }

    //    ================================== ERRORS ======================================

    public static StringBuilder getErrorMsgBuilder(Context context)
    {
        return new StringBuilder(context.getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());
    }

//    ============================== EXCEPTIONS =======================================

    public static void doRuntimeException(Exception e, String tagClass)
    {
        Log.e(tagClass, e.getMessage());
        throw new RuntimeException(e);
    }

//    =============================== GOOGLE SERVICES =================================

    /**
     * Check the availability of Google Play Services.
     */
    public static boolean checkPlayServices(Context context)
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

//    ================================== SHARED PREFERENCES ======================================

    public static boolean isRegisteredUser(Context context)
    {
        Log.d(TAG, "isRegisteredUser()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (app_preferences_file.toString(), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SharedPrefFiles.IS_USER_REG, false);
    }

    public static void updateIsRegistered(boolean isRegisteredUser, Context context)
    {
        Log.d(TAG, "updateIsRegistered()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (app_preferences_file.toString(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SharedPrefFiles.IS_USER_REG, isRegisteredUser);
        editor.apply();
    }

    public static boolean isGcmTokenSentServer(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SharedPrefFiles.IS_GCM_TOKEN_SENT_TO_SERVER, false);
    }

    public static void updateIsGcmTokenSentServer(boolean isSentToServer, Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SharedPrefFiles.IS_GCM_TOKEN_SENT_TO_SERVER, isSentToServer);
        editor.apply();
        Log.d(TAG, "updateIsGcmTokenSentServer(), exit. iSentToServer= " + isSentToServer);
    }

    //  ..............  INNER CLASSES ............

    public enum SharedPrefFiles {

        app_preferences_file,;

        private static final String IS_USER_REG = "isRegisteredUser";
        private static final String IS_GCM_TOKEN_SENT_TO_SERVER = "isGcmTokenSentToServer";

        @Override
        public String toString()
        {
            return getClass().getCanonicalName().concat(".").concat(this.name());
        }
    }

//    ================================== TOASTS ======================================

    public static void makeToast(Context context, int resourceStringId, int toastLength)
    {
        makeToast(context, context.getResources().getText(resourceStringId), toastLength);
        /*makeToast(context, context.getResources().getText(resourceStringId), Toast.LENGTH_LONG);*/
    }

    public static void makeToast(Context context, CharSequence toastMessage, int toastLength)
    {
        Toast clickToast = makeText(context, null, toastLength);
        View toastView = clickToast.getView();
        toastView.setBackgroundColor(ContextCompat.getColor(context, deep_purple_100));
        TextView textView = new TextView(context);
        textView.setTextSize(context.getResources().getDimension(R.dimen.text_decription_widget));
        textView.setText(toastMessage);
        textView.setTextColor(ContextCompat.getColor(context, R.color.black));
        ((ViewGroup) toastView).removeAllViews();
        ((ViewGroup) toastView).addView(textView, ViewGroup.LayoutParams.WRAP_CONTENT);
        clickToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        clickToast.show();
    }

//    ================================== TOOL BAR ======================================

    public static void doToolBar(AppCompatActivity activity, int resourceIdView, boolean hasParentAc)
    {

        Log.d(TAG, "doToolBar()");

        Toolbar myToolbar = (Toolbar) activity.findViewById(resourceIdView);
        activity.setSupportActionBar(myToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            if (hasParentAc) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            /*actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);*/
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
}
