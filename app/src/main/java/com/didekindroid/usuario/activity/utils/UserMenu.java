package com.didekindroid.usuario.activity.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.*;

import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.uiutils.UIutils.makeToast;

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
            Log.d(TAG,"comu_data_ac.doMenuItem()");
            activity.startActivity(activity.getIntent());
        }
    },

    COMU_SEARCH_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "comu_search_ac.doMenuItem()");
            Intent intent = new Intent(activity, ComuSearchAc.class);
            activity.startActivity(intent);
        }
    },

    DELETE_ME_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "delete_me_ac.doMenuItem()");
            Intent intent = new Intent(activity, DeleteMeAc.class);
            activity.startActivity(intent);
        }
    },

    PASSWORD_CHANGE_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "password_change_ac.doMenuItem()");
            Intent intent = new Intent(activity, PasswordChangeAc.class);
            activity.startActivity(intent);
        }
    },

    REG_COMU_USER_USERCOMU_AC { // Menu: Nueva comunidad.

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
    },

    REG_USER_AND_USERCOMU_AC { // Menú: Unirme (a la comunidad).

        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "reg_user_and_usercomu.doMenuItem()");
            if (isRegisteredUser(activity)) {
                Log.d(TAG, "reg_user_and_usercomu.doMenuItem(); user is registered.");
                Intent intent = new Intent(activity, RegUserComuAc.class);
                activity.startActivity(intent);
            } else {
                Log.d(TAG, "reg_user_and_usercomu.doMenuItem(); user is not registered.");
                Intent intent = new Intent(activity, RegUserAndUserComuAc.class);
            }
        }
    },

    SEE_USERCOMU_BY_COMU_AC {

        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "see_usercomu_by_comu_ac.doMenuItem()");
            activity.startActivity(activity.getIntent());
        }
    },

    SEE_USERCOMU_BY_USER_AC {  // Comunidades de un usuario: Mis comunidades.

        @Override
        public void doMenuItem(Activity activity)
        {
            Log.i(TAG, "comu_by_user.doMenuItem()");

            if (!isRegisteredUser(activity)) {
                Log.i(TAG, "comu_by_user.doMenuItem(), user not registered.");
                makeToast(activity, R.string.user_without_signedUp, Toast.LENGTH_SHORT);
            } else {
                Log.i(TAG, "comu_by_user.doMenuItem(), user registered.");
                Intent intent = new Intent(activity, SeeUserComuByUserAc.class);
                activity.startActivity(intent);
            }
        }
    },

    USER_DATA_AC {  // menú: Mi usuario.

        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "user_data_ac_mn.doMenuItem()");

            if (!isRegisteredUser(activity)) {
                Log.i(TAG, "user_data_ac_mn.doMenuItem(), user not registered.");
                makeToast(activity, R.string.user_without_signedUp, Toast.LENGTH_SHORT);
            } else {
                Log.i(TAG, "user_data_ac_mn.doMenuItem(), user registered.");
                Intent intent = new Intent(activity, UserDataAc.class);
                activity.startActivity(intent);
            }
        }
    },;

    private static final String TAG = UserMenu.class.getCanonicalName();

    public abstract void doMenuItem(Activity activity);
}