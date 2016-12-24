package com.didekinaar.usuario.delete;

import android.app.Activity;
import android.content.Context;

/**
 * User: pedro@didekin
 * Date: 23/12/16
 * Time: 11:46
 */
interface DeleteMeControllerIf {
    void unregisterUser(Context context, Class<? extends Activity> nextActivityClass);
}
