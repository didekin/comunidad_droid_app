package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.ParentViewerInjected;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.security.CtrlerAuthToken;
import com.didekindroid.security.CtrlerAuthTokenIf;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 16/05/17
 * Time: 14:21
 */

class ViewerComuSearchAc extends ParentViewerInjected<View, CtrlerAuthTokenIf> implements ActivityInitiatorIf {

    ViewerComuSearchAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
    }

    static ViewerComuSearchAc newViewerComuSearch(ComuSearchAc activity)
    {
        Timber.d("newViewerComuSearch()");
        ViewerComuSearchAc instance = new ViewerComuSearchAc(activity.acView, activity);
        instance.setController(new CtrlerAuthToken());
        // We initialize viewerRegComuFr in its associated fragment.
        return instance;
    }

    /* ==================================== ViewerIf ====================================*/

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        // Check token cache.
        controller.refreshAccessToken(this);
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
                initAcFromActivity(bundle);
            }
        }
    }
}
