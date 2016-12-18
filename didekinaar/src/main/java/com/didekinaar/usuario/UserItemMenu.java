package com.didekinaar.usuario;

import android.app.Activity;
import android.content.Intent;

import com.didekinaar.utils.ItemMenuIf;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 10:37
 */
public enum UserItemMenu implements ItemMenuIf {

    DELETE_ME_AC,
    LOGIN_AC,
    PASSWORD_CHANGE_AC,
    USER_DATA_AC,
    ;

    @Override
    public void doMenuItem(Activity activity, Class<? extends Activity> activityToGoClass)
    {
        Timber.d("doMenuItem(): %s", activityToGoClass.getName());
        Intent intent = new Intent(activity, activityToGoClass);
        activity.startActivity(intent);
    }
}