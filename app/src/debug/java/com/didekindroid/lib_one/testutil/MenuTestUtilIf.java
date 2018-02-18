package com.didekindroid.lib_one.testutil;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:40
 */
@FunctionalInterface
public interface MenuTestUtilIf {
    void checkItem(Activity activity) throws InterruptedException;
}
