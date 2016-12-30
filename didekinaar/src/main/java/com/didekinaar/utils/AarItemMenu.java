package com.didekinaar.utils;

import android.app.Activity;
import android.content.Intent;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 10:37
 */
public enum AarItemMenu implements ItemMenuIf {

    mn_handler,
    ;

    @Override
    public void doMenuItem(Activity activity, Class<? extends Activity> activityToGoClass)
    {
        Timber.d("doMenuItem(): %s", activityToGoClass.getName());
        Intent intent = activity.getIntent();
        if (intent == null){
            intent = new Intent();
        }
        intent.setClass(activity, activityToGoClass);
        activity.startActivity(intent);
    }
}