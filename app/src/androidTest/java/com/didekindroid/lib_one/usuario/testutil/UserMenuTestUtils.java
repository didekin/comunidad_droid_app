package com.didekindroid.lib_one.usuario.testutil;

import android.app.Activity;

import com.didekindroid.R;
import com.didekindroid.lib_one.testutil.EspressoTestUtil;
import com.didekindroid.lib_one.testutil.MenuTestUtilIf;

import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 15:09
 */
public enum UserMenuTestUtils implements MenuTestUtilIf {

    DELETE_ME_AC {
        @Override
        public void checkItem(Activity activity)
        {
            EspressoTestUtil.checkItemMnExists(activity, R.string.delete_me_ac_mn, R.id.delete_me_ac_layout);
        }
    },

    LOGIN_AC {
        @Override
        public void checkItem(Activity activity)
        {
            if (secInitializer.get().getTkCacher().isRegisteredUser()) {
                EspressoTestUtil.checkItemMnNotExists(activity, R.string.login_ac_mn);
            } else {
                EspressoTestUtil.checkItemMnExists(activity, R.string.login_ac_mn, R.id.login_ac_layout);
            }
        }
    },

    PASSWORD_CHANGE_AC {
        @Override
        public void checkItem(Activity activity)
        {
            EspressoTestUtil.checkItemMnExists(activity, R.string.password_change_ac_mn, R.id.password_change_ac_layout);
        }
    },

    USER_DATA_AC {
        @Override
        public void checkItem(Activity activity)
        {
            if (secInitializer.get().getTkCacher().isRegisteredUser()) {
                EspressoTestUtil.checkItemMnExists(activity, R.string.user_data_ac_mn, R.id.user_data_ac_layout);
            } else {
                EspressoTestUtil.checkItemMnNotExists(activity, R.string.user_data_ac_mn);
            }
        }
    },;
}