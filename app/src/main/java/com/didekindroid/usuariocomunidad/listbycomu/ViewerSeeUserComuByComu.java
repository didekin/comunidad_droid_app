package com.didekindroid.usuariocomunidad.listbycomu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 14:40
 */
@SuppressWarnings("ClassWithOnlyPrivateConstructors")
class ViewerSeeUserComuByComu extends Viewer<ListView, CtrlerUserComuByComuList> {

    final TextView nombreComuView;

    ViewerSeeUserComuByComu(View frView, Activity activity)
    {
        super((ListView) frView.findViewById(android.R.id.list), activity, null);
        nombreComuView = (TextView) frView.findViewById(R.id.see_usercomu_by_comu_list_header);
        // To get visible a divider on top of the list.
        view.addHeaderView(new View(activity), null, true);
    }

    static ViewerSeeUserComuByComu newViewerUserComuByComu(View frView, Activity activity)
    {
        Timber.d("newViewerUserComuByComu()");
        ViewerSeeUserComuByComu instance = new ViewerSeeUserComuByComu(frView, activity);
        instance.setController(new CtrlerUserComuByComuList(instance));
        return instance;
    }

    // ==================================  VIEWER  =================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable comunidadBean)
    {
        Timber.d("doViewInViewer()");
        // Precondition.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        long comunidadId = ComunidadBean.class.cast(comunidadBean).getComunidadId();
        assertTrue(comunidadId > 0L, intent_extra_should_be_initialized);

        controller.loadItemsByEntitiyId(comunidadId);
        controller.comunidadData(comunidadId);
    }

    // =============================================================================

    void onSuccessLoadItems(List<UsuarioComunidad> itemList)
    {
        Timber.d("onSuccessLoadItems()");
        SeeUserComuByComuListAdapter adapter = new SeeUserComuByComuListAdapter(activity);
        adapter.addAll(itemList);
        view.setAdapter(adapter);
    }

    void onSuccessComunidadData(String text)
    {
        Timber.d("onSuccessComunidadData()");
        nombreComuView.setText(text);
    }
}
