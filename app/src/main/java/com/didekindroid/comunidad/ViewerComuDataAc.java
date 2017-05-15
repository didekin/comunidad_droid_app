package com.didekindroid.comunidad;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.router.ComponentReplacerIf;
import com.didekindroid.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComunidadAssertionMsg.comunidadId_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 14:09
 */
class ViewerComuDataAc extends Viewer<View, CtrlerComuDataAc> implements ComponentReplacerIf,
        ViewerParentInjectedIf {

    // Since initialization depends on fragment lifecycle, it is done in the activity and
    // in the fragment through the ViewerParentInjectorIf interface.
    @SuppressWarnings("WeakerAccess")
    ViewerRegComuFr viewerRegComuFr;

    ViewerComuDataAc(View view, Activity activity)
    {
        super(view, activity, null);
    }

    static ViewerComuDataAc newViewerComuDataAc(@NonNull ComuDataAc activity)
    {
        Timber.d("newViewerComuDataAc()");
        ViewerComuDataAc instance = new ViewerComuDataAc(activity.acView, activity);
        instance.setController(new CtrlerComuDataAc(instance));
        // We initialize viewerRegComuFr in its associated fragment.
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, final Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser() && viewBean != null, comunidadId_should_be_initialized);
        Button modifyButton = (Button) view.findViewById(R.id.comu_data_ac_button);
        modifyButton.setOnClickListener(new ComuDataAcButtonListener(Comunidad.class.cast(viewBean)));
    }

    // ==================================  ComponentReplaceIF  =================================

    @Override
    public void replaceComponent(@NonNull Bundle bundle)
    {
        Timber.d("replaceComponent()");
        new ActivityInitiator(activity).initActivityWithBundle(bundle);
    }

    // ==================================  ViewerParentInjectedIf  =================================

    @Override
    public void setChildViewer(@NonNull ViewerIf childViewer)
    {
        Timber.d("setChildViewer()");
        viewerRegComuFr = ViewerRegComuFr.class.cast(childViewer);
    }

    // ==================================  HELPERS =================================

    void onSuccessModifyComunidad()
    {
        Timber.d("onSuccessModifyComunidad()");
        replaceComponent(new Bundle(0));
    }

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
            Comunidad comunidadFromViewer = viewerRegComuFr.getComunidadFromViewer(errorBuilder);
            if (comunidadFromViewer == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                Comunidad comunidadOut = new Comunidad.ComunidadBuilder()
                        .c_id(comunidadIn.getC_Id())
                        .copyComunidadNonNullValues(comunidadFromViewer)
                        .build();
                controller.modifyComunidadData(comunidadOut);
            }
        }
    }
}
