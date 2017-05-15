package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.ViewerIncidRegEdit;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.router.ComponentReplacerIf;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 11:59
 */
@SuppressWarnings("WeakerAccess")
public class ViewerIncidRegAc extends ViewerIncidRegEdit implements ViewerParentInjectedIf,
        ComponentReplacerIf {

    ViewerFirebaseTokenIf viewerFirebaseToken;
    // Since initialization depends on fragment lifecycle, it is done in the activity and fragment through the ViewerParentInjectorIf interface.
    ViewerIncidRegFr viewerIncidRegFr;

    public ViewerIncidRegAc(IncidRegAc activity)
    {
        super(activity.acView, activity, null);
    }

    static ViewerIncidRegAc newViewerIncidRegAc(IncidRegAc activity)
    {
        Timber.d("newViewerIncidRegAc()");
        ViewerIncidRegAc instance = new ViewerIncidRegAc(activity);
        instance.viewerFirebaseToken = newViewerFirebaseToken(activity);
        instance.setController(new CtrlerIncidRegEditFr(instance));
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
        viewerIncidRegFr.saveState(savedState);
        viewerFirebaseToken.saveState(savedState);
    }

    @Override
    public void onSuccessRegisterIncidImportancia(int rowInserted)
    {
        Timber.d("onSuccessRegisterIncidImportancia()");
        replaceComponent(new Bundle());
    }

    boolean registerIncidencia(@Nullable IncidImportancia incidImportancia, @NonNull StringBuilder errorMsg)
    {
        Timber.d("registerIncidImportancia()");
        if (incidImportancia == null) {
            makeToast(activity, errorMsg);
            return false;
        } else {
            return checkInternetConnected(activity) && controller.registerIncidImportancia(incidImportancia);
        }
    }

    // ==================================  ViewerParentInjectedIf  =================================

    @Override
    public void setChildViewer(@NonNull ViewerIf childViewer)
    {
        Timber.d("setChildViewer()");
        viewerIncidRegFr = ViewerIncidRegFr.class.cast(childViewer);
    }

    // ==================================  ComponentReplacerIf  =================================

    @Override
    public void replaceComponent(@NonNull Bundle bundle)
    {
        Timber.d("replaceComponent()");
        new ActivityInitiator(activity).initActivityWithBundle(bundle);
    }

//  ................................... HELPERS ......................................

    @SuppressWarnings("WeakerAccess")
    class RegButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v)
        {
            Timber.d("View.OnClickListener().onClickLinkToImportanciaUsers()");
            StringBuilder errorMsg = getErrorMsgBuilder(activity);
            IncidImportancia incidImportancia = viewerIncidRegFr.doIncidImportanciaFromView(errorMsg);
            registerIncidencia(incidImportancia, errorMsg);
        }
    }
}
