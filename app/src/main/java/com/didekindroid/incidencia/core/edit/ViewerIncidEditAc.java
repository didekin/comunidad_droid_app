package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.view.View;

import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekindroid.lib_one.util.CommonAssertionMsg;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableMaybeObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidContextualName.to_edit_incid_resolucion;
import static com.didekindroid.incidencia.IncidContextualName.to_register_new_incid_resolucion;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
final class ViewerIncidEditAc extends ParentViewer<View, CtrlerIncidenciaCore> {

    IncidAndResolBundle resolBundle;

    private ViewerIncidEditAc(IncidEditAc activity)
    {
        super(activity.acView, activity, null);
    }

    static ViewerIncidEditAc newViewerIncidEditAc(IncidEditAc activity)
    {
        Timber.d("newViewerIncidEditAc()");
        ViewerIncidEditAc instance = new ViewerIncidEditAc(activity);
        instance.setController(new CtrlerIncidenciaCore());
        return instance;
    }

    // .................................... ViewerIf .................................

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
        assertTrue(controller.isRegisteredUser(), CommonAssertionMsg.user_should_be_registered);
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
            getContextualRouter().getActionFromContextNm(to_edit_incid_resolucion).initActivity(activity, bundle);
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
            getContextualRouter().getActionFromContextNm(to_register_new_incid_resolucion)
                    .initActivity(activity, bundle);
        }
    }
}
