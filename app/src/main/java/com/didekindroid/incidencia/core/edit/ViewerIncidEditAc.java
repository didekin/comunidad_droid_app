package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.view.View;

import com.didekindroid.api.ParentViewerInjected;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.usuario.UsuarioAssertionMsg;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableMaybeObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.editResolucion;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.regResolucion;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
class ViewerIncidEditAc extends ParentViewerInjected<View, CtrlerIncidenciaCore> implements ActivityInitiatorIf {

    IncidAndResolBundle resolBundle;

    ViewerIncidEditAc(IncidEditAc activity)
    {
        super(activity.acView, activity);
    }

    static ViewerIncidEditAc newViewerIncidEditAc(IncidEditAc activity)
    {
        Timber.d("newViewerIncidEditAc()");
        ViewerIncidEditAc instance = new ViewerIncidEditAc(activity);
        instance.setController(new CtrlerIncidenciaCore());
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

    void checkResolucion()
    {
        Timber.d("checkResolucion()");
        assertTrue(controller.isRegisteredUser(), UsuarioAssertionMsg.user_should_be_registered);
        controller.seeResolucion(
                new ResolucionObserver(),
                resolBundle.getIncidImportancia().getIncidencia().getIncidenciaId());
    }

    // .................................... HELPERS .................................

    @SuppressWarnings("WeakerAccess")
    class ResolucionObserver extends DisposableMaybeObserver<Resolucion> {

        @Override
        public void onSuccess(@NonNull Resolucion resolucion)
        {
            Timber.d("onSuccess()");
            Bundle bundle = new Bundle(1);
            bundle.putSerializable(INCID_IMPORTANCIA_OBJECT.key, resolBundle.getIncidImportancia());
            bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
            for (ViewerIncidEditFr child : getChildViewersFromSuperClass(ViewerIncidEditFr.class)) {
                child.setHasResolucion();
            }
            initAcFromRouter(bundle, editResolucion);
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
            Bundle bundle = new Bundle(1);
            bundle.putSerializable(INCID_IMPORTANCIA_OBJECT.key, resolBundle.getIncidImportancia());
            initAcFromRouter(bundle, regResolucion);
        }
    }
}
