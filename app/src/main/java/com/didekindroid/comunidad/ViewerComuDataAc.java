package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewerInjected;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComunidadAssertionMsg.comuData_should_be_modified;
import static com.didekindroid.comunidad.utils.ComunidadAssertionMsg.comunidadId_should_be_initialized;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.LeadRouter.afterMofiedComunidad;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 14:09
 */
class ViewerComuDataAc extends ParentViewerInjected<View, CtrlerComunidad> {

    ViewerComuDataAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
    }

    static ViewerComuDataAc newViewerComuDataAc(@NonNull ComuDataAc activity)
    {
        Timber.d("newViewerComuDataAc()");
        ViewerComuDataAc instance = new ViewerComuDataAc(activity.acView, activity);
        instance.setController(new CtrlerComunidad());
        // We initialize viewerRegComuFr in its associated fragment.
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, final Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser() && viewBean != null, comunidadId_should_be_initialized);
        Button modifyButton = view.findViewById(R.id.comu_data_ac_button);
        modifyButton.setOnClickListener(new ComuDataAcButtonListener(Comunidad.class.cast(viewBean)));
    }

    // ==================================  HELPERS =================================

    @SuppressWarnings("WeakerAccess")
    class ComuDataAcButtonListener implements View.OnClickListener {

        private final Comunidad comunidadIn;

        public ComuDataAcButtonListener(Comunidad viewBean)
        {
            comunidadIn = viewBean;
        }

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            Comunidad comunidadFromViewer = getChildViewer(ViewerRegComuFr.class).getComunidadFromViewer(errorBuilder);
            if (comunidadFromViewer == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                Comunidad comunidadOut = new Comunidad.ComunidadBuilder()
                        .c_id(comunidadIn.getC_Id())
                        .copyComunidadNonNullValues(comunidadFromViewer)
                        .build();
                controller.modifyComunidadData(new ComuDataAcObserver(), comunidadOut);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class ComuDataAcObserver extends DisposableSingleObserver<Integer> {

        @Override
        public void onSuccess(Integer rowsUpdated)
        {
            Timber.d("onSuccess()");
            assertTrue(rowsUpdated == 1, comuData_should_be_modified);
            afterMofiedComunidad.initActivity(activity, null);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }
    }
}
