package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.didekindroid.api.ParentViewerInjected;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableMaybeObserver;
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
class ViewerIncidEditAc extends ParentViewerInjected<View, CtrlerIncidEditAc> implements ActivityInitiatorIf {

    IncidAndResolBundle resolBundle;

    ViewerIncidEditAc(IncidEditAc activity)
    {
        super(activity.acView, activity);
    }

    static ViewerIncidEditAc newViewerIncidEditAc(IncidEditAc activity)
    {
        Timber.d("newViewerIncidEditAc()");
        ViewerIncidEditAc instance = new ViewerIncidEditAc(activity);
        instance.setController(new CtrlerIncidEditAc());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        // Preconditions.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        resolBundle = IncidAndResolBundle.class.cast(viewBean);
    }

    void checkResolucion(int resourceIdItemMn)
    {
        Timber.d("checkResolucion()");
        controller.seeResolucion(
                new ResolucionObserver(resourceIdItemMn),
                resolBundle.getIncidImportancia().getIncidencia().getIncidenciaId());
    }

    void onSuccessCheckResolucion(Resolucion resolucion, int resourceIdItemMn)
    {
        Timber.d("onSuccessCheckResolucion()");
        onAfterSeeResolucion(resolucion, resourceIdItemMn);
    }

    void onCompleteCheckResolucion(int resourceIdItemMn)
    {
        Timber.d("onCompleteCheckResolucion()");
        onAfterSeeResolucion(null, resourceIdItemMn);
    }

    // .................................... HELPERS .................................

    private void onAfterSeeResolucion(Resolucion resolucion, int resourceIdItemMn)
    {
        Timber.d("onAfterSeeResolucion()");

        Intent intent = new Intent();
        intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, resolBundle.getIncidImportancia());
        if (resolucion != null) {
            intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
            for (ViewerIncidEditFr child : getChildViewersFromSuperClass(ViewerIncidEditFr.class)) {
                child.setHasResolucion();
            }
        }
        activity.setIntent(intent);
        initAcFromMenu(resourceIdItemMn);
    }

    @SuppressWarnings("WeakerAccess")
    class ResolucionObserver extends DisposableMaybeObserver<Resolucion> {

        private final int idItemMenu;

        ResolucionObserver(int idItemMenu)
        {
            this.idItemMenu = idItemMenu;
        }

        @Override
        public void onSuccess(@NonNull Resolucion resolucion)
        {
            Timber.d("onSuccess()");
            onSuccessCheckResolucion(resolucion, idItemMenu);
        }

        @Override
        public void onError(@NonNull Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }

        @Override
        public void onComplete()
        {
            Timber.d("onComplete()");
            onCompleteCheckResolucion(idItemMenu);
        }
    }
}
