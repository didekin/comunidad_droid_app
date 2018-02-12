package com.didekindroid.lib_one.testutil;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;

import static io.reactivex.schedulers.Schedulers.single;
import static io.reactivex.schedulers.Schedulers.trampoline;

/**
 * User: pedro@didekin
 * Date: 25/01/17
 * Time: 11:52
 */

public class RxSchedulersUtils {

    public static void trampolineReplaceIoScheduler()
    {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> trampoline());
    }

    public static void trampolineReplaceAndroidMain()
    {
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> trampoline());
    }

    public static void trampolineReplaceIoMain()
    {
        trampolineReplaceAndroidMain();
        trampolineReplaceIoScheduler();
    }

    @SuppressWarnings("unused")
    public static void singleReplaceAndroidMain()
    {
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> single());
    }

    @SuppressWarnings("unused")
    public static void singleReplaceIoScheduler()
    {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> single());
    }

    public static void resetAllSchedulers()
    {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

}
