package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.ParentViewerInjectedIf;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.view.View.GONE;
import static com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidencia_should_be_deleted;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.usercomu_should_have_admAuthority;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;

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

    private ViewerIncidEditMaxFr(View view, AppCompatActivity activity, ParentViewerInjectedIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerIncidEditMaxFr newViewerIncidEditMaxFr(@NonNull View frView, @NonNull ParentViewerInjectedIf parentViewer)
    {
        Timber.d("newViewerIncidEditMaxFr()");

        AppCompatActivity activity = parentViewer.getActivity();
        ViewerIncidEditMaxFr instance = new ViewerIncidEditMaxFr(frView, activity, parentViewer);
        instance.viewerAmbitoIncidSpinner =
                newViewerAmbitoIncidSpinner(frView.findViewById(R.id.incid_reg_ambito_spinner), activity, instance);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner(frView.findViewById(R.id.incid_reg_importancia_spinner), activity, instance);
        instance.setController(new CtrlerIncidRegEditFr());
        return instance;
    }

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
            controller.eraseIncidencia(new EraseIncidenciaObserver(), incidImportancia.getIncidencia());
        }
    }

    void onSuccessEraseIncidencia(int rowsDeleted)
    {
        Timber.d("onSuccessEraseIncidencia()");
        assertTrue(rowsDeleted == 1, incidencia_should_be_deleted);
        new ActivityInitiator(activity).initAcWithBundle(new Bundle(0));
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

        @Override
        public void onSuccess(Integer rowDeleted)
        {
            Timber.d("onSuccess()");
            onSuccessEraseIncidencia(rowDeleted);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }
    }
}
