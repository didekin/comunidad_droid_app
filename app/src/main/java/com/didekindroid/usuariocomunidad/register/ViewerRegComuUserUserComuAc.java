package com.didekindroid.usuariocomunidad.register;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.lib_one.api.ObserverCacheCleaner;
import com.didekindroid.lib_one.api.ParentViewerInjected;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekindroid.lib_one.api.exception.UiExceptionRouterIf;
import com.didekindroid.usuario.ViewerRegUserFr;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.UiExceptionRouter.uiException_router;

/**
 * User: pedro@didekin
 * Date: 22/05/17
 * Time: 11:59
 */

public final class ViewerRegComuUserUserComuAc extends ParentViewerInjected<View, CtrlerUsuarioComunidad> {

    static ViewerRegComuUserUserComuAc newViewerRegComuUserUserComuAc(RegComuAndUserAndUserComuAc activity)
    {
        Timber.d("newViewerRegComuUserUserComuAc()");
        ViewerRegComuUserUserComuAc instance = new ViewerRegComuUserUserComuAc(activity.acView, activity);
        instance.setController(new CtrlerUsuarioComunidad());
        // We initialize each fragment viewer in its associated fragment.
        return instance;
    }

    private ViewerRegComuUserUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
    }

    // ==================================== ViewerIf ====================================

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return uiException_router;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        Button mRegistroButton = view.findViewById(R.id.reg_user_plus_button);
        mRegistroButton.setOnClickListener(new RegComuUserUserComuBtonListener());
    }

    // ==================================  HELPERS =================================

    void onRegisterSuccess(UsuarioComunidad userComu)
    {
        Timber.d("onRegisterSuccess()");
        DialogFragment newFragment = PasswordSentDialog.newInstance(userComu.getUsuario());
        newFragment.show(activity.getFragmentManager(), "passwordMailDialog");
    }

    @SuppressWarnings("WeakerAccess")
    class RegComuUserUserComuBtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            Comunidad comunidadFromViewer = getChildViewer(ViewerRegComuFr.class).getComunidadFromViewer(errorBuilder);
            Usuario usuarioFromViewer = getChildViewer(ViewerRegUserFr.class).getUserFromViewer(errorBuilder);
            final UsuarioComunidad usuarioComunidad = comunidadFromViewer != null && usuarioFromViewer != null ?
                    getChildViewer(ViewerRegUserComuFr.class).getUserComuFromViewer(errorBuilder, comunidadFromViewer, usuarioFromViewer) :
                    null;

            if (usuarioComunidad == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                controller.registerUserAndComu(
                        new ObserverCacheCleaner(ViewerRegComuUserUserComuAc.this) {
                            @Override
                            public void onComplete()
                            {
                                super.onComplete();
                                onRegisterSuccess(usuarioComunidad);
                            }
                        },
                        usuarioComunidad);
            }
        }
    }
}
