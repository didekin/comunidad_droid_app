package com.didekindroid.testutil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.DatePicker;

import com.didekindroid.R;

import org.hamcrest.CoreMatchers;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 18:51
 */

public final class ActivityTestUtils {

    private ActivityTestUtils()
    {
    }

    //    ============================= DATES ===================================

    public static Timestamp doTimeStampFromCalendar(int daysToAdd)
    {
        Calendar fCierre = new GregorianCalendar();
        fCierre.add(DAY_OF_MONTH, daysToAdd);
        return new Timestamp(fCierre.getTimeInMillis());
    }


    //    ============================= DATE PICKERS ===================================

    public static Calendar reSetDatePicker(long fechaInicial, int monthsToAdd)
    {
        Calendar newCalendar = new GregorianCalendar();
        if (fechaInicial > 0L) {
            newCalendar.setTimeInMillis(fechaInicial);
        }
        // Aumentamos la fecha estimada en un número de meses.
        newCalendar.add(MONTH, monthsToAdd);
        // Android PickerActions substract 1 from the month passed to setDate(), so we increased the month parameter value in 1 before passing it.
        onView(withClassName(CoreMatchers.is(DatePicker.class.getName())))
                .perform(PickerActions.setDate(newCalendar.get(Calendar.YEAR), newCalendar.get(MONTH) + 1, newCalendar.get(DAY_OF_MONTH)));
        return newCalendar;
    }

    public static void closeDatePicker(Context context)
    {
        if (SDK_INT == KITKAT) {
            onView(withId(android.R.id.button1)).perform(click());
        }
        if (SDK_INT > KITKAT) {
            onView(withText(context.getString(android.R.string.ok))).perform(click());
        }
    }

    //    ============================= NAVIGATION ===================================

    public static void clickNavigateUp()
    {
        onView(CoreMatchers.allOf(
                ViewMatchers.withContentDescription(R.string.navigate_up_txt),
                isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    public static void checkUp(Integer... activityLayoutIds)
    {
        clickNavigateUp();
        for (Integer layout : activityLayoutIds) {
            onView(withId(layout)).check(matches(isDisplayed()));
        }
    }

    public static void checkBack(ViewInteraction viewInteraction, Integer... activityLayoutIds)
    {
        viewInteraction.perform(closeSoftKeyboard()).perform(pressBack());
        for (Integer layout : activityLayoutIds) {
            onView(withId(layout)).check(matches(isDisplayed()));
        }
    }

    //    ============================ TOASTS ============================

    public static void checkToastInTest(int resourceId, Activity activity, int... resourceFieldsErrorId)
    {
        Resources resources = activity.getResources();

        ViewInteraction toast = onView(
                withText(containsString(resources.getText(resourceId).toString())))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        if (resourceFieldsErrorId != null) {
            for (int field : resourceFieldsErrorId) {
                toast.check(matches(withText(containsString(resources.getText(field).toString()))));
            }
        }
    }

    public static void checkNoToastInTest(int resourceStringId, Activity activity)
    {
        Resources resources = activity.getResources();

        onView(
                withText(containsString(resources.getText(resourceStringId).toString())))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(doesNotExist());
    }
}
