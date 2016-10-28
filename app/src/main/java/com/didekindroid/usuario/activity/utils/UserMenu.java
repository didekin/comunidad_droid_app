package com.didekindroid.usuario.activity.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.didekindroid.R;
import com.didekindroid.usuario.activity.ComuSearchAc;
import com.didekindroid.usuario.activity.DeleteMeAc;
import com.didekindroid.usuario.activity.LoginAc;
import com.didekindroid.usuario.activity.PasswordChangeAc;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuario.activity.RegComuAndUserComuAc;
import com.didekindroid.usuario.activity.SeeUserComuByComuAc;
import com.didekindroid.usuario.activity.SeeUserComuByUserAc;
import com.didekindroid.usuario.activity.UserDataAc;

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 10:37
 */
public enum UserMenu {

    COMU_DATA_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("comu_data_ac.doMenuItem()");
            activity.startActivity(activity.getIntent());
        }
    },

    COMU_SEARCH_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("comu_search_ac.doMenuItem()");
            Intent intent = new Intent(activity, ComuSearchAc.class);
            activity.startActivity(intent);
        }
    },

    DELETE_ME_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("delete_me_ac.doMenuItem()");
            Intent intent = new Intent(activity, DeleteMeAc.class);
            activity.startActivity(intent);
        }
    },


    LOGIN_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("login_ac.doMenuItem()");
            Intent intent = new Intent(activity, LoginAc.class);
            activity.startActivity(intent);
        }
    },

    PASSWORD_CHANGE_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("password_change_ac.doMenuItem()");
            Intent intent = new Intent(activity, PasswordChangeAc.class);
            activity.startActivity(intent);
        }
    },

    REG_COMU_USERCOMU_AC { // Menu: Nueva comunidad.
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.i("reg_comu_usercomu.doMenuItem().");
            Intent intent = new Intent(activity, RegComuAndUserComuAc.class);
            activity.startActivity(intent);
        }
    },

    REG_COMU_USER_USERCOMU_AC { /* Menu: Nueva comunidad.*/
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("reg_comu_user_usercomu.doMenuItem()");
            Intent intent = new Intent(activity, RegComuAndUserAndUserComuAc.class);
            activity.startActivity(intent);
        }
    },

    SEE_USERCOMU_BY_COMU_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("see_usercomu_by_comu_ac.doMenuItem()");
            Intent intent = activity.getIntent();
            intent.setClass(activity, SeeUserComuByComuAc.class);
            activity.startActivity(intent);
        }
    },

    SEE_USERCOMU_BY_USER_AC {  // Comunidades de un userComu: Tus comunidades.

        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.i("comu_by_user.doMenuItem()");

            if (!isRegisteredUser(activity)) {
                Timber.i("comu_by_user.doMenuItem(), user not registered.");
                makeToast(activity, R.string.user_without_signedUp, Toast.LENGTH_SHORT);
            } else {
                Timber.i("comu_by_user.doMenuItem(), user registered.");
                Intent intent = new Intent(activity, SeeUserComuByUserAc.class);
                activity.startActivity(intent);
            }
        }
    },

    USER_DATA_AC {  // menú: Mi userComu.

        @Override
        public void doMenuItem(Activity activity)
        {
            Timber.d("user_data_ac_mn.doMenuItem()");

            if (!isRegisteredUser(activity)) {
                Timber.i("user_data_ac_mn.doMenuItem(), user not registered.");
                makeToast(activity, R.string.user_without_signedUp, Toast.LENGTH_SHORT);
            } else {
                Timber.i("user_data_ac_mn.doMenuItem(), user registered.");
                Intent intent = new Intent(activity, UserDataAc.class);
                activity.startActivity(intent);
            }
        }
    },;

    public abstract void doMenuItem(Activity activity);

    //  =================================  HELPER METHODS ====================================

    public static void doUpMenuClearSingleTop(Activity parentActivity)
    {
        Intent intent = NavUtils.getParentActivityIntent(parentActivity);
        // We need both flags to reuse the intent of the parent activity.
        intent.setFlags(/*Intent.FLAG_ACTIVITY_CLEAR_TOP | */Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(parentActivity, intent);
    }
}