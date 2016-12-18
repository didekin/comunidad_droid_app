package com.didekindroid.comunidad;

import android.app.Activity;
import android.content.Intent;

import com.didekinaar.utils.ItemMenuIf;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:04
 */

public enum ComunidadMenu implements ItemMenuIf {

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
    },;
}
