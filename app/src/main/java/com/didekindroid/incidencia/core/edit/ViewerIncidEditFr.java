package com.didekindroid.incidencia.core.edit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.incidencia.IncidBundleKey;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinner;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.incidencia.IncidenciaBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidContextualName.incid_open_just_modified;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
abstract class ViewerIncidEditFr extends Viewer<View, CtrlerIncidenciaCore> {

    IncidAndResolBundle resolBundle;
    IncidenciaBean incidenciaBean;
    IncidImportanciaBean incidImportanciaBean;
    ViewerImportanciaSpinner viewerImportanciaSpinner;
    /**
     * Its state is changed in two ways:
     * 1. when resolBundle.hasResolucion() is true.
     * 2. when activated the option menu to see/edit a resolucion in the parent activity.
     */
    AtomicBoolean hasResolucion = new AtomicBoolean(false);

    ViewerIncidEditFr(View view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        resolBundle = IncidAndResolBundle.class.cast(viewBean);
        // HasResolucion can change only from false --> true.
        hasResolucion.compareAndSet(false, resolBundle.hasResolucion());
        doViewBeans();

        viewerImportanciaSpinner.doViewInViewer(savedState, incidImportanciaBean);
        ((TextView) view.findViewById(R.id.incid_comunidad_txt)).setText(resolBundle.getIncidImportancia().getIncidencia().getComunidad().getNombreComunidad());

        Button buttonModify = view.findViewById(R.id.incid_edit_fr_modif_button);
        buttonModify.setOnClickListener(v -> onClickButtonModify());
    }

    @SuppressWarnings("WeakerAccess")
    void onClickButtonModify()
    {
        Timber.d("onClickButtonModify()");
        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        try {
            // Check first for a valid IncidImportancia instance; then check the internet connection.
            IncidImportancia newIncidImportancia = doNewIncidImportancia(errorMsg);
            if (checkInternetConnected(getActivity())) {
                controller.modifyIncidImportancia(
                        new ModIncidImportanciaObserver<>(this, newIncidImportancia.getIncidencia().getComunidad()),
                        newIncidImportancia);
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(getActivity(), errorMsg.toString());
        }
    }

    void onSuccessModifyIncidImportancia(Comunidad comunidad)
    {
        Timber.d("onSuccessModifyIncidImportancia()");
        Bundle bundle = new Bundle(1);
        bundle.putLong(COMUNIDAD_ID.key, comunidad.getC_Id());
        bundle.putBoolean(IncidBundleKey.INCID_CLOSED_LIST_FLAG.key, false);
        getContextualRouter().getActionFromContextNm(incid_open_just_modified).initActivity(getActivity(), bundle);
    }

    //    ============================  LIFE CYCLE   ===================================

    @SuppressWarnings("ConstantConditions")
    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return controller.clearSubscriptions()
                + viewerImportanciaSpinner.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        viewerImportanciaSpinner.saveState(savedState);
    }

    /* =======================================  HELPERS  =======================================*/

    /**
     * This method initialize beans with their initial values.
     */
    @SuppressWarnings("WeakerAccess")
    void doViewBeans()
    {
        Timber.d("doViewBeans()");
        IncidImportancia incidImportancia = resolBundle.getIncidImportancia();

        incidenciaBean = new IncidenciaBean();
        incidImportanciaBean = new IncidImportanciaBean();
        incidenciaBean.setCodAmbitoIncid(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId());
        incidenciaBean.setComunidadId(incidImportancia.getIncidencia().getComunidadId());
        incidImportanciaBean.setImportancia(incidImportancia.getImportancia());
    }

    /**
     * Method to be implemented in subclasses to provide a new IncidImportancia instance, with the modified data.
     */
    protected abstract IncidImportancia doNewIncidImportancia(StringBuilder errorMsg);

    void setHasResolucion()
    {
        Timber.d("setHasResolucion()");
        hasResolucion.set(true);
    }
}
