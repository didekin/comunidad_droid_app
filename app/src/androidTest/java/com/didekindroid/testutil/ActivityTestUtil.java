package com.didekindroid.testutil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingRootException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ControllerIf;
import com.didekindroid.lib_one.api.ViewerIf;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.lib_one.testutil.UiTestUtil.addSubscription;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 18:51
 */

public final class ActivityTestUtil {

    private ActivityTestUtil()
    {
    }

    //    ======================== ACTIVITY / FRAGMENT ================================

    public static Callable<Boolean> isActivityDying(final Activity activity)
    {
        return () -> activity.isFinishing() || activity.isDestroyed();
    }

    public static Callable<Boolean> isStatementTrue(Boolean objetToTest)
    {
        return () -> objetToTest;
    }

    public static Callable<Boolean> isResourceIdDisplayed(final Integer... resourceIds)
    {
        return () -> {
            try {
                for (int resourceId : resourceIds) {
                    onView(withId(resourceId)).check(matches(isDisplayed()));
                }
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    private static Callable<Boolean> isTextIdNonExist(final Integer... stringId)
    {
        return () -> {
            try {
                for (int resourceId : stringId) {
                    onView(withText(resourceId)).check(doesNotExist());
                }
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static Callable<Boolean> isViewDisplayed(final Matcher<View> viewMatcher)
    {
        return () -> {
            try {
                onView(viewMatcher).check(matches(isDisplayed()));
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static Callable<Boolean> isViewDisplayed(final ViewInteraction viewInteraction)
    {
        return () -> {
            try {
                viewInteraction.check(matches(isDisplayed()));
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static Callable<Boolean> isViewDisplayedAndPerform(final Matcher<View> viewMatcher, final ViewAction... viewActions)
    {
        return () -> {
            try {
                onView(viewMatcher).check(matches(isDisplayed())).perform(viewActions);
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static Callable<Boolean> isDataDisplayedAndClick(final Matcher<?> objectMatcher)
    {
        return () -> {
            try {
                Espresso.onData(objectMatcher)
                        .check(matches(isDisplayed()))
                        .perform(click());
                return true;
            } catch (NoMatchingViewException | PerformException e) {
                return false;
            }
        };
    }

    //    ============================= CONTROLLER/Adapters ===================================

    public static void checkSubscriptionsOnStop(final Activity activity, final ControllerIf... controllers)
    {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (ControllerIf controller : controllers) {
            atomicInteger.addAndGet(addSubscription(controller).size());
        }
        assertThat(atomicInteger.get() >= controllers.length, is(true));

        activity.runOnUiThread(() -> {
            getInstrumentation().callActivityOnStop(activity);
            atomicInteger.set(0);
            for (ControllerIf controller : controllers) {
                atomicInteger.addAndGet(controller.getSubscriptions().size());
            }
        });

        waitAtMost(6, SECONDS).untilAtomic(atomicInteger, is(0));
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
        onView(ViewMatchers.withClassName(is(DatePicker.class.getName())))
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

    // ============================  Dialogs  ============================

    public static void checkTextsInDialog(int... textsDialogs)
    {
        for (int textsDialog : textsDialogs) {
            waitAtMost(6, SECONDS).until(() -> {
                try {
                    onView(withText(textsDialog)).inRoot(isDialog()).check(matches(isDisplayed()));
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            });
        }
    }

    //    ============================= IDENTITY ===================================

    @SuppressWarnings("ConstantConditions")
    public static void checkIsRegistered(ViewerIf<?, ?> viewer)
    {
        AtomicBoolean isRegistered = new AtomicBoolean(false);
        isRegistered.compareAndSet(false, viewer.getController().isRegisteredUser());
        waitAtMost(4, SECONDS).untilTrue(isRegistered);
    }

    //    ============================= MENU ===================================

    public static void checkAppBarMenu(Activity activity, int menuResourceId, int nextLayoutId)
    {
        onView(withText(menuResourceId)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(activity);
        waitAtMost(4, SECONDS).until(() -> {
            try {
                onView(withText(menuResourceId)).check(matches(isDisplayed())).perform(click());
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        });
        onView(withId(nextLayoutId)).check(matches(isDisplayed()));
    }

    @SuppressWarnings("EmptyCatchBlock")
    public static void checkAppBarMnNotExist(Activity activity, int menuResourceId)
    {
        onView(withText(menuResourceId)).check(doesNotExist());
        try {
            openActionBarOverflowOrOptionsMenu(activity);
        } catch (NoMatchingViewException e) {
        }
        waitAtMost(4, SECONDS).until(isTextIdNonExist(menuResourceId));
    }

    /*    ============================= NAVIGATION ===================================*/

    public static void clickNavigateUp()
    {
        onView(allOf(
                withContentDescription(R.string.navigate_up_txt),
                isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    public static void checkBack(ViewInteraction viewInteraction, Integer... activityLayoutIds)
    {
        viewInteraction.perform(closeSoftKeyboard()).perform(ViewActions.pressBack());
        for (Integer layout : activityLayoutIds) {
            try {
                waitAtMost(6, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                fail();
            }
        }
    }

    public static void checkUp(Integer... activityLayoutIds)
    {
        clickNavigateUp();
        for (Integer layout : activityLayoutIds) {
            try {
                waitAtMost(6, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                fail();
            }
        }
    }

    //    ============================ TOASTS ============================

    public static void checkToastInTest(int resourceId, Activity activity, int... resourceFieldsErrorId)
    {
        Resources resources = activity.getResources();

        ViewInteraction toast = onView(
                withText(Matchers.containsString(resources.getText(resourceId).toString())))
                .inRoot(RootMatchers.withDecorView(CoreMatchers.not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        if (resourceFieldsErrorId != null) {
            for (int field : resourceFieldsErrorId) {
                toast.check(matches(withText(Matchers.containsString(resources.getText(field).toString()))));
            }
        }
    }

    public static Callable<Boolean> isToastInView(final int resourceStringId, final Activity activity, final int... resorceErrorId)
    {
        return () -> {
            try {
                checkToastInTest(resourceStringId, activity, resorceErrorId);
                return true;
            } catch (NoMatchingViewException | NoMatchingRootException ne) {
                return false;
            }
        };
    }
}

