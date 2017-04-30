package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.didekindroid.api.Viewer;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
public class ViewerIncidEditAc extends Viewer<View, CtrlerIncidEditAc> {

    private IncidImportancia incidImportancia;

    ViewerIncidEditAc(IncidEditAc activity)
    {
        super(activity.acView, activity, null);
    }

    static ViewerIncidEditAc newViewerIncidEditAc(IncidEditAc activity, View frView)
    {
        Timber.d("newViewerIncidEditAc()");
        ViewerIncidEditAc instance = new ViewerIncidEditAc(activity);
        instance.setController(new CtrlerIncidEditAc(instance));
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        // Preconditions.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        incidImportancia = IncidImportancia.class.cast(viewBean);
    }

    boolean checkResolucion(int resourceIdItemMn)
    {
        Timber.d("checkResolucion()");
        return controller.seeResolucion(incidImportancia.getIncidencia().getIncidenciaId(), resourceIdItemMn);
    }

    void onSuccessSeeResolucion(Resolucion resolucion, int resourceIdItemMn)
    {
        Timber.d("onSuccessSeeResolucion()");
        Intent intent0 = new Intent();
        intent0.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        if (resolucion != null) {
            intent0.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        }
        activity.setIntent(intent0);
        new ActivityInitiator(activity).initActivityFromMn(resourceIdItemMn);
    }
}
