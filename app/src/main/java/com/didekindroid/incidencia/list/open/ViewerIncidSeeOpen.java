package com.didekindroid.incidencia.list.open;

import android.app.Activity;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.close.ViewerIncidSeeClose;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;

import timber.log.Timber;

import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 20/03/17
 * Time: 12:33
 */

class ViewerIncidSeeOpen extends ViewerIncidSeeClose {

    private ViewerFirebaseTokenIf viewerFirebaseToken;

    ViewerIncidSeeOpen(View frView, Activity activity)
    {
        super(frView, activity);
    }

    static ViewerIncidSeeOpen newViewerIncidSeeOpen(View view, Activity activity)
    {
        Timber.d("newViewerIncidSeeOpen()");
        ViewerIncidSeeOpen parentInstance = new ViewerIncidSeeOpen(view, activity);
        parentInstance.setController(new CtrlerIncidSeeOpenByComu(parentInstance));
        parentInstance.viewerFirebaseToken = newViewerFirebaseToken(activity);
        parentInstance.spinnerViewer = newViewerComuSpinner((Spinner) view.findViewById(R.id.incid_reg_comunidad_spinner), activity, parentInstance);
        parentInstance.viewerFirebaseToken.checkGcmTokenAsync();
        return parentInstance;
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        viewerFirebaseToken.clearSubscriptions();
        return super.clearSubscriptions();
    }
}
