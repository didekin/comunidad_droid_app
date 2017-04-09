package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.RootViewReplacer;
import com.didekindroid.api.RootViewReplacerIf;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 11:59
 */
class ViewerIncidRegAc extends Viewer<View, CtrlerIncidRegAc> implements RootViewReplacerIf {

    ViewerFirebaseTokenIf viewerFirebaseToken;
    @SuppressWarnings("WeakerAccess")
    ViewerIncidRegFr viewerIncidRegFr;

    ViewerIncidRegAc(IncidRegAc activity)
    {
        super(activity.acView, activity, null);
    }

    static ViewerIncidRegAc newViewerIncidRegAc(IncidRegAc activity)
    {
        Timber.d("newViewerIncidRegAc()");
        ViewerIncidRegAc instance = new ViewerIncidRegAc(activity);
        instance.viewerFirebaseToken = newViewerFirebaseToken(activity);
        instance.setController(new CtrlerIncidRegAc(instance));
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        viewerFirebaseToken.checkGcmTokenAsync();
        Button registerButton = (Button) activity.findViewById(R.id.incid_reg_ac_button);
        registerButton.setOnClickListener(new RegButtonOnClickListener());
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return controller.clearSubscriptions()
                + viewerFirebaseToken.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
    }

    @Override
    public void replaceRootView(@NonNull Bundle bundle)
    {
        Timber.d("replaceRootView()");
        new RootViewReplacer(activity).replaceRootView(bundle);
    }

    void setChildViewer(@NonNull ViewerIf childViewer)
    {
        Timber.d("setChildViewer()");
        viewerIncidRegFr = ViewerIncidRegFr.class.cast(childViewer);
    }

    boolean registerIncidencia(@Nullable IncidImportancia incidImportancia, @NonNull StringBuilder errorMsg)
    {
        Timber.d("registerIncidencia()");
        if (incidImportancia == null) {
            makeToast(activity, errorMsg);
            return false;
        } else {
            return checkInternetConnected(activity) && controller.registerIncidencia(incidImportancia);
        }
    }

    void onSuccessRegisterIncidencia(int rowInserted)
    {
        Timber.d("onSuccessRegisterIncidencia()");
        replaceRootView(new Bundle());
    }

//  ................................... HELPERS ......................................

    @SuppressWarnings("WeakerAccess")
    class RegButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v)
        {
            Timber.d("View.OnClickListener().onClick()");
            StringBuilder errorMsg = getErrorMsgBuilder(activity);
            IncidImportancia incidImportancia = viewerIncidRegFr.doIncidImportanciaFromView(errorMsg);
            registerIncidencia(incidImportancia, errorMsg);
        }
    }
}
