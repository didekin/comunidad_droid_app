package com.didekindroid.comunidad;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.Controller;
import com.didekindroid.api.Viewer;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 21/06/17
 * Time: 11:29
 */

final class ViewerComuSearchResultAc extends Viewer<View, Controller> {

    private ViewerComuSearchResultAc(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
    }

    static ViewerComuSearchResultAc newViewerComuSearchResultAc(ComuSearchResultsAc activity)
    {
        ViewerComuSearchResultAc instance = new ViewerComuSearchResultAc(activity.acView, activity);
        instance.setController(new Controller());
        return instance;
    }

    // .............................. HELPERS ..................................

    /**
     * Option 'see_usercomu_by_user_ac_mn' is only visible if the user is registered.
     */
    @SuppressWarnings("WeakerAccess")
    void updateActivityMenu(Menu menu)
    {
        Timber.d("updateActivityMenu()");
        if(!controller.isRegisteredUser()){
            return;
        }
        MenuItem comuDataItem = menu.findItem(R.id.see_usercomu_by_user_ac_mn);
        comuDataItem.setVisible(true);
        comuDataItem.setEnabled(true);
    }
}
