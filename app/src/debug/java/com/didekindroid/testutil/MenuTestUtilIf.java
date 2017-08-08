package com.didekindroid.testutil;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:40
 */

public interface MenuTestUtilIf {
    void checkItemNoRegisterUser(Activity activity) throws InterruptedException;
    void checkItemRegisterUser(Activity activity) throws InterruptedException;
}
