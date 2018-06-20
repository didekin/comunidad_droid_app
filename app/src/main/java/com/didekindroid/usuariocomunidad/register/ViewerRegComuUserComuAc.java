package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuContextualName.new_comu_usercomu_just_registered;
import static com.didekindroid.lib_one.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 01/06/17
 * Time: 14:31
 */

final class ViewerRegComuUserComuAc extends ParentViewer<View, CtrlerUsuarioComunidad> {

    private ViewerRegComuUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
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
            } else if (!isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                controller.regComuAndUserComu(new RegComuAndUserComuObserver(), usuarioComunidad);
            }
        }
    }

    class RegComuAndUserComuObserver extends DisposableCompletableObserver {

        @Override
        public void onComplete()
        {
            Timber.d("onComplete()");
            getContextualRouter().getActionFromContextNm(new_comu_usercomu_just_registered).initActivity(activity);
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
