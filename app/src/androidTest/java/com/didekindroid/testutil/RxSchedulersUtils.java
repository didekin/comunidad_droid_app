package com.didekindroid.testutil;

import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

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
                return Schedulers.trampoline();
            }
        });
    }
}
