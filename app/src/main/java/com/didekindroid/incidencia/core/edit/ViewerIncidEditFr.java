package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinner;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_modified;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
abstract class ViewerIncidEditFr extends Viewer<View, CtrlerIncidRegEditFr> implements
        LinkToImportanciaUsersClickable, ModIncidImportanciaCallableBack {

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

    @SuppressWarnings("WeakerAccess")
    protected ViewerIncidEditFr(View view, AppCompatActivity activity, ViewerIf parentViewer)
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


        TextView linkToImportanciaUsersView = view.findViewById(R.id.incid_importancia_otros_view);
        linkToImportanciaUsersView.setOnClickListener(new LinkToImportanciaUsersListener(this));

        Button buttonModify = view.findViewById(R.id.incid_edit_fr_modif_button);
        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("onClickLinkToImportanciaUsers()");
                onClickButtonModify();
            }
        });
    }

    public void onClickLinkToImportanciaUsers(LinkToImportanciaUsersListener listener)
    {
        Timber.d("LinkToImportanciaUsersListener.onClickLinkToImportanciaUsers()");
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(INCIDENCIA_OBJECT.key, resolBundle.getIncidImportancia().getIncidencia());
        new ActivityInitiator(activity).initAcFromListener(bundle, listener.getClass());
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
                controller.modifyIncidImportancia(new ModIncidImportanciaObserver<>(this), newIncidImportancia);
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(getActivity(), errorMsg.toString());
        }
    }

    @Override
    public void onSuccessModifyIncidImportancia(int rowInserted)
    {
        Timber.d("onSuccessModifyIncidImportancia()");
        assertTrue(rowInserted >= 1, incid_importancia_should_be_modified);
        new ActivityInitiator(activity).initAcWithBundle(new Bundle(0));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return getController().clearSubscriptions()
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
