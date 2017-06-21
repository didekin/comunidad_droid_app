package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekindroid.api.ViewerParent;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.ViewerRegUserFr;
import com.didekindroid.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 03/06/17
 * Time: 10:37
 */

final class ViewerRegUserAndUserComuAc extends ViewerParent<View, CtrlerUsuarioComunidad> {

    private ViewerRegUserAndUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
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
        TextView nombreComunidad = (TextView) view.findViewById(R.id.descripcion_comunidad_text);
        nombreComunidad.setText(comunidad.getNombreComunidad());
        Button registroButton = (Button) view.findViewById(R.id.reg_user_usercomu_button);
        registroButton.setOnClickListener(new RegUserAndUserComuButtonListener(comunidad));
    }

    // ==================================  HELPERS =================================

    void onRegisterSuccess(long c_id)
    {
        Timber.d("onRegisterSuccess()");
        Bundle bundle = new Bundle(1);
        bundle.putLong(COMUNIDAD_ID.key, c_id);
        new ActivityInitiator(activity).initAcWithBundle(bundle);
    }

    @SuppressWarnings("WeakerAccess")
    class RegUserAndUserComuButtonListener implements View.OnClickListener {

        final Comunidad comunidadIntent;

        public RegUserAndUserComuButtonListener(Comunidad comunidadIntent)
        {
            this.comunidadIntent = comunidadIntent;
        }

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            Usuario usuarioFromViewer = getChildViewer(ViewerRegUserFr.class).getUserFromViewer(errorBuilder);
            UsuarioComunidad usuarioComunidad = getChildViewer(ViewerRegUserComuFr.class).getUserComuFromViewer(errorBuilder, comunidadIntent, usuarioFromViewer);

            if (usuarioComunidad == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                controller.registerUserAndUserComu(
                        new ObserverCacheCleaner(controller) {
                            @Override
                            public void onComplete()
                            {
                                Timber.d("onComplete()");
                                onRegisterSuccess(comunidadIntent.getC_Id());
                            }
                        },
                        usuarioComunidad);
            }
        }
    }
}
