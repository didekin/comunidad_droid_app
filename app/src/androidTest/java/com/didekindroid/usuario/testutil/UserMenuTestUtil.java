package com.didekindroid.usuario.testutil;

import android.app.Activity;

import com.didekindroid.R;
import com.didekindroid.lib_one.testutil.MenuTestUtilIf;

import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.testutil.ActivityTestUtil.checkAppBarMenu;
import static com.didekindroid.testutil.ActivityTestUtil.checkAppBarMnNotExist;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 15:09
 */
public enum UserMenuTestUtil implements MenuTestUtilIf {
    LOGIN_AC {
        @Override
        public void checkItem(Activity activity)
        {
            if (secInitializer.get().getTkCacher().isUserRegistered()) {
                checkAppBarMnNotExist(activity, R.string.login_ac_mn);
            } else {
                checkAppBarMenu(activity, R.string.login_ac_mn, R.id.login_ac_layout);
            }
        }
    },;
}