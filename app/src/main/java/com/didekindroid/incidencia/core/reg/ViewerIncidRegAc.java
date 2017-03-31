package com.didekindroid.incidencia.core.reg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.ViewBean;
import com.didekindroid.api.Viewer;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.reg.ViewerIncidRegFr.newViewerIncidReg;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 11:59
 */
final class ViewerIncidRegAc extends Viewer<View, CtrlerIncidRegAc> {

    private ViewerIncidRegFr viewerIncidRegFr;
    private ViewerFirebaseTokenIf viewerFirebaseToken;

    private ViewerIncidRegAc(View view, Activity activity)
    {
        super(view, activity, null);
    }

    static ViewerIncidRegAc newViewerIncidRegAc(View view, Activity activity)
    {
        Timber.d("newViewerIncidRegAc()");
        ViewerIncidRegAc instance = new ViewerIncidRegAc(view, activity);
        instance.viewerIncidRegFr = newViewerIncidReg(view.findViewById(R.id.incid_reg_frg_layout), activity, instance);
        instance.viewerFirebaseToken = newViewerFirebaseToken(activity);
        instance.setController(new CtrlerIncidRegAc(instance));
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, ViewBean viewBean)
    {
        Timber.d("doViewInViewer()");
        viewerIncidRegFr.doViewInViewer(savedState, viewBean);
        viewerFirebaseToken.checkGcmTokenAsync();

        Button mRegisterButton = (Button) activity.findViewById(R.id.incid_reg_ac_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                registerIncidencia();
            }
        });
    }

    /**
     * ViewerIncidRegFr subscriptions are cleared by the life-cycle call-back methods in the fragment.
     */
    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return controller.clearSubscriptions()
                + viewerFirebaseToken.clearSubscriptions();
    }

    boolean registerIncidencia()
    {
        Timber.d("registerIncidencia()");

        final StringBuilder errorMsg = getErrorMsgBuilder(activity);
        IncidImportancia incidImportancia = viewerIncidRegFr.doIncidImportanciaFromView(errorMsg);
        if (incidImportancia == null) {
            makeToast(activity, errorMsg);
            return false;
        } else {
            return checkInternetConnected(activity) && controller.registerIncidencia(incidImportancia);
        }
    }
}
