package com.didekindroid.usuariocomunidad.listbycomu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.ViewBean;
import com.didekindroid.api.Viewer;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 14:40
 */
class ViewerSeeUserComuByComu extends Viewer<ListView, CtrlerUserComuByComuList> {

    private final TextView nombreComuView;

    ViewerSeeUserComuByComu(ListView view, TextView nombreComuView, Activity activity)
    {
        super(view, activity, null);
        this.nombreComuView = nombreComuView;
    }

    static ViewerSeeUserComuByComu newViewerUserComuByComu(View mainView, Activity activity)
    {
        Timber.d("newViewerUserComuByComu()");
        ViewerSeeUserComuByComu instance = new ViewerSeeUserComuByComu(
                (ListView) mainView.findViewById(android.R.id.list),
                (TextView) mainView.findViewById(R.id.see_usercomu_by_comu_list_header),
                activity);
        instance.setController(new CtrlerUserComuByComuList(instance));
        // Precondition.
        assertTrue(instance.controller.isRegisteredUser(), user_should_be_registered);
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, ViewBean viewBean)
    {
        Timber.d("doViewInViewer()");
        // To get visible a divider on top of the list.
        view.addHeaderView(new View(activity), null, true);

        Intent intentInActivity = activity.getIntent();
        assertTrue(intentInActivity.hasExtra(COMUNIDAD_ID.key), intent_extra_should_be_initialized);
        long comunidadId = intentInActivity.getExtras().getLong(ComuBundleKey.COMUNIDAD_ID.key);

        controller.loadItemsByEntitiyId(comunidadId);
        controller.comunidadData(comunidadId);
    }

    void processLoadedItemsinView(List<UsuarioComunidad> itemList)
    {
        Timber.d("processLoadedItemsinView()");
        SeeUserComuByComuListAdapter adapter = new SeeUserComuByComuListAdapter(activity);
        adapter.addAll(itemList);
        view.setAdapter(adapter);
    }

    void setNombreComuViewText(String text)
    {
        Timber.d("setNombreComuViewText()");
        nombreComuView.setText(text);
    }
}
