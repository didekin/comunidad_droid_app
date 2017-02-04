package com.didekindroid.testutil;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

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
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception
            {
                return trampoline();
            }
        });
    }
}
