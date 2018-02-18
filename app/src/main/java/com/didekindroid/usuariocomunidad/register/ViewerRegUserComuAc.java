package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.util.ComuContextualName.new_usercomu_just_registered;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.user_and_comunidad_should_be_registered;

/**
 * User: pedro@didekin
 * Date: 05/06/17
 * Time: 13:39
 */

final class ViewerRegUserComuAc extends ParentViewer<View, CtrlerUsuarioComunidad> {

    private ViewerRegUserComuAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
    }

    static ViewerRegUserComuAc newViewerRegUserComuAc(RegUserComuAc activity)
    {
        Timber.d("newViewerRegUserComuAc()");
        ViewerRegUserComuAc instance = new ViewerRegUserComuAc(activity.acView, activity);
        instance.setController(new CtrlerUsuarioComunidad());
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        Comunidad comunidad = Comunidad.class.cast(viewBean);
        TextView nombreComunidad = view.findViewById(R.id.descripcion_comunidad_text);
        nombreComunidad.setText(comunidad.getNombreComunidad());
        Button registroButton = view.findViewById(R.id.reg_usercomu_button);
        registroButton.setOnClickListener(new RegUserComuButtonListener(comunidad));
    }

    @SuppressWarnings("WeakerAccess")
    class RegUserComuButtonListener implements View.OnClickListener {

        private final Comunidad comunidad;

        RegUserComuButtonListener(Comunidad comunidad)
        {
            this.comunidad = comunidad;
        }

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            UsuarioComunidad usuarioComunidad = getChildViewer(ViewerRegUserComuFr.class).getUserComuFromViewer(errorBuilder, comunidad, null);

            if (usuarioComunidad == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                controller.registerUserComu(
                        new RegUserComuObserver(comunidad),
                        usuarioComunidad);
            }
        }
    }

    // ==================================== Observer ================================

    @SuppressWarnings("WeakerAccess")
    class RegUserComuObserver extends DisposableSingleObserver<Integer> {

        final Comunidad comunidad;

        public RegUserComuObserver(Comunidad comunidad)
        {
            this.comunidad = comunidad;
        }

        @Override
        public void onSuccess(Integer rowInserted)
        {
            Timber.d("onSuccess()");
            assertTrue(rowInserted == 1, user_and_comunidad_should_be_registered);
            Bundle bundle = new Bundle(1);
            bundle.putLong(COMUNIDAD_ID.key, comunidad.getC_Id());
            getContextualRouter().getActionFromContextNm(new_usercomu_just_registered)
                    .initActivity(activity, bundle);
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
