package com.didekinaar.testutil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.DatePicker;


import com.didekinaar.R;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 18:51
 */

public final class AarActivityTestUtils {

    private AarActivityTestUtils()
    {
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
        Espresso.onView(withClassName(CoreMatchers.is(DatePicker.class.getName())))
                .perform(PickerActions.setDate(newCalendar.get(Calendar.YEAR), newCalendar.get(MONTH) + 1, newCalendar.get(DAY_OF_MONTH)));
        return newCalendar;
    }

    public static void closeDatePicker(Context context)
    {
        if (SDK_INT == KITKAT) {
            Espresso.onView(withId(android.R.id.button1)).perform(ViewActions.click());
        }
        if (SDK_INT > KITKAT) {
            Espresso.onView(withText(context.getString(android.R.string.ok))).perform(ViewActions.click());
        }
    }

    //    ============================= NAVIGATION ===================================

    public static void clickNavigateUp()
    {
        Espresso.onView(CoreMatchers.allOf(
                ViewMatchers.withContentDescription(R.string.navigate_up_txt),
                isClickable())
        ).check(ViewAssertions.matches(isDisplayed())).perform(ViewActions.click());
    }

    public static void checkUp(Integer... activityLayoutIds)
    {
        clickNavigateUp();
        for (Integer layout : activityLayoutIds) {
            Espresso.onView(withId(layout)).check(ViewAssertions.matches(isDisplayed()));
        }
    }

    public static void checkBack(ViewInteraction viewInteraction, Integer... activityLayoutIds){
        viewInteraction.perform(ViewActions.closeSoftKeyboard()).perform(ViewActions.pressBack());
        for (Integer layout : activityLayoutIds) {
            Espresso.onView(withId(layout)).check(ViewAssertions.matches(isDisplayed()));
        }
    }

    //    ============================ TOASTS ============================

    public static void checkToastInTest(int resourceId, Activity activity, int... resourceFieldsErrorId)
    {
        Resources resources = activity.getResources();

        ViewInteraction toast = Espresso.onView(
                ViewMatchers.withText(Matchers.containsString(resources.getText(resourceId).toString())))
                .inRoot(RootMatchers.withDecorView(Matchers.not(activity.getWindow().getDecorView())))
                .check(ViewAssertions.matches(isDisplayed()));

        if (resourceFieldsErrorId != null) {
            for (int field : resourceFieldsErrorId) {
                toast.check(ViewAssertions.matches(ViewMatchers.withText(Matchers.containsString(resources.getText(field).toString()))));
            }
        }
    }

    public static void checkNoToastInTest(int resourceStringId, Activity activity)
    {
        Resources resources = activity.getResources();

        Espresso.onView(
                ViewMatchers.withText(Matchers.containsString(resources.getText(resourceStringId).toString())))
                .inRoot(RootMatchers.withDecorView(Matchers.not(activity.getWindow().getDecorView())))
                .check(ViewAssertions.doesNotExist());
    }
}
