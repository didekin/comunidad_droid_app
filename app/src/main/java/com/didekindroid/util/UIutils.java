package com.didekindroid.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.didekin.http.ErrorBean;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.didekin.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekin.http.GenericExceptionMsg.TOKEN_NULL;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static java.text.DateFormat.MEDIUM;
import static java.util.Locale.getDefault;

/**
 * User: pedro
 * Date: 04/07/15
 * Time: 17:36
 */
@SuppressWarnings("WeakerAccess")
public final class UIutils {

    public static final Locale SPAIN_LOCALE = new Locale("es", "ES");
    private static final int APPBAR_ID = R.id.appbar;

    /* ASSERTION MESSAGES*/
    public static final String fragment_should_be_initialized = "Fragment should be initialized";
    public static final String bean_fromView_should_be_initialized = "Bean with view data should be initialized";
    public static final String intent_extra_should_be_initialized = "Intent extra should be initialized";
    public static final String cursor_should_be_closed = "Database cursor should be closed";

    private UIutils()
    {
    }

//    ========================== ACTIVITIES ======================================

    public static boolean checkInternet(Activity activity)
    {
        if (!ConnectionUtils.isInternetConnected(activity)) {
            makeToast(activity, R.string.no_internet_conn_toast);
            return true;
        }
        return false;
    }

    public static boolean checkPostExecute(Activity activity)
    {
        if (activity.isDestroyed() || activity.isChangingConfigurations()) {
            Timber.i("onPostExcecute(): activity is already destroyed");
            return true;
        }
        return false;
    }

    public static void closeCursor(Adapter adapter)
    {
        CursorAdapter cursorAdapter;
        Cursor cursor;
        if (adapter != null) {
            try {
                cursorAdapter = (CursorAdapter) adapter;
                cursor = cursorAdapter.getCursor();
                if (cursor != null) {
                    cursor.close();
                    assertTrue(cursor.isClosed(), cursor_should_be_closed );
                }
            } catch (ClassCastException e) {
                throw new IllegalStateException("Illegal NON cursorAdapter", e);
            }
        }
    }

//    ===========================  AUTHENTICATION ==============================

    public static String checkBearerToken() throws UiException
    {
        String bearerAccessTkHeader = TKhandler.doHttpAuthHeaderFromTkInCache();

        if (bearerAccessTkHeader == null) { // No token in cache.
            ErrorBean errorBean = new ErrorBean(TOKEN_NULL.getHttpMessage(), TOKEN_NULL.getHttpStatus());
            throw new UiException(errorBean);
        }
        return bearerAccessTkHeader;
    }

//    ================================ DATA FORMATS ==========================================

    public static String formatTimeStampToString(Timestamp timestamp)
    {
        return DateFormat.getDateInstance(MEDIUM, getDefault()).format(timestamp);
    }

    public static String formatTimeToString(long time)
    {
        return DateFormat.getDateInstance(MEDIUM, getDefault()).format(new Date(time));
    }

    public static String formatDoubleZeroDecimal(Double myDouble, Context context)
    {
        NumberFormat myFormatter = NumberFormat.getInstance(SPAIN_LOCALE);
        return myFormatter.format(myDouble);
    }

    @SuppressWarnings("deprecation")
    public static String formatDoubleTwoDecimals(Double myDouble, Context context)
    {
        Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        NumberFormat myFormatter = NumberFormat.getInstance(locale);
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

    public static void assertTrue(boolean assertion, String message)
    {
        if (!assertion) {
            throw new AssertionError(message);
        }
    }

    public static StringBuilder getErrorMsgBuilder(Context context)
    {
        return new StringBuilder(context.getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());
    }

    //    ================================== TOASTS ======================================

    public static void makeToast(Context context, int resourceStringId)
    {
        makeToast(context, context.getResources().getText(resourceStringId), R.color.deep_purple_100);
    }

    public static void makeToast(Context context, CharSequence toastMessage, int toastBackColor)
    {
        Toast clickToast = makeText(context, null, LENGTH_SHORT);
        View toastView = clickToast.getView();
        toastView.setBackgroundColor(ContextCompat.getColor(context, toastBackColor));
        TextView textView = new TextView(context);
        textView.setTextSize(context.getResources().getDimension(R.dimen.toast_text_size));
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

        Timber.d("doToolBar()");

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

    public static void doToolBar(AppCompatActivity activity, boolean hasParentAc)
    {
        Timber.d("doToolBar()");
        doToolBar(activity, APPBAR_ID, hasParentAc);
    }

/*
    When inflating anything to be displayed on the action bar (such as a SpinnerAdapter for
    list navigation in the toolbar), make sure you use the action bar’s themed context, retrieved
    via getSupportActionBar().getThemedContext().
    You must use the static methods in MenuItemCompat for any action-related calls on a MenuItem.
*/

    public ActivityManager getActivityManager(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getSystemService(ActivityManager.class);
        }
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }
}
