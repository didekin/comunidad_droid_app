package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.api.ParentViewerInjected;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.LeadRouter.afterRegNewIncid;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 11:59
 */
@SuppressWarnings("WeakerAccess")
public class ViewerIncidRegAc extends ParentViewerInjected<View, CtrlerIncidenciaCore> {

    ViewerFirebaseTokenIf viewerFirebaseToken;

    public ViewerIncidRegAc(IncidRegAc activity)
    {
        super(activity.acView, activity);
    }

    static ViewerIncidRegAc newViewerIncidRegAc(IncidRegAc activity)
    {
        Timber.d("newViewerIncidRegAc()");
        ViewerIncidRegAc instance = new ViewerIncidRegAc(activity);
        instance.viewerFirebaseToken = newViewerFirebaseToken(activity);
        instance.setController(new CtrlerIncidenciaCore());
        // We initialize viewerIncidRegFr in its associated fragment.
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        // Preconditions.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        viewerFirebaseToken.checkGcmTokenAsync();
        Button registerButton = activity.findViewById(R.id.incid_reg_ac_button);
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
        viewerFirebaseToken.saveState(savedState);
    }

    public void onSuccessRegisterIncidImportancia(Comunidad comunidad)
    {
        Timber.d("onSuccessRegisterIncidImportancia()");
        Bundle bundle = new Bundle(1);
        bundle.putLong(COMUNIDAD_ID.key, comunidad.getC_Id());
        bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
        afterRegNewIncid.initActivity(activity, bundle);
    }

    boolean registerIncidencia(@Nullable IncidImportancia incidImportancia, @NonNull StringBuilder errorMsg)
    {
        Timber.d("registerIncidImportancia()");
        if (incidImportancia == null) {
            makeToast(activity, errorMsg);
            return false;
        } else {
            return checkInternetConnected(activity) &&
                    controller.registerIncidImportancia(
                            new RegIncidImportanciaObserver<>(this, incidImportancia.getIncidencia().getComunidad()),
                            incidImportancia);
        }
    }

//  ................................... HELPERS ......................................

    @SuppressWarnings("WeakerAccess")
    class RegButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v)
        {
            Timber.d("View.OnClickListener().onClickLinkToImportanciaUsers()");
            StringBuilder errorMsg = getErrorMsgBuilder(activity);

            IncidImportancia incidImportancia = getChildViewer(ViewerIncidRegFr.class).doIncidImportanciaFromView(errorMsg);
            registerIncidencia(incidImportancia, errorMsg);
        }
    }
}
