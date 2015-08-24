package com.didekindroid.usuario.common;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.UserDataAc;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuario.activity.RegComuAndUserComuAc;
import com.didekindroid.usuario.activity.ComusByUserListAc;

import static com.didekindroid.common.ui.UIutils.*;
import static com.didekindroid.common.ui.UIutils.isRegisteredUser;

/**
 * User: pedro@didekin
 * Date: 10/08/15
 * Time: 10:37
 */
public enum UserMenu {

    USER_DATA_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "user_data_ac_mn.doMenuItem()");

            if (!isRegisteredUser(activity)) {
                Log.i(TAG, "user_data_ac_mn.doMenuItem(), user not registered.");
                makeToast(activity, R.string.user_without_signedUp);
            } else {
                Log.i(TAG, "user_data_ac_mn.doMenuItem(), user registered.");
                Intent intent = new Intent(activity, UserDataAc.class);
                activity.startActivity(intent);
            }
        }
    },

    COMU_BY_USER_LIST_AC {  // Comunidades de un usuario.
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.i(TAG, "comu_by_user.doMenuItem()");

            if (!isRegisteredUser(activity)) {
                Log.i(TAG, "comu_by_user.doMenuItem(), user not registered.");
                makeToast(activity, R.string.user_without_signedUp);
            } else {
                Log.i(TAG, "comu_by_user.doMenuItem(), user registered.");
                Intent intent = new Intent(activity, ComusByUserListAc.class);
                activity.startActivity(intent);
            }
        }
    },

    REG_COMU_USER_USERCOMU_AC { // Menu: nueva comunidad.
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "reg_comu_user_usercomu.doMenuItem()");

            if (!isRegisteredUser(activity)) {
                Log.i(TAG, "reg_comu_user_usercomu.doMenuItem(), user not registered.");
                // Activity with user and comunidad data.
                Intent intent = new Intent(activity, RegComuAndUserAndUserComuAc.class);
                activity.startActivity(intent);
            } else {
                Log.i(TAG, "reg_comu_user_usercomu.doMenuItem(), user registered.");
                // Activity without user data: password, alias, email, telephone,..
                Intent intent = new Intent(activity, RegComuAndUserComuAc.class);
                activity.startActivity(intent);
            }

        }
    };

    private static final String TAG = UserMenu.class.getCanonicalName();

    public abstract void doMenuItem(Activity activity);
}