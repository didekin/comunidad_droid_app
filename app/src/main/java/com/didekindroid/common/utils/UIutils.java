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

import com.didekin.common.exception.InServiceException;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;

import static android.widget.Toast.makeText;
import static com.didekin.common.domain.DataPatterns.DECIMAL_TWO;
import static com.didekin.common.domain.DataPatterns.DECIMAL_ZERO;
import static com.didekin.common.domain.DataPatterns.LINE_BREAK;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.isMessageToLogin;
import static com.didekin.common.exception.DidekinExceptionMsg.isMessageToSeeIncidencia;
import static com.didekindroid.R.color.deep_purple_100;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.UiException.UiAction.INCID_SEE_BY_COMU;
import static com.didekindroid.common.UiException.UiAction.LOGIN;
import static com.didekindroid.common.UiException.UiAction.SEARCH_COMU;
import static com.didekindroid.common.utils.UIutils.SharedPrefFiles.app_preferences_file;

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

//    ===========================  AUTHENTICATION ==============================

    public static String checkBearerToken() throws UiException
    {
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();

        if (bearerAccessTkHeader == null) { // No token in cache.
            throw new UiException(LOGIN, R.string.user_without_signedUp, null);
        }
        return bearerAccessTkHeader;
    }

//    ================================ DATA FORMATS ==========================================

    public static String formatTimeStampToString(Timestamp timestamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return String.format(DidekindroidApp.getContext().getResources().getString(R.string.date_local_format), day, month, year);
    }

    public static String formatDoubleZeroDecimal(Double myDouble)
    {
        DecimalFormat myFormatter = new DecimalFormat(DECIMAL_ZERO.getRegexp());
        return myFormatter.format(myDouble);
    }

    public static String formatDoubleTwoDecimals(Double myDouble)
    {
        DecimalFormat myFormatter = new DecimalFormat(DECIMAL_TWO.getRegexp());
        return myFormatter.format(myDouble);
    }

//    ================================== ERRORS ======================================

    public static StringBuilder getErrorMsgBuilder(Context context)
    {
        return new StringBuilder(context.getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());
    }

//    ============================== EXCEPTIONS =======================================

    public static void catchAuthenticationException(InServiceException e, String tagClass, int resourceId) throws UiException
    {
        Log.e(tagClass, e.getHttpMessage());
        if (isMessageToLogin(e.getHttpMessage())) {  // Problema de identificación.
            throw new UiException(LOGIN, resourceId, e);
        }
    }

    public static void catchComunidadFkException(InServiceException ie, String tagClass) throws UiException
    {
        Log.e(tagClass, ie.getHttpMessage());
        if (ie.getHttpMessage().equals(COMUNIDAD_NOT_FOUND.getHttpMessage())){
            throw new UiException(SEARCH_COMU, R.string.comunidad_not_found_message, null);
        }
    }

    public static void catchIncidenciaFkException(InServiceException ie, String tagClass) throws UiException
    {
        Log.e(tagClass, ie.getHttpMessage());
        if (isMessageToSeeIncidencia(ie.getHttpMessage())){
            throw new UiException(INCID_SEE_BY_COMU, R.string.incidencia_wrong_init, ie);
        }
    }

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
