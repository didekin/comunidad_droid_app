package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekindroid.lib_one.usuario.ViewerRegUserFr;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.router.UserContextName.new_comu_user_usercomu_just_registered;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 22/05/17
 * Time: 11:59
 */

public final class ViewerRegComuUserUserComuAc extends ParentViewer<View, CtrlerUsuarioComunidad> {

    private ViewerRegComuUserUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
    }

    static ViewerRegComuUserUserComuAc newViewerRegComuUserUserComuAc(RegComuAndUserAndUserComuAc activity)
    {
        Timber.d("newViewerRegComuUserUserComuAc()");
        ViewerRegComuUserUserComuAc instance = new ViewerRegComuUserUserComuAc(activity.acView, activity);
        instance.setController(new CtrlerUsuarioComunidad());
        // We initialize each fragment viewer in its associated fragment.
        return instance;
    }

    // ==================================== ViewerIf ====================================

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
        getContextualRouter().getActionFromContextNm(new_comu_user_usercomu_just_registered)
                .initActivity(activity, usuario_object.getBundleForKey(userComu.getUsuario()));
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
                controller.regComuAndUserAndUserComu(
                        new DisposableCompletableObserver() {
                            @Override
                            public void onComplete()
                            {
                                onRegisterSuccess(usuarioComunidad);
                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                onErrorInObserver(e);
                            }
                        },
                        usuarioComunidad);
            }
        }
    }
}
