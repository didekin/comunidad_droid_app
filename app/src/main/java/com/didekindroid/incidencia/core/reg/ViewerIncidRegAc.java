package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidContextualName.new_incidencia_just_registered;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 11:59
 */
public class ViewerIncidRegAc extends ParentViewer<View, CtrlerIncidenciaCore> {

    private ViewerIncidRegAc(IncidRegAc activity)
    {
        super(activity.acView, activity, null);
    }

    static ViewerIncidRegAc newViewerIncidRegAc(IncidRegAc activity)
    {
        Timber.d("newViewerIncidRegAc()");
        ViewerIncidRegAc instance = new ViewerIncidRegAc(activity);
        instance.setController(new CtrlerIncidenciaCore());
        // We initialize viewerIncidRegFr in its associated fragment.
        return instance;
    }

    // .............................. ViewerIf ..................................

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        // Preconditions.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        Button registerButton = activity.findViewById(R.id.incid_reg_ac_button);
        registerButton.setOnClickListener(new RegButtonOnClickListener());
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return controller.clearSubscriptions();
    }

    public void onSuccessRegisterIncidImportancia(Comunidad comunidad)
    {
        Timber.d("onSuccessRegisterIncidImportancia()");
        Bundle bundle = new Bundle(1);
        bundle.putLong(COMUNIDAD_ID.key, comunidad.getC_Id());
        bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
        getContextualRouter().getActionFromContextNm(new_incidencia_just_registered).initActivity(activity, bundle);
    }

    boolean registerIncidencia(@Nullable IncidImportancia incidImportancia, @NonNull StringBuilder errorMsg)
    {
        Timber.d("regIncidImportancia()");
        if (incidImportancia == null) {
            makeToast(activity, errorMsg);
            return false;
        } else {
            return checkInternetConnected(activity) &&
                    controller.regIncidImportancia(
                            new RegIncidImportanciaObserver<>(this, incidImportancia.getIncidencia().getComunidad()),
                            incidImportancia);
        }
    }

//  ................................... HELPERS ......................................

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
