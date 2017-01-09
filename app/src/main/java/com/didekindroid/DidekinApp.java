package com.didekindroid;

import android.app.Application;

import com.didekinaar.AppInitializer;
import com.didekindroid.exception.UiExceptionDealer;

import static com.didekinaar.AppInitializer.creator;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 19:19
 */
public final class DidekinApp extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
        creator.compareAndSet(null, new AppInitializer(this, new UiExceptionDealer()));
    }
}
