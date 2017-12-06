package com.didekindroid.incidencia.list.open;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.close.ViewerIncidSeeCloseFr;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 20/03/17
 * Time: 12:33
 */

final class ViewerIncidSeeOpenFr extends ViewerIncidSeeCloseFr implements ActivityInitiatorIf {

    ViewerFirebaseTokenIf viewerFirebaseToken;

    private ViewerIncidSeeOpenFr(View frView, AppCompatActivity activity)
    {
        super(frView, activity);
    }

    static ViewerIncidSeeOpenFr newViewerIncidSeeOpen(View view, AppCompatActivity activity)
    {
        Timber.d("newViewerIncidSeeOpen()");
        ViewerIncidSeeOpenFr parentInstance = new ViewerIncidSeeOpenFr(view, activity);
        parentInstance.setController(new CtrlerIncidSeeOpenByComu());
        parentInstance.viewerFirebaseToken = newViewerFirebaseToken(activity);
        parentInstance.comuSpinnerViewer = newViewerComuSpinner(view.findViewById(R.id.incid_reg_comunidad_spinner), parentInstance);
        return parentInstance;
    }

    /* ==================================  ViewerSelectionIf  =================================*/

    @Override
    public void onSuccessLoadItemList(List<IncidenciaUser> itemsList)
    {
        Timber.d("onSuccessLoadItemList()");
        onSuccessLoadItems(itemsList, new AdapterIncidSeeOpenByComu(activity));
    }

    @Override
    public void onSuccessLoadSelectedItem(@NonNull Bundle bundle)
    {
        Timber.d("onSuccessLoadSelectedItem()");
        initAcFromActivity(bundle);
    }

    /* ==================================  VIEWER  =================================*/

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        super.doViewInViewer(savedState, viewBean);
        viewerFirebaseToken.checkGcmTokenAsync();
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        viewerFirebaseToken.clearSubscriptions();
        return super.clearSubscriptions();
    }

    // ==================================  HELPERS  =================================

    ViewerComuSpinner getComuSpinner()
    {
        Timber.d("getComuSpinner()");
        return comuSpinnerViewer;
    }

}
