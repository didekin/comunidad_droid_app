package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinner;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.view.View.GONE;
import static com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_modified;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidencia_should_be_deleted;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.usercomu_should_have_admAuthority;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
class ViewerIncidEditMaxFr extends Viewer<View, CtrlerIncidRegEditFr> implements
        LinkToImportanciaUsersClickable, ModIncidImportanciaCallableBack {

    /**
     *  Its state is changed in two ways:
     *  1. when resolBundle.hasResolucion() is true.
     *  2. when activated the option menu to see/edit a resolucion in the parent activity.
     */
    AtomicBoolean hasResolucion = new AtomicBoolean(false);
    IncidAndResolBundle resolBundle;
    IncidenciaBean incidenciaBean;
    IncidImportanciaBean incidImportanciaBean;
    ViewerAmbitoIncidSpinner viewerAmbitoIncidSpinner;
    ViewerImportanciaSpinner viewerImportanciaSpinner;

    @SuppressWarnings("WeakerAccess")
    public ViewerIncidEditMaxFr(View view, AppCompatActivity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerIncidEditMaxFr newViewerIncidEditMaxFr(@NonNull View frView, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerIncidEditMaxFr()");

        AppCompatActivity activity = parentViewer.getActivity();
        ViewerIncidEditMaxFr instance = new ViewerIncidEditMaxFr(frView, activity, parentViewer);
        instance.viewerAmbitoIncidSpinner =
                newViewerAmbitoIncidSpinner((Spinner) frView.findViewById(R.id.incid_reg_ambito_spinner), activity, instance);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner((Spinner) frView.findViewById(R.id.incid_reg_importancia_spinner), activity, instance);
        instance.setController(new CtrlerIncidRegEditFr());
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

        resolBundle = IncidAndResolBundle.class.cast(viewBean);
        // HasResolucion can change only from false --> true.
        hasResolucion.compareAndSet(false, resolBundle.hasResolucion());
        IncidImportancia incidImportancia = resolBundle.getIncidImportancia();
        doViewBeans();

        viewerAmbitoIncidSpinner.doViewInViewer(savedState, incidenciaBean);
        viewerImportanciaSpinner.doViewInViewer(savedState, incidImportanciaBean);

        ((TextView) view.findViewById(R.id.incid_comunidad_txt)).setText(incidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((EditText) view.findViewById(R.id.incid_reg_desc_ed)).setText(incidImportancia.getIncidencia().getDescripcion());

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

        Button buttonErase = view.findViewById(R.id.incid_edit_fr_borrar_button);
        // Se puede borrar incidencia si no tiene resolución y el usuario tiene rol ADM o dio de alta la incidencia.
        boolean canUserEraseIncid =  !hasResolucion.get()
                && (incidImportancia.getUserComu().hasAdministradorAuthority() || incidImportancia.isIniciadorIncidencia());
        if (!canUserEraseIncid) {
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
        IncidImportancia incidImportancia = resolBundle.getIncidImportancia();

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
        bundle.putSerializable(INCIDENCIA_OBJECT.key, resolBundle.getIncidImportancia().getIncidencia());
        new ActivityInitiator(activity).initAcFromListener(bundle, listener.getClass());
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
                    resolBundle.getIncidImportancia().getIncidencia()
            );
            if (checkInternetConnected(getActivity())) {
                controller.modifyIncidImportancia(new ModIncidImportanciaObserver<>(this), newIncidImportancia);
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
        IncidImportancia incidImportancia = resolBundle.getIncidImportancia();
        assertTrue(incidImportancia.getUserComu().hasAdministradorAuthority() || incidImportancia.isIniciadorIncidencia(), usercomu_should_have_admAuthority);

        if (checkInternetConnected(getActivity())) {
            controller.eraseIncidencia(new EraseIncidenciaObserver(), incidImportancia.getIncidencia());
        }
    }

    @Override
    public void onSuccessModifyIncidImportancia(int rowInserted)
    {
        Timber.d("onSuccessModifyIncidImportancia()");
        assertTrue(rowInserted >= 1, incid_importancia_should_be_modified);
        new ActivityInitiator(activity).initAcWithBundle(new Bundle(0));
    }

    void onSuccessEraseIncidencia(int rowsDeleted)
    {
        Timber.d("onSuccessEraseIncidencia()");
        assertTrue(rowsDeleted == 1, incidencia_should_be_deleted);
        new ActivityInitiator(activity).initAcWithBundle(new Bundle(0));
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

    // =======================================  HELPERS  =======================================


    void setHasResolucion()
    {
        Timber.d("setHasResolucion()");
        hasResolucion.set(true);
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
