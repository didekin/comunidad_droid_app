package com.didekindroid.testutil;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
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
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler scheduler) throws Exception
            {
                return trampoline();
            }
        });
    }

    public static void trampolineReplaceAndroidMain()
    {
        RxAndroidPlugins.setMainThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler scheduler) throws Exception
            {
                return trampoline();
            }
        });
    }

    public static void singleReplaceAndroidMain()
    {
        RxAndroidPlugins.setMainThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler scheduler) throws Exception
            {
                return single();
            }
        });
    }

    public static void singleReplaceIoScheduler()
    {
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler scheduler) throws Exception
            {
                return single();
            }
        });
    }

    public static void resetAllSchedulers()
    {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

}
