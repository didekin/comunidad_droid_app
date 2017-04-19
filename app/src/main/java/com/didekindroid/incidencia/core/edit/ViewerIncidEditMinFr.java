package com.didekindroid.incidencia.core.edit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinner;
import com.didekindroid.incidencia.core.ViewerIncidRegEdit;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
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
final class ViewerIncidEditMinFr extends ViewerIncidRegEdit implements
        LinkToImportanciaUsersClickable {

    IncidImportancia incidImportancia;
    IncidImportanciaBean incidImportanciaBean;
    ViewerImportanciaSpinner viewerImportanciaSpinner;

    private ViewerIncidEditMinFr(View view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerIncidEditMinFr newViewerIncidEditMinFr(@NonNull View frView, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerIncidEditMaxFr()");

        Activity activity = parentViewer.getActivity();
        ViewerIncidEditMinFr instance = new ViewerIncidEditMinFr(frView, activity, parentViewer);

        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner((Spinner) frView.findViewById(R.id.incid_reg_importancia_spinner), activity, instance);
        instance.setController(new CtrlerIncidRegEditFr(instance));
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, @NonNull Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        incidImportancia = IncidImportancia.class.cast(viewBean);
        doViewBeans();

        viewerImportanciaSpinner.doViewInViewer(savedState, incidImportanciaBean);

        ((TextView) view.findViewById(R.id.incid_comunidad_txt)).setText(incidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((TextView) view.findViewById(R.id.incid_reg_desc_txt)).setText(incidImportancia.getIncidencia().getDescripcion());
        ((TextView) view.findViewById(R.id.incid_ambito_view))
                .setText(controller.getAmbitoIncidDesc(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));

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
    }

    private void doViewBeans()
    {
        Timber.d("doViewBeans()");
        incidImportanciaBean = new IncidImportanciaBean();
        incidImportanciaBean.setImportancia(incidImportancia.getImportancia());
    }

    @Override
    public void onClickLinkToImportanciaUsers(LinkToImportanciaUsersListener listener)
    {
        Timber.d("onClickLinkImportanciaUsers()");
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(INCIDENCIA_OBJECT.key, incidImportancia.getIncidencia());
        new ActivityInitiator(activity).initActivityFromListener(bundle, listener);
    }

    void onClickButtonModify()
    {
        Timber.d("onClickButtonModify()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());

        try {
            IncidImportancia newIncidImportancia = incidImportanciaBean.makeIncidImportancia(
                    errorMsg,
                    activity.getResources(),
                    incidImportancia
            );
            if (checkInternetConnected(getActivity())) {
                if (incidImportancia.getImportancia() == 0) {
                    // New IncidImportancia instance to be persisted.
                    controller.registerIncidImportancia(newIncidImportancia);
                } else {
                    controller.modifyIncidImportancia(newIncidImportancia);
                }
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(getActivity(), errorMsg.toString());
        }
    }

    @Override
    public void onSuccessRegisterIncidImportancia(int rowInserted)
    {
        Timber.d("onSuccessRegisterIncidImportancia()");
        new ActivityInitiator(activity).initActivityWithBundle(new Bundle());
    }

    public void onSuccessModifyIncidImportancia(int rowInserted)
    {
        Timber.d("onSuccessModifyIncidImportancia()");
        assertTrue(rowInserted >= 1, incid_importancia_should_be_modified);
        new ActivityInitiator(activity).initActivityWithBundle(new Bundle(0));
    }

    @SuppressWarnings("ConstantConditions")
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
}