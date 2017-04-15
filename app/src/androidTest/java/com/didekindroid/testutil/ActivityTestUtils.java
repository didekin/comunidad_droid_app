package com.didekindroid.testutil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.test.espresso.NoMatchingRootException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ControllerIf;
import com.didekindroid.api.CtrlerSpinner;
import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenIf;
import com.didekindroid.util.BundleKey;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.exception.ExceptionMsgIf;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.test.InstrumentationRegistry.getTargetContext;
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
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 18:51
 */

public final class ActivityTestUtils {

    public static final long LONG_DEFAULT_EXTRA_VALUE = 0L;

    private ActivityTestUtils()
    {
    }

    //    ======================== ACTIVITY / FRAGMENT ================================

    public static Callable<Boolean> isActivityDying(final Activity activity)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                return activity.isFinishing() || activity.isDestroyed();
            }
        };
    }

    public static Callable<Boolean> isResourceIdDisplayed(final int resourceStringId)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                try {
                    onView(withId(resourceStringId)).check(matches(isDisplayed()));
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            }
        };
    }

    public static Callable<Boolean> isViewDisplayed(final Matcher<View> viewMatcher)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                try {
                    onView(viewMatcher).check(matches(isDisplayed()));
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            }
        };
    }

    public static Callable<Boolean> isComuSpinnerWithText(final String textToCheck)
    {

        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                try {
                    onView(allOf(
                            withId(R.id.app_spinner_1_dropdown_item),
                            withParent(withId(R.id.incid_reg_comunidad_spinner))
                    )).check(matches(withText(is(textToCheck))
                    )).check(matches(isDisplayed()));
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            }
        };
    }

    public static View doFragmentView(int resourdeIdLayout, String description)
    {
        LayoutInflater inflater = (LayoutInflater) getTargetContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View frView = inflater.inflate(resourdeIdLayout, null);
        EditText editText = (EditText) frView.findViewById(R.id.incid_reg_desc_ed);
        editText.setText(description);
        return frView;
    }

    //    ============================= CONTROLLER/Adapters ===================================

    public static CompositeDisposable addSubscription(ControllerIf controller)
    {
        controller.getSubscriptions().add(new Disposable() {
            @Override
            public void dispose()
            {
            }

            @Override
            public boolean isDisposed()
            {
                return false;
            }
        });
        assertThat(controller.getSubscriptions().size(), is(1));
        return controller.getSubscriptions();
    }

    public static Callable<Boolean> gcmTokenSentFlag(final CtrlerFirebaseTokenIf controller)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                return controller.isGcmTokenSentServer();
            }
        };
    }

    public static Callable<Integer> getAdapterCount(final BaseAdapter adapter)
    {
        return new Callable<Integer>() {
            public Integer call() throws Exception
            {
                return adapter.getCount();
            }
        };
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

    //    ============================= EXCEPTIONS/ERRORS ===================================

    public static boolean checkProcessCtrlError(final ViewerIf viewer, final ExceptionMsgIf exceptionMsg, ActionForUiExceptionIf actionToExpect)
    {
        final Activity activityError = viewer.getActivity();
        final AtomicReference<ActionForUiExceptionIf> actionException = new AtomicReference<>(null);

        activityError.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(actionException.compareAndSet(
                        null,
                        viewer.processControllerError(new UiException(new ErrorBean(exceptionMsg)))
                        ),
                        is(true)
                );
            }
        });
        waitAtMost(1, SECONDS).untilAtomic(actionException, notNullValue());
        return actionException.get().getActivityToGoClass().equals(actionToExpect.getActivityToGoClass());
    }

    public static void checkProcessCtrlErrorOnlyToast(final ViewerIf viewer,
                                                      final ExceptionMsgIf exceptionMsg, int resourceIdToast,
                                                      int activityLayoutId)
    {
        final Activity activityError = (Activity) viewer;
        activityError.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.processControllerError(new UiException(new ErrorBean(exceptionMsg)));
            }
        });
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    //    ============================= IDENTITY CACHE ===================================

    public static void checkUpdateTokenCache(SpringOauthToken oldToken)
    {
        assertThat(TKhandler.getTokenCache().get(), not(is(oldToken)));
        checkInitTokenCache();
    }

    public static void checkInitTokenCache()
    {
        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        assertThat(TKhandler.getTokenCache().get().getValue().isEmpty(), is(false));
        assertThat(TKhandler.getRefreshTokenValue().isEmpty(), is(false));
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
    }

    public static void checkNoInitCache()
    {
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
    }

    public static Callable<String> getRefreshTokenValue(final IdentityCacher identityCacher)
    {
        return new Callable<String>() {
            @Override
            public String call() throws Exception
            {
                return identityCacher.getRefreshTokenValue();
            }
        };
    }

    //    ============================= NAVIGATION ===================================

    public static void clickNavigateUp()
    {
        onView(allOf(
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

    public static void checkViewerReplaceView(final ViewerIf<View, ?> viewer, int resorceIdNextView)
    {
        viewer.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ActivityInitiatorIf.class.cast(viewer).initActivity(new Bundle());
            }
        });
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(resorceIdNextView));
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

    public static Callable<Boolean> isToastInView(final int resourceStringId, final Activity activity, final int... resorceErrorId)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                try {
                    checkToastInTest(resourceStringId, activity, resorceErrorId);
                    return true;
                } catch (NoMatchingViewException | NoMatchingRootException ne) {
                    return false;
                }
            }
        };
    }

    public static Callable<Boolean> isNotToastInView(final int resourceStringId, final Activity activity)
    {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                try {
                    checkNoToastInTest(resourceStringId, activity);
                    return true;
                } catch (NoMatchingViewException | NoMatchingRootException ne) {
                    return false;
                }
            }
        };
    }

    //    ============================ VIEWERS ============================

    //    ................. Spinners ...............

    public static AtomicReference<String> doCtrlerInSpinnerViewer(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewer)
    {

        final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

        CtrlerSpinnerIf controllerLocal = new CtrlerSpinner(viewer) {
            @Override
            public boolean loadDataInSpinner()
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), CoreMatchers.is(BEFORE_METHOD_EXEC));
                return true;
            }

            @Override
            public int getSelectedFromItemId(long itemId)
            {
                return 0;
            }
        };

        viewer.setController(controllerLocal);
        return flagMethodExec;
    }

    public static long checkSavedStateInSpinner(Bundle stateToSave, long keyValue, BundleKey key, ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewer)
    {
        assertThat(viewer.getSelectedItemId(), is(LONG_DEFAULT_EXTRA_VALUE));
        viewer.saveState(stateToSave);

        if (stateToSave == null) {
            return LONG_DEFAULT_EXTRA_VALUE;
        }

        // Since itemSelectedId == 0, it returns default value.
        assertThat(stateToSave.getLong(key.getKey(), LONG_DEFAULT_EXTRA_VALUE), is(LONG_DEFAULT_EXTRA_VALUE));

        stateToSave.putLong(key.getKey(), keyValue);
        viewer.initSelectedItemId(stateToSave);
        viewer.saveState(stateToSave);
        // It returns initialized value.
        assertThat(stateToSave.getLong(key.getKey(), LONG_DEFAULT_EXTRA_VALUE), is(keyValue));

        return viewer.getSelectedItemId();
    }
}

