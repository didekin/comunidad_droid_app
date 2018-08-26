package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.incidencia.spinner.ViewerAmbitoIncidSpinner;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.view.View.GONE;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidContextualName.incidencia_just_erased;
import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.lib_one.incidencia.spinner.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.usercomu_should_have_admAuthority;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 * <p>
 * Preconditions:
 * 1. An incidencia with resolucion is not allowed to be erased.
 * 2. An incidencia can be erased by a user with adm function.
 */
final class ViewerIncidEditMaxFr extends ViewerIncidEditFr {

    ViewerAmbitoIncidSpinner viewerAmbitoIncidSpinner;

    private ViewerIncidEditMaxFr(View view, ParentViewerIf parentViewer)
    {
        super(view, parentViewer.getActivity(), parentViewer);
    }

    static ViewerIncidEditMaxFr newViewerIncidEditMaxFr(@NonNull View frView, @NonNull ParentViewerIf parentViewer)
    {
        Timber.d("newViewerIncidEditMaxFr()");

        ViewerIncidEditMaxFr instance = new ViewerIncidEditMaxFr(frView, parentViewer);
        instance.viewerAmbitoIncidSpinner =
                newViewerAmbitoIncidSpinner(frView.findViewById(R.id.incid_reg_ambito_spinner), instance);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner(frView.findViewById(R.id.incid_reg_importancia_spinner), instance);
        instance.setController(new CtrlerIncidenciaCore());
        return instance;
    }

    // .............................. ViewerIf ..................................

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        super.doViewInViewer(savedState, viewBean);

        viewerAmbitoIncidSpinner.doViewInViewer(savedState, incidenciaBean);
        ((EditText) view.findViewById(R.id.incid_reg_desc_ed)).setText(resolBundle.getIncidImportancia().getIncidencia().getDescripcion());

        Button buttonErase = view.findViewById(R.id.incid_edit_fr_borrar_button);
        if (!canUserEraseIncidencia(resolBundle.getIncidImportancia())) {
            buttonErase.setVisibility(GONE);
        } else {
            buttonErase.setOnClickListener(v -> onClickButtonErase());
        }
    }

    @Override
    protected IncidImportancia doNewIncidImportancia(StringBuilder errorMsg)
    {
        Timber.d("doNewIncidImportancia()");
        return incidImportanciaBean.makeIncidImportancia(
                errorMsg,
                activity.getResources(),
                view,
                incidenciaBean,
                resolBundle.getIncidImportancia().getIncidencia()
        );
    }

    @SuppressWarnings("WeakerAccess")
    void onClickButtonErase()
    {
        Timber.d("onClickButtonErase()");
        IncidImportancia incidImportancia = resolBundle.getIncidImportancia();
        assertTrue(incidImportancia.getUserComu().hasAdministradorAuthority() || incidImportancia.isIniciadorIncidencia(), usercomu_should_have_admAuthority);

        if (checkInternetConnected(getActivity())) {
            controller.eraseIncidencia(new EraseIncidenciaObserver(incidImportancia.getIncidencia().getComunidad()), incidImportancia.getIncidencia());
        }
    }

    void onSuccessEraseIncidencia(Comunidad comunidad)
    {
        Timber.d("onSuccessEraseIncidencia()");
        Bundle bundle = new Bundle(1);
        bundle.putLong(COMUNIDAD_ID.key, comunidad.getC_Id());
        bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
        getContextualRouter().getActionFromContextNm(incidencia_just_erased).initActivity(getActivity(), bundle);
    }

    //    ============================  LIFE CYCLE   ===================================

    @SuppressWarnings("ConstantConditions")
    @Override
    public int clearSubscriptions()
    {
        return super.clearSubscriptions()
                + viewerAmbitoIncidSpinner.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        super.saveState(savedState);
        viewerAmbitoIncidSpinner.saveState(savedState);
    }

    // =======================================  HELPERS  =======================================

    boolean canUserEraseIncidencia(IncidImportancia incidImportancia)
    {
        Timber.d("canUserEraseIncidencia()");
        // Se puede borrar incidencia si no tiene resolución y el usuario tiene rol ADM o dio de alta la incidencia.
        return !hasResolucion.get()
                && (incidImportancia.getUserComu().hasAdministradorAuthority() || incidImportancia.isIniciadorIncidencia());
    }

    @SuppressWarnings("WeakerAccess")
    class EraseIncidenciaObserver extends DisposableSingleObserver<Integer> {

        private final Comunidad comunidad;

        public EraseIncidenciaObserver(Comunidad comunidad)
        {
            this.comunidad = comunidad;
        }

        @Override
        public void onSuccess(Integer rowDeleted)
        {
            Timber.d("onSuccess()");
            onSuccessEraseIncidencia(comunidad);
            getActivity().finish();
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }
    }
}
