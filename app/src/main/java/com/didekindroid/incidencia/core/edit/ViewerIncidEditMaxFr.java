package com.didekindroid.incidencia.core.edit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinner;
import com.didekindroid.incidencia.core.ViewerIncidRegEdit;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import timber.log.Timber;

import static android.view.View.GONE;
import static com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_modified;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidencia_should_be_deleted;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.usercomu_should_have_admAuthority;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
class ViewerIncidEditMaxFr extends ViewerIncidRegEdit implements
        LinkToImportanciaUsersClickable {

    final boolean hasResolucion;
    IncidImportancia incidImportancia;
    IncidenciaBean incidenciaBean;
    IncidImportanciaBean incidImportanciaBean;
    ViewerAmbitoIncidSpinner viewerAmbitoIncidSpinner;
    ViewerImportanciaSpinner viewerImportanciaSpinner;

    @SuppressWarnings("WeakerAccess")
    public ViewerIncidEditMaxFr(View view, Activity activity, ViewerIf parentViewer, boolean flagResolucion)
    {
        super(view, activity, parentViewer);
        hasResolucion = flagResolucion;
    }

    static ViewerIncidEditMaxFr newViewerIncidEditMaxFr(@NonNull View frView, @NonNull ViewerIf parentViewer, boolean flagResolucion)
    {
        Timber.d("newViewerIncidEditMaxFr()");

        Activity activity = parentViewer.getActivity();
        ViewerIncidEditMaxFr instance = new ViewerIncidEditMaxFr(frView, activity, parentViewer, flagResolucion);

        instance.viewerAmbitoIncidSpinner =
                newViewerAmbitoIncidSpinner((Spinner) frView.findViewById(R.id.incid_reg_ambito_spinner), activity, instance);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner((Spinner) frView.findViewById(R.id.incid_reg_importancia_spinner), activity, instance);
        instance.setController(new CtrlerIncidRegEditFr(instance));
        return instance;
    }

    /**
     * Preconditions:
     * 1. An incidencia with resolucion is not allowed to be erased.
     * 2. An incidencia can be erased by a user with adm function.
     */
    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        incidImportancia = IncidImportancia.class.cast(viewBean);
        doViewBeans();

        viewerAmbitoIncidSpinner.doViewInViewer(savedState, incidenciaBean);
        viewerImportanciaSpinner.doViewInViewer(savedState, incidImportanciaBean);

        ((TextView) view.findViewById(R.id.incid_comunidad_txt)).setText(incidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((EditText) view.findViewById(R.id.incid_reg_desc_ed)).setText(incidImportancia.getIncidencia().getDescripcion());

        TextView linkToImportanciaUsersView = (TextView) view.findViewById(R.id.incid_importancia_otros_view);
        linkToImportanciaUsersView.setOnClickListener(new LinkToImportanciaUsersListener(this));

        Button buttonModify = (Button) view.findViewById(R.id.incid_edit_fr_modif_button);
        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("onClickLinkToImportanciaUsers()");
                onClickButtonModify();
            }
        });

        Button buttonErase = (Button) view.findViewById(R.id.incid_edit_fr_borrar_button);
        if (hasResolucion || !incidImportancia.getUserComu().hasAdministradorAuthority()) {
            buttonErase.setVisibility(GONE);
            view.findViewById(R.id.incid_edit_fr_borrar_txt).setVisibility(GONE);
        } else {
            buttonErase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Timber.d("mButtonErase.onClickLinkToImportanciaUsers()");
                    onClickButtonErase();
                }
            });
        }
    }

    /**
     * This method initialize the spinners' beans with their initial values.
     */
    @SuppressWarnings("WeakerAccess")
    void doViewBeans()
    {
        Timber.d("doViewBeans()");
        incidenciaBean = new IncidenciaBean();
        incidImportanciaBean = new IncidImportanciaBean();
        incidenciaBean.setCodAmbitoIncid(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId());
        incidenciaBean.setComunidadId(incidImportancia.getIncidencia().getComunidadId());
        incidImportanciaBean.setImportancia(incidImportancia.getImportancia());
    }

    public void onClickLinkToImportanciaUsers(LinkToImportanciaUsersListener listener)
    {
        Timber.d("LinkToImportanciaUsersListener.onClickLinkToImportanciaUsers()");
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(INCIDENCIA_OBJECT.key, incidImportancia.getIncidencia());
        new ActivityInitiator(activity).initActivityFromListener(bundle, listener);
    }

    @SuppressWarnings("WeakerAccess")
    void onClickButtonModify()
    {
        Timber.d("onClickButtonModify()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());

        try {
            IncidImportancia newIncidImportancia = incidImportanciaBean.makeIncidImportancia(
                    errorMsg,
                    activity.getResources(),
                    view,
                    incidenciaBean,
                    incidImportancia.getIncidencia()
            );
            if (checkInternetConnected(getActivity())) {
                controller.modifyIncidImportancia(newIncidImportancia);
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(getActivity(), errorMsg.toString());
        }
    }

    @SuppressWarnings("WeakerAccess")
    void onClickButtonErase()
    {
        Timber.d("onClickButtonErase()");
        assertTrue(incidImportancia.getUserComu().hasAdministradorAuthority(), usercomu_should_have_admAuthority);

        if (checkInternetConnected(getActivity())) {
            controller.eraseIncidencia(incidImportancia.getIncidencia());
        }
    }

    public void onSuccessModifyIncidImportancia(int rowInserted)
    {
        Timber.d("onSuccessModifyIncidImportancia()");
        assertTrue(rowInserted >= 1, incid_importancia_should_be_modified);
        new ActivityInitiator(activity).initActivityWithBundle(new Bundle(0));
    }

    public void onSuccessEraseIncidencia(int rowsDeleted)
    {
        Timber.d("onSuccessEraseIncidencia()");
        assertTrue(rowsDeleted == 1, incidencia_should_be_deleted);
        new ActivityInitiator(activity).initActivityWithBundle(new Bundle(0));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return getController().clearSubscriptions()
                + viewerImportanciaSpinner.clearSubscriptions()
                + viewerAmbitoIncidSpinner.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        viewerAmbitoIncidSpinner.saveState(savedState);
        viewerImportanciaSpinner.saveState(savedState);
    }

    // ==================================== HELPERS ======================================

}
