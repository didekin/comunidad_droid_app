package com.didekindroid.usuariocomunidad.listbycomu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.lib_one.api.AbstractSingleObserver;
import com.didekindroid.lib_one.api.Viewer;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static android.R.id.list;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 14:40
 */
public final class ViewerSeeUserComuByComu extends Viewer<ListView, CtrlerUserComuByComuList> {

    final TextView nombreComuView;

    private ViewerSeeUserComuByComu(View frView, AppCompatActivity activity)
    {
        super(frView.findViewById(list), activity, null);
        nombreComuView = frView.findViewById(R.id.see_usercomu_by_comu_list_header);
        // To get visible a divider on top of the list.
        view.addHeaderView(new View(activity), null, true);
    }

    static ViewerSeeUserComuByComu newViewerUserComuByComu(View frView, AppCompatActivity activity)
    {
        Timber.d("newViewerUserComuByComu()");
        ViewerSeeUserComuByComu instance = new ViewerSeeUserComuByComu(frView, activity);
        instance.setController(new CtrlerUserComuByComuList());
        return instance;
    }

    // ==================================  VIEWER  =================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable comunidadBean)
    {
        Timber.d("doViewInViewer()");
        // Precondition.
        long comunidadId = ComunidadBean.class.cast(comunidadBean).getComunidadId();
        assertTrue(comunidadId > 0L, intent_extra_should_be_initialized);

        controller.loadItemsByEntitiyId(
                new AbstractSingleObserver<List<UsuarioComunidad>>(this) {
                    @Override
                    public void onSuccess(List<UsuarioComunidad> usuariosComunidad)
                    {
                        Timber.d("onSuccess()");
                        List<UsuarioComunidad> newList;
                        if (usuariosComunidad == null) {
                            newList = new ArrayList<>(0);
                        } else {
                            newList = Collections.unmodifiableList(usuariosComunidad);
                        }
                        onSuccessLoadItems(newList);
                    }
                },
                comunidadId);

        controller.comunidadData(
                new AbstractSingleObserver<Comunidad>(this) {
                    @Override
                    public void onSuccess(Comunidad comunidad)
                    {
                        Timber.d("onSuccess()");
                        onSuccessComunidadData(comunidad.getNombreComunidad());
                    }
                },
                comunidadId);
    }

    // =============================================================================

    void onSuccessLoadItems(List<UsuarioComunidad> itemList)
    {
        Timber.d("onSuccessLoadItemList()");
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
