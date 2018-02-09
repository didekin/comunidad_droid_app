package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.lib_one.api.ParentViewerInjected;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.LeadRouter.afterRegComuAndUserComu;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.user_and_comunidad_should_be_registered;

/**
 * User: pedro@didekin
 * Date: 01/06/17
 * Time: 14:31
 */

final class ViewerRegComuUserComuAc extends ParentViewerInjected<View, CtrlerUsuarioComunidad> {

    private ViewerRegComuUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
    }

    static ViewerRegComuUserComuAc newViewerRegComuUserComuAc(RegComuAndUserComuAc activity)
    {
        Timber.d("newViewerRegComuUserComuAc()");
        ViewerRegComuUserComuAc instance = new ViewerRegComuUserComuAc(activity.acView, activity);
        instance.setController(new CtrlerUsuarioComunidad());
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        Button mRegistroButton = view.findViewById(R.id.reg_comu_usuariocomunidad_button);
        mRegistroButton.setOnClickListener(new RegComuAndUserComuButtonListener());
    }

    // ==================================  HELPERS =================================

    @SuppressWarnings("WeakerAccess")
    class RegComuAndUserComuButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            Comunidad comunidadFromViewer = getChildViewer(ViewerRegComuFr.class).getComunidadFromViewer(errorBuilder);
            UsuarioComunidad usuarioComunidad =
                    comunidadFromViewer != null ?
                            getChildViewer(ViewerRegUserComuFr.class).getUserComuFromViewer(errorBuilder, comunidadFromViewer, null) :
                            null;

            if (usuarioComunidad == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                controller.registerUserComuAndComu(new RegComuAndUserComuObserver(), usuarioComunidad);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class RegComuAndUserComuObserver extends DisposableSingleObserver<Boolean> {

        @Override
        public void onSuccess(Boolean rowInserted)
        {
            Timber.d("onSuccess()");
            assertTrue(rowInserted, user_and_comunidad_should_be_registered);
            afterRegComuAndUserComu.initActivity(activity);
            dispose();
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
            dispose();
        }
    }
}
