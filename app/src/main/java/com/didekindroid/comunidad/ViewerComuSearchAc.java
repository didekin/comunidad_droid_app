package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekindroid.router.DidekinContextAction.showComuFound;

/**
 * User: pedro@didekin
 * Date: 16/05/17
 * Time: 14:21
 */

class ViewerComuSearchAc extends ParentViewer<View, Controller> {

    private ViewerComuSearchAc(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
    }

    static ViewerComuSearchAc newViewerComuSearch(ComuSearchAc activity)
    {
        Timber.d("newViewerComuSearch()");
        ViewerComuSearchAc instance = new ViewerComuSearchAc(activity.acView, activity);
        instance.setController(new Controller());
        // We initialize viewerRegComuFr in its associated fragment.
        return instance;
    }

    /* ==================================== ViewerIf ====================================*/

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        Timber.d("getExceptionRouter()");
        return routerInitializer.get().getExceptionRouter();
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        Button searchButton = view.findViewById(R.id.searchComunidad_Bton);
        searchButton.setOnClickListener(new ComuSearchButtonListener());
    }

    // ==================================  HELPERS =================================

    @SuppressWarnings("WeakerAccess")
    class ComuSearchButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");

            StringBuilder errorMsg = getErrorMsgBuilder(activity);
            Comunidad comunidadFromViewer = getChildViewer(ViewerRegComuFr.class).getComunidadFromViewer(errorMsg);
            if (comunidadFromViewer == null) {
                makeToast(activity, errorMsg.toString());
            } else if (!isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                Bundle bundle = new Bundle(1);
                bundle.putSerializable(COMUNIDAD_SEARCH.key, comunidadFromViewer);
                showComuFound.initActivity(activity, bundle);
            }
        }
    }
}
