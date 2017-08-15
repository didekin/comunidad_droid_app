package com.didekindroid.testutil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.NoMatchingRootException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.runner.lifecycle.Stage;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.api.ControllerIf;
import com.didekindroid.api.CtrlerSelectListIf;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.api.ViewerSelectListIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenIf;
import com.didekindroid.util.BundleKey;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.exception.ExceptionMsgIf;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
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
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 18:51
 */

public final class ActivityTestUtils {

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

    public static Callable<Boolean> isResourceIdDisplayed(final Integer... resourceIds)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                try {
                    for (int resourceId : resourceIds) {
                        onView(withId(resourceId)).check(matches(isDisplayed()));
                    }
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            }
        };
    }

    public static Callable<Boolean> isTextIdNonExist(final Integer... stringId)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                try {
                    for (int resourceId : stringId) {
                        onView(withText(resourceId)).check(doesNotExist());
                    }
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            }
        };
    }

    public static Callable<Boolean> isViewDisplayed(final Matcher<View> viewMatcher, final ViewAction... viewActions)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                try {
                    onView(viewMatcher).check(matches(isDisplayed())).perform(viewActions);
                    return true;
                } catch (NoMatchingViewException ne) {
                    return false;
                }
            }
        };
    }

    public static Callable<Boolean> isDataDisplayedAndClick(final Matcher<?> objectMatcher)
    {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                try {
                    onData(objectMatcher)
                            .check(matches(isDisplayed()))
                            .perform(click());
                    return true;
                } catch (NoMatchingViewException | PerformException e) {
                    return false;
                }
            }
        };
    }

    //    ============================== BUTTONS ============================

    public static int focusOnButton(Activity activity, int buttonRsId)
    {
        final Button button = activity.findViewById(buttonRsId);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                button.setFocusable(true);
                button.setFocusableInTouchMode(true);
                button.requestFocus();
            }
        });
        return buttonRsId;
    }

    //    ============================= CONTROLLER/Adapters ===================================

    public static CompositeDisposable addSubscription(ControllerIf controller)
    {
        int oldNumberSubscriptions = controller.getSubscriptions().size();
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
        assertThat(controller.getSubscriptions().size(), is(++oldNumberSubscriptions));
        assertThat(controller.getSubscriptions().size() > 0, is(true));
        return controller.getSubscriptions();
    }

    public static void checkSubscriptionsOnStop(Activity activity, ControllerIf... controllers)
    {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (ControllerIf controller : controllers) {
            atomicInteger.addAndGet(addSubscription(controller).size());
        }
        assertThat(atomicInteger.get() >= controllers.length, is(true));

        getInstrumentation().callActivityOnStop(activity);
        atomicInteger.set(0);
        for (ControllerIf controller : controllers) {
            atomicInteger.addAndGet(controller.getSubscriptions().size());
        }
        waitAtMost(6, SECONDS).untilAtomic(atomicInteger, is(0));
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

    public static Callable<Adapter> getAdapter(final AdapterView<? extends Adapter> adapterView)
    {
        return new Callable<Adapter>() {
            public Adapter call() throws Exception
            {
                return adapterView.getAdapter();
            }
        };
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

    public static boolean checkOnErrorInObserver(final ViewerIf viewer, final ExceptionMsgIf exceptionMsg, ActionForUiExceptionIf actionToExpect)
    {
        final Activity activityError = viewer.getActivity();
        final AtomicReference<ActionForUiExceptionIf> actionException = new AtomicReference<>(null);

        activityError.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(actionException.compareAndSet(
                        null,
                        viewer.onErrorInObserver(new UiException(new ErrorBean(exceptionMsg)))
                        ),
                        is(true)
                );
            }
        });
        waitAtMost(1, SECONDS).untilAtomic(actionException, notNullValue());
        return actionException.get().getActivityToGoClass().equals(actionToExpect.getActivityToGoClass());
    }

    //    ============================= IDENTITY ===================================

    public static void checkIsRegistered(ViewerIf<?, ?> viewer)
    {
        AtomicBoolean isRegistered = new AtomicBoolean(false);
        isRegistered.compareAndSet(false, viewer.getController().isRegisteredUser());
        waitAtMost(4, SECONDS).untilTrue(isRegistered);
    }

    public static void checkUpdateTokenCache(SpringOauthToken oldToken)
    {
        checkUpdatedCacheAfterPswd(true, oldToken);
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

    public static void checkUpdatedCacheAfterPswd(boolean isPswdUpdated, SpringOauthToken oldToken)
    {
        checkInitTokenCache();
        if (isPswdUpdated) {
            assertThat(TKhandler.getTokenCache().get(), not(is(oldToken)));
        } else {
            assertThat(TKhandler.getTokenCache().get(), is(oldToken));
        }
    }

    //    ============================= NAVIGATION ===================================

    public static void checkMenu(Activity activity, int menuResourceId, int actionResourceId)
    {
        try {
            onView(withText(menuResourceId)).check(doesNotExist());
            openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
        } catch (Throwable e) {
        } finally {
            onView(withText(menuResourceId)).check(matches(isDisplayed())).perform(click());
            waitAtMost(4, SECONDS).until(isResourceIdDisplayed(actionResourceId));
        }
    }

    public static void clickNavigateUp()
    {
        onView(allOf(
                withContentDescription(R.string.navigate_up_txt),
                isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    public static void checkUp(Integer... activityLayoutIds)
    {
        clickNavigateUp();
        for (Integer layout : activityLayoutIds)
            try {
                waitAtMost(4, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                fail();
            }
    }

    @SuppressWarnings("unused")
    public static Stage getStageByActivity(final Activity activity) throws ExecutionException, InterruptedException
    {
        Timber.d("============= getStageByActivity() =================");

        final FutureTask<Stage> taskGetActivities = new FutureTask<>(new Callable<Stage>() {
            @Override
            public Stage call() throws Exception
            {
                return getInstance().getLifecycleStageOf(activity);
            }
        });
        getInstrumentation().runOnMainSync(taskGetActivities);
        return taskGetActivities.get();
    }

    public static Collection<Activity> getActivitesInTaskByStage(final Stage stage) throws ExecutionException, InterruptedException
    {
        Timber.d("============= getActivitesInTaskByStage() =================");

        final FutureTask<Collection<Activity>> taskGetActivities = new FutureTask<>(new Callable<Collection<Activity>>() {
            @Override
            public Collection<Activity> call() throws Exception
            {
                return getInstance().getActivitiesInStage(stage);
            }
        });
        getInstrumentation().runOnMainSync(taskGetActivities);
        return taskGetActivities.get();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void cleanTasks(Activity activity)
    {
        ActivityManager manager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> tasks = manager.getAppTasks();
        for (ActivityManager.AppTask task : tasks) {
            task.finishAndRemoveTask();
        }
    }

    public static void checkBack(ViewInteraction viewInteraction, Integer... activityLayoutIds)
    {
        viewInteraction.perform(closeSoftKeyboard()).perform(pressBack());
        for (Integer layout : activityLayoutIds) {
            try {
                waitAtMost(4, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                fail();
            }
        }
    }

    public static void checkViewerReplaceComponent(final ViewerIf<? extends View, ? extends ControllerIf> viewer, int resorceIdNextView, Bundle bundle)
    {
        if (bundle == null) {
            bundle = new Bundle(0);
        }
        final Bundle finalBundle = bundle;

        viewer.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                new ActivityInitiator(viewer.getActivity()).initAcWithBundle(finalBundle);
            }
        });
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(resorceIdNextView)));
    }

    //    ============================ SPINNERS ============================

    public static <E extends Serializable> void checkSpinnerCtrlerLoadItems(CtrlerSelectListIf<E> controller, Long... entityId)
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            Timber.d("checkSpinnerCtrlerLoadItems(), Thread: %s", Thread.currentThread().getName());
            assertThat(controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<E>>() {
                @Override
                public void onSuccess(List<E> es)
                {
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, entityId), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
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

    //    ============================ VIEWERS ============================

    public static void checkSavedStateWithItemSelected(ViewerSelectListIf viewer, BundleKey bundleKey)
    {
        viewer.setItemSelectedId(18L);
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        assertThat(bundle.getLong(bundleKey.getKey()), is(18L));
    }

    public static <T extends AppCompatActivity & ViewerParentInjectorIf> void checkChildInViewer(T activity)
    {
        final ViewerMock viewerChild = new ViewerMock(activity);
        activity.setChildInViewer(viewerChild);
        assertThat(activity.getViewerAsParent().getChildViewer(ViewerMock.class), CoreMatchers.<ViewerIf>is(viewerChild));
    }
}

