package com.didekindroid.usuariocomunidad;

import android.app.Activity;
import android.content.Intent;

import com.didekinaar.utils.ItemMenuIf;

import timber.log.Timber;

import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:05
 */

public enum UserComuMenu implements ItemMenuIf {

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
                makeToast(activity, com.didekinaar.R.string.user_without_signedUp);
            } else {
                Timber.i("comu_by_user.doMenuItem(), user registered.");
                Intent intent = new Intent(activity, SeeUserComuByUserAc.class);
                activity.startActivity(intent);
            }
        }
    },;
}
