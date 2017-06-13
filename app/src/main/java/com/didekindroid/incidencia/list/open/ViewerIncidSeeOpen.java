package com.didekindroid.incidencia.list.open;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.close.ViewerIncidSeeClose;
import com.didekindroid.router.ActivityInitiator;
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

class ViewerIncidSeeOpen extends ViewerIncidSeeClose {

    ViewerFirebaseTokenIf viewerFirebaseToken;

    ViewerIncidSeeOpen(AppCompatActivity activity)
    {
        super(new ListView(activity), activity);
    }

    private ViewerIncidSeeOpen(View frView, AppCompatActivity activity)
    {
        super(frView, activity);
    }

    static ViewerIncidSeeOpen newViewerIncidSeeOpen(View view, AppCompatActivity activity)
    {
        Timber.d("newViewerIncidSeeOpen()");
        ViewerIncidSeeOpen parentInstance = new ViewerIncidSeeOpen(view, activity);
        parentInstance.setController(new CtrlerIncidSeeOpenByComu());
        parentInstance.viewerFirebaseToken = newViewerFirebaseToken(activity);
        parentInstance.comuSpinnerViewer = newViewerComuSpinner((Spinner) view.findViewById(R.id.incid_reg_comunidad_spinner), activity, parentInstance);
        return parentInstance;
    }

    /* ==================================  ViewerSelectionIf  =================================*/

    @Override
    public void onSuccessLoadItemList(List<IncidenciaUser> itemsList)
    {
        Timber.d("onSuccessLoadItemList()");
        onSuccessLoadItems(itemsList, getNewViewAdapter());
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


    @Override
    public void replaceComponent(@NonNull Bundle bundle)
    {
        Timber.d("replaceComponent()");
        new ActivityInitiator(activity).initActivityWithBundle(bundle);
    }

    ViewerComuSpinner getComuSpinner()
    {
        Timber.d("getComuSpinner()");
        return comuSpinnerViewer;
    }

    @NonNull
    private ArrayAdapter<IncidenciaUser> getNewViewAdapter()
    {
        Timber.d("getNewViewAdapter()");
        return new AdapterIncidSeeOpenByComu(activity);
    }
}
