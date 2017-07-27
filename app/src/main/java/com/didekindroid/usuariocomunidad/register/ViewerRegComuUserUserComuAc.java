package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekindroid.api.ViewerParent;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.ViewerRegUserFr;
import com.didekindroid.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 22/05/17
 * Time: 11:59
 */

public final class ViewerRegComuUserUserComuAc extends ViewerParent<View, CtrlerUsuarioComunidad> {

    private ViewerRegComuUserUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
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
        Button mRegistroButton = view.findViewById(R.id.reg_com_usuario_usuariocomu_button);
        mRegistroButton.setOnClickListener(new RegComuUserButtonListener());
    }

    // ==================================  HELPERS =================================

    @SuppressWarnings("WeakerAccess")
    void onRegisterSuccess()
    {
        Timber.d("onRegisterSuccess()");
        new ActivityInitiator(activity).initAcWithBundle(new Bundle(0));
    }

    @SuppressWarnings("WeakerAccess")
    class RegComuUserButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            Comunidad comunidadFromViewer = getChildViewer(ViewerRegComuFr.class).getComunidadFromViewer(errorBuilder);
            Usuario usuarioFromViewer = getChildViewer(ViewerRegUserFr.class).getUserFromViewer(errorBuilder);
            UsuarioComunidad usuarioComunidad = getChildViewer(ViewerRegUserComuFr.class).getUserComuFromViewer(errorBuilder, comunidadFromViewer, usuarioFromViewer);

            // Nullity for comunidad in controlled in UsuarioComunidad builder.
            if (usuarioComunidad == null || usuarioFromViewer == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                controller.registerUserAndComu(
                        new ObserverCacheCleaner(ViewerRegComuUserUserComuAc.this) {
                            @Override
                            public void onComplete()
                            {
                                Timber.d("onComplete()");
                                onRegisterSuccess();
                            }
                        },
                        usuarioComunidad);
            }
        }
    }
}
