package com.didekindroid.testutil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingRootException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.lib_one.api.ChildViewersInjectorIf;
import com.didekindroid.lib_one.api.ControllerIf;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.ViewerSelectListIf;
import com.didekindroid.lib_one.util.BundleKey;
import com.didekinlib.http.auth.SpringOauthToken;

import org.awaitility.Awaitility;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;

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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.view.Gravity.LEFT;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.concurrent.TimeUnit.SECONDS;

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
                    Espresso.onView(ViewMatchers.withId(resourceId)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
                }
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static Callable<Boolean> isTextIdNonExist(final Integer... stringId)
    {
        return () -> {
            try {
                for (int resourceId : stringId) {
                    Espresso.onView(ViewMatchers.withText(resourceId)).check(ViewAssertions.doesNotExist());
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
                Espresso.onView(viewMatcher).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
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
                viewInteraction.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
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
                Espresso.onView(viewMatcher).check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(viewActions);
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
                        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                        .perform(ViewActions.click());
                return true;
            } catch (NoMatchingViewException | PerformException e) {
                return false;
            }
        };
    }

    public static int focusOnView(Activity activity, int viewRsId)
    {
        final View view = activity.findViewById(viewRsId);

        activity.runOnUiThread(() -> {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        });
        return viewRsId;
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
        Assert.assertThat(controller.getSubscriptions().size(), CoreMatchers.is(++oldNumberSubscriptions));
        Assert.assertThat(controller.getSubscriptions().size() > 0, CoreMatchers.is(true));
        return controller.getSubscriptions();
    }

    public static void checkSubscriptionsOnStop(final Activity activity, final ControllerIf... controllers)
    {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (ControllerIf controller : controllers) {
            atomicInteger.addAndGet(addSubscription(controller).size());
        }
        Assert.assertThat(atomicInteger.get() >= controllers.length, CoreMatchers.is(true));

        activity.runOnUiThread(() -> {
            InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
            atomicInteger.set(0);
            for (ControllerIf controller : controllers) {
                atomicInteger.addAndGet(controller.getSubscriptions().size());
            }
        });

        Awaitility.waitAtMost(6, SECONDS).untilAtomic(atomicInteger, CoreMatchers.is(0));
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
        Espresso.onView(ViewMatchers.withClassName(CoreMatchers.is(DatePicker.class.getName())))
                .perform(PickerActions.setDate(newCalendar.get(Calendar.YEAR), newCalendar.get(MONTH) + 1, newCalendar.get(DAY_OF_MONTH)));
        return newCalendar;
    }

    public static void closeDatePicker(Context context)
    {
        if (SDK_INT == KITKAT) {
            Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
        }
        if (SDK_INT > KITKAT) {
            Espresso.onView(ViewMatchers.withText(context.getString(android.R.string.ok))).perform(ViewActions.click());
        }
    }

    //    ============================= IDENTITY ===================================

    public static void checkIsRegistered(ViewerIf<?, ?> viewer)
    {
        AtomicBoolean isRegistered = new AtomicBoolean(false);
        isRegistered.compareAndSet(false, viewer.getController().isRegisteredUser());
        Awaitility.waitAtMost(4, SECONDS).untilTrue(isRegistered);
    }

    public static void checkUpdateTokenCache(SpringOauthToken oldToken)
    {
        checkUpdatedCacheAfterPswd(true, oldToken);
    }

    public static void checkInitTokenCache()
    {
        Assert.assertThat(TKhandler.getTokenCache().get(), CoreMatchers.notNullValue());
        Assert.assertThat(TKhandler.getTokenCache().get().getValue().isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(TKhandler.getRefreshTokenValue().isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(TKhandler.getRefreshTokenFile().exists(), CoreMatchers.is(true));
    }

    public static void checkNoInitCache()
    {
        Assert.assertThat(TKhandler.getTokenCache().get(), CoreMatchers.nullValue());
        Assert.assertThat(TKhandler.getRefreshTokenFile().exists(), CoreMatchers.is(false));
    }

    public static void checkUpdatedCacheAfterPswd(boolean isPswdUpdated, SpringOauthToken oldToken)
    {
        checkInitTokenCache();
        if (isPswdUpdated) {
            Assert.assertThat(TKhandler.getTokenCache().get(), CoreMatchers.not(CoreMatchers.is(oldToken)));
        } else {
            Assert.assertThat(TKhandler.getTokenCache().get(), CoreMatchers.is(oldToken));
        }
    }

    //    ============================= MENU ===================================

    @SuppressWarnings("unused")
    @NonNull
    public static Menu doMockMenu(Activity activity, int menuMockRsId)
    {
        PopupMenu popupMenu = new PopupMenu(InstrumentationRegistry.getTargetContext(), null);
        Menu menu = popupMenu.getMenu();
        activity.getMenuInflater().inflate(menuMockRsId, menu);
        return menu;
    }

    public static void checkAppBarMenu(Activity activity, int menuResourceId, int actionResourceId)
    {
        try {
            Espresso.onView(ViewMatchers.withText(menuResourceId)).check(ViewAssertions.doesNotExist());
            Espresso.openActionBarOverflowOrOptionsMenu(activity);
            Thread.sleep(1000);
        } catch (Throwable e) {
        } finally {
            Espresso.onView(ViewMatchers.withText(menuResourceId)).check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(ViewActions.click());
            Awaitility.waitAtMost(4, SECONDS).until(isResourceIdDisplayed(actionResourceId));
        }
    }

    public static void checkAppBarMnNotExist(Activity activity, int menuResourceId)
    {
        Espresso.onView(ViewMatchers.withText(menuResourceId)).check(ViewAssertions.doesNotExist());
        try {
            Espresso.openActionBarOverflowOrOptionsMenu(activity);
        } catch (NoMatchingViewException e) {
        }
        Awaitility.waitAtMost(4, SECONDS).until(isTextIdNonExist(menuResourceId));
    }

    public static void checkDrawerMenu(int drawerLayoutId, int navigationViewId, int menuResourceId, int actionResourceId)
    {
        Espresso.onView(ViewMatchers.withId(drawerLayoutId)).check(ViewAssertions.matches(isClosed(LEFT))).perform(DrawerActions.open());
        Espresso.onView(ViewMatchers.withId(navigationViewId)).perform(NavigationViewActions.navigateTo(menuResourceId));
        Awaitility.waitAtMost(4, SECONDS).until(isResourceIdDisplayed(actionResourceId));
    }

    /*    ============================= NAVIGATION ===================================*/

    public static void clickNavigateUp()
    {
        Espresso.onView(CoreMatchers.allOf(
                ViewMatchers.withContentDescription(R.string.navigate_up_txt),
                ViewMatchers.isClickable())
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(ViewActions.click());
    }

    public static void checkBack(ViewInteraction viewInteraction, Integer... activityLayoutIds)
    {
        viewInteraction.perform(ViewActions.closeSoftKeyboard()).perform(ViewActions.pressBack());
        for (Integer layout : activityLayoutIds) {
            try {
                Awaitility.waitAtMost(6, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                Assert.fail();
            }
        }
    }

    public static void checkUp(Integer... activityLayoutIds)
    {
        clickNavigateUp();
        for (Integer layout : activityLayoutIds) {
            try {
                Awaitility.waitAtMost(6, SECONDS).until(isResourceIdDisplayed(layout));
            } catch (Exception e) {
                Assert.fail();
            }
        }
    }

    @SuppressWarnings("unused")
    public static Stage getStageByActivity(final Activity activity) throws ExecutionException, InterruptedException
    {
        Timber.d("============= getStageByActivity() =================");

        final FutureTask<Stage> taskGetActivities = new FutureTask<>(() -> ActivityLifecycleMonitorRegistry.getInstance().getLifecycleStageOf(activity));
        InstrumentationRegistry.getInstrumentation().runOnMainSync(taskGetActivities);
        return taskGetActivities.get();
    }

    public static Collection<Activity> getActivitesInTaskByStage(final Stage stage) throws ExecutionException, InterruptedException
    {
        Timber.d("============= getActivitesInTaskByStage() =================");

        final FutureTask<Collection<Activity>> taskGetActivities = new FutureTask<>(() -> ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(stage));
        InstrumentationRegistry.getInstrumentation().runOnMainSync(taskGetActivities);
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

    //    ============================ SPINNERS ============================

    public static <E extends Serializable> void checkSpinnerCtrlerLoadItems(CtrlerSelectListIf<E> controller, Long... entityId)
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            Timber.d("checkSpinnerCtrlerLoadItems(), Thread: %s", Thread.currentThread().getName());
            Assert.assertThat(controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<E>>() {
                @Override
                public void onSuccess(List<E> es)
                {
                }

                @Override
                public void onError(Throwable e)
                {
                    Assert.fail();
                }
            }, entityId), CoreMatchers.is(true));
        } finally {
            resetAllSchedulers();
        }
        Assert.assertThat(controller.getSubscriptions().size(), CoreMatchers.is(1));
    }

    //    ============================ TOASTS ============================

    public static void checkToastInTest(int resourceId, Activity activity, int... resourceFieldsErrorId)
    {
        Resources resources = activity.getResources();

        ViewInteraction toast = Espresso.onView(
                ViewMatchers.withText(Matchers.containsString(resources.getText(resourceId).toString())))
                .inRoot(RootMatchers.withDecorView(CoreMatchers.not(activity.getWindow().getDecorView())))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        if (resourceFieldsErrorId != null) {
            for (int field : resourceFieldsErrorId) {
                toast.check(ViewAssertions.matches(ViewMatchers.withText(Matchers.containsString(resources.getText(field).toString()))));
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

    //    ============================ VIEWERS ============================

    public static void checkSavedStateWithItemSelected(ViewerSelectListIf viewer, BundleKey bundleKey)
    {
        viewer.setSelectedItemId(18L);
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        Assert.assertThat(bundle.getLong(bundleKey.getKey()), CoreMatchers.is(18L));
    }

    public static <T extends AppCompatActivity & ChildViewersInjectorIf> void checkChildInViewer(T activity)
    {
        final ViewerMock viewerChild = new ViewerMock(activity);
        activity.setChildInParentViewer(viewerChild);
        Assert.assertThat(activity.getParentViewer().getChildViewer(ViewerMock.class), CoreMatchers.is(viewerChild));
    }
}

