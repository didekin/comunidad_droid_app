package com.didekindroid.usuariocomunidad.testutil;

import android.app.Activity;

import com.didekindroid.R;
import com.didekindroid.lib_one.testutil.MenuTestUtilIf;

import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_not_be_registered;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.testutil.ActivityTestUtil.checkAppBarMenu;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:31
 */

public enum UserComuMenuTestUtil implements MenuTestUtilIf {

    REG_COMU_USERCOMU_AC {
        @Override
        public void checkItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.reg_nueva_comunidad_ac_mn, R.id.reg_comu_and_usercomu_layout);
        }
    },

    REG_COMU_USER_USERCOMU_AC {
        @Override
        public void checkItem(Activity activity)
        {
            assertTrue(!secInitializer.get().getTkCacher().isUserRegistered(), user_should_not_be_registered);
            checkAppBarMenu(activity, R.string.reg_nueva_comunidad_ac_mn, R.id.reg_comu_and_user_and_usercomu_ac_layout);
        }
    },

    SEE_USERCOMU_BY_COMU_AC {
        @Override
        public void checkItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.see_usercomu_by_comu_ac_mn, R.id.see_usercomu_by_comu_frg);
        }
    },

    SEE_USERCOMU_BY_USER_AC {
        @Override
        public void checkItem(Activity activity)
        {
            checkAppBarMenu(activity, R.string.see_usercomu_by_user_ac_mn, R.id.see_usercomu_by_user_frg);
        }
    },;
}
