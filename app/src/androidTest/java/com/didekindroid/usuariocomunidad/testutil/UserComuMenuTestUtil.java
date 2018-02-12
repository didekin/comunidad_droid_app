package com.didekindroid.usuariocomunidad.testutil;

import android.app.Activity;

import com.didekindroid.R;
import com.didekindroid.lib_one.testutil.MenuTestUtilIf;

import static com.didekindroid.testutil.ActivityTestUtils.checkAppBarMenu;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.REGISTERED_USER;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.REQUIRES_USER_NO_TOKEN;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:31
 */

public enum UserComuMenuTestUtil implements MenuTestUtilIf {

    REG_COMU_USERCOMU_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            throw new UnsupportedOperationException(REGISTERED_USER);
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            checkAppBarMenu(activity, R.string.reg_nueva_comunidad_ac_mn, R.id.reg_comu_and_usercomu_layout);
        }
    },

    REG_COMU_USER_USERCOMU_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            checkAppBarMenu(activity, R.string.reg_nueva_comunidad_ac_mn, R.id.reg_comu_and_user_and_usercomu_ac_layout);
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            throw new UnsupportedOperationException(REQUIRES_USER_NO_TOKEN);
        }
    },

    SEE_USERCOMU_BY_COMU_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            throw new UnsupportedOperationException(SEE_USERCOMU_BY_COMU_AC.name() + REGISTERED_USER);
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            checkAppBarMenu(activity, R.string.see_usercomu_by_comu_ac_mn, R.id.see_usercomu_by_comu_frg);
        }
    },

    SEE_USERCOMU_BY_USER_AC {
        @Override
        public void checkItemNoRegisterUser(Activity activity)
        {
            throw new UnsupportedOperationException(SEE_USERCOMU_BY_USER_AC + " without token");
        }

        @Override
        public void checkItemRegisterUser(Activity activity)
        {
            checkAppBarMenu(activity, R.string.see_usercomu_by_user_ac_mn, R.id.see_usercomu_by_user_frg);
        }
    },;
}
