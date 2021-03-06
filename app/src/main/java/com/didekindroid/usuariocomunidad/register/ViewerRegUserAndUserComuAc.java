package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekindroid.lib_one.usuario.ViewerRegUserFr;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.router.UserContextName.new_user_usercomu_just_registered;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 03/06/17
 * Time: 10:37
 */

final class ViewerRegUserAndUserComuAc extends ParentViewer<View, CtrlerUsuarioComunidad> {

    private ViewerRegUserAndUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
    }

    static ViewerRegUserAndUserComuAc newViewerRegUserAndUserComuAc(RegUserAndUserComuAc activity)
    {
        Timber.d("newViewerRegUserAndUserComuAc()");
        ViewerRegUserAndUserComuAc instance = new ViewerRegUserAndUserComuAc(activity.acView, activity);
        instance.setController(new CtrlerUsuarioComunidad());
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        Comunidad comunidad = Comunidad.class.cast(viewBean);
        view.<TextView>findViewById(R.id.descripcion_comunidad_text).setText(comunidad.getNombreComunidad());
        Button registroButton = view.findViewById(R.id.reg_user_plus_button);
        registroButton.setOnClickListener(new RegUserAndUserComuButtonListener(comunidad));
    }

    // ==================================  HELPERS =================================

    void onRegisterSuccess(UsuarioComunidad userComu)
    {
        Timber.d("onRegisterSuccess()");
        getContextualRouter().getActionFromContextNm(new_user_usercomu_just_registered)
                .initActivity(activity, usuario_object.getBundleForKey(userComu.getUsuario()));
    }

    class RegUserAndUserComuButtonListener implements View.OnClickListener {

        final Comunidad comunidadIntent;

        RegUserAndUserComuButtonListener(Comunidad comunidadIntent)
        {
            this.comunidadIntent = comunidadIntent;
        }

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            final Usuario usuarioFromViewer = getChildViewer(ViewerRegUserFr.class).getUserFromViewer(errorBuilder);
            final UsuarioComunidad usuarioComunidad = usuarioFromViewer != null ?
                    getChildViewer(ViewerRegUserComuFr.class).getUserComuFromViewer(errorBuilder, comunidadIntent, usuarioFromViewer) :
                    null;

            if (usuarioComunidad == null) {
                makeToast(activity, errorBuilder.toString());
                return;
            }
            if (checkInternetConnected(activity)) {
                controller.regUserAndUserComu(
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
