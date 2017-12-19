package com.didekindroid.api.router;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * User: pedro@didekin
 * Date: 14/12/2017
 * Time: 12:41
 */
public interface ActionForUiExceptionIf {

    @Nullable
    Class<? extends Activity> getActivityToGo();

    @NonNull
    Bundle getExtrasForActivity();

    int getToastResourceId();
}
