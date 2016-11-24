package com.didekinaar;

import android.app.Application;

import static com.didekinaar.PrimalCreator.creator;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 20:36
 */

public class DidekinAarApp extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
        creator.compareAndSet(null, new PrimalCreator(this));
    }
}
