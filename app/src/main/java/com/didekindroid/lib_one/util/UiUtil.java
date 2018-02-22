package com.didekindroid.lib_one.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static java.text.DateFormat.MEDIUM;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Locale.getDefault;

/**
 * User: pedro
 * Date: 04/07/15
 * Time: 17:36
 */
@SuppressWarnings("WeakerAccess")
public final class UiUtil {

    public static final Locale SPAIN_LOCALE = new Locale("es", "ES");

    public static final int APPBAR_ID = R.id.appbar;

    private UiUtil()
    {
    }

//    ========================== ACTIVITIES ======================================

    public static boolean checkInternet(Activity activity)
    {
        if (!ConnectionUtils.isInternetConnected(activity)) {
            makeToast(activity, R.string.no_internet_conn_toast);
            return false;
        }
        return true;
    }

    public static boolean checkPostExecute(Activity activity)
    {
        if (activity == null || activity.isDestroyed() || activity.isChangingConfigurations()) {
            Timber.i("onPostExcecute(): activity is already destroyed");
            return true;
        }
        return false;
    }

    public static View getContetViewInAc(Activity activity)
    {
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    //    ================================== ASSERTIONS ======================================

    public static void assertTrue(boolean assertion, String message)
    {
        if (!assertion) {
            throw new AssertionError(message);
        }
    }

    //    ================================== CONTROLLERS ======================================

    public static int destroySubscriptions(CompositeDisposable subscriptions)
    {
        Timber.d("destroySubscriptions()");

        assertTrue(subscriptions != null, CommonAssertionMsg.subscriptions_should_not_be_null);
        subscriptions.clear();
        assertTrue(subscriptions.size() == 0, CommonAssertionMsg.subscriptions_should_be_zero);
        return subscriptions.size();
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

    public static String formatDoubleZeroDecimal(Double myDouble)
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

    //    ================================ DATES ==========================================

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isCalendarPreviousTimeStamp(Calendar calendar, Timestamp timestamp)
    {
        Calendar timeStampCalendar = Calendar.getInstance();
        timeStampCalendar.setTimeInMillis(timestamp.getTime());

        if (calendar.get(YEAR) < timeStampCalendar.get(YEAR)) {
            return true;
        }
        if (calendar.get(YEAR) > timeStampCalendar.get(YEAR)) {
            return false;
        }
        if (calendar.get(MONTH) < timeStampCalendar.get(MONTH)) {
            return true;
        }
        if (calendar.get(MONTH) > timeStampCalendar.get(MONTH)) {
            return false;
        }
        return calendar.get(DAY_OF_MONTH) < timeStampCalendar.get(DAY_OF_MONTH);
    }

    public static Calendar getCalendarFromTimeMillis(long time)
    {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(time);
        return timeCalendar;
    }

    //    ================================== EXCEPTIONS ===================================

    public static StringBuilder getErrorMsgBuilder(Context context)
    {
        return new StringBuilder(context.getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());
    }

    @NonNull
    public static UiException getUiExceptionFromThrowable(Throwable e)
    {
        UiException ui;
        if (e instanceof UiException) {
            Timber.d("UiException message: %s", ((UiException) e).getErrorBean().getMessage());
            ui = (UiException) e;
        } else {
            ui = new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
        return ui;
    }

    //    ================================== TOASTS ======================================

    public static void makeToast(Context context, int resourceStringId)
    {
        makeToastIntern(context, context.getString(resourceStringId), R.color.deep_purple_100);
    }

    public static void makeToast(Context context, CharSequence toastMessage)
    {
        makeToastIntern(context, toastMessage, R.color.deep_purple_100);
    }

    private static void makeToastIntern(Context context, CharSequence toastMessage, int toastBackColor)
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

    public static ActionBar doToolBar(AppCompatActivity activity, int resourceIdView, boolean hasParentAc)
    {

        Timber.d("doToolBar()");

        Toolbar myToolbar = activity.findViewById(resourceIdView);
        activity.setSupportActionBar(myToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            if (hasParentAc) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            /*actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);*/
        }
        return actionBar;
    }

    public static ActionBar doToolBar(AppCompatActivity activity, boolean hasParentAc)
    {
        Timber.d("doToolBar()");
        return doToolBar(activity, APPBAR_ID, hasParentAc);
    }
}
