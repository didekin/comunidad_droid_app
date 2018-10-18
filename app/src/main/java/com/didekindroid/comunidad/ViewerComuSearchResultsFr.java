package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.AbstractSingleObserver;
import com.didekindroid.lib_one.api.Viewer;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.comunidad.util.ComuContextualName.found_comu_single_for_current_neighbour;
import static com.didekindroid.comunidad.util.ComuContextualName.found_comu_single_for_current_user;
import static com.didekindroid.comunidad.util.ComuContextualName.found_comu_single_for_no_reg_user;
import static com.didekindroid.comunidad.util.ComuContextualName.no_found_comu_for_current_user;
import static com.didekindroid.comunidad.util.ComuContextualName.no_found_comu_for_no_reg_user;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;

/**
 * User: pedro@didekin
 * Date: 17/06/17
 * Time: 16:29
 */
final class ViewerComuSearchResultsFr extends Viewer<ListView, CtrlerComunidad> {

    private ViewerComuSearchResultsFr(@NonNull View frView, @NonNull AppCompatActivity activity)
    {
        super(frView.findViewById(android.R.id.list), activity, null);
    }

    static ViewerComuSearchResultsFr newViewerComuSearchResultsFr(View frView, AppCompatActivity activity)
    {
        Timber.d("newViewerComuSearchResultsFr()");
        ViewerComuSearchResultsFr instance = new ViewerComuSearchResultsFr(frView, activity);
        instance.setController(new CtrlerComunidad());
        return instance;
    }

    // ==================================  VIEWER  =================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        Comunidad comunidadToSearch = Comunidad.class.cast(viewBean);
        view.setItemsCanFocus(true);
        view.setOnItemClickListener(new ComuSearchResultListener());
        controller.searchInComunidades(new ListComunidadesSearchObserver(comunidadToSearch), comunidadToSearch);
    }

    // ==================================  HELPERS =================================

    void onSuccessLoadList(@NonNull List<Comunidad> comunidades)
    {
        Timber.d("onSuccessLoadList()");
        ArrayAdapter<Comunidad> adapter = new ComuSearchResultsListAdapter(getActivity());
        adapter.addAll(comunidades);
        view.setAdapter(adapter);
    }

    void onSuccessEmptyList(Comunidad comunidad)
    {
        Timber.d("onSuccessEmptyList()");
        makeToast(activity, R.string.no_result_search_comunidad);
        if (controller.isRegisteredUser()) {
            getContextualRouter().getActionFromContextNm(no_found_comu_for_current_user).initActivity(activity, COMUNIDAD_SEARCH.getBundleForKey(comunidad));
        } else {
            getContextualRouter().getActionFromContextNm(no_found_comu_for_no_reg_user).initActivity(activity, COMUNIDAD_SEARCH.getBundleForKey(comunidad));
        }
        activity.finish();
    }

    // ==================================  INNER CLASSES =================================

    final class ComuSearchResultListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View clickView, int position, long id)
        {
            Timber.d("onItemClick()");
            view.setItemChecked(position, true);
            clickView.setSelected(true);
            final Comunidad comunidadSelect = (Comunidad) view.getItemAtPosition(position);

            if (!controller.isRegisteredUser()) {
                getContextualRouter().getActionFromContextNm(found_comu_single_for_no_reg_user)
                        .initActivity(activity, COMUNIDAD_LIST_OBJECT.getBundleForKey(comunidadSelect));
            } else {
                controller.getUserComu(new UsuarioComunidadObserver(comunidadSelect), comunidadSelect);
            }
        }
    }

    final class UsuarioComunidadObserver extends DisposableMaybeObserver<UsuarioComunidad> {

        final Comunidad comunidad;

        UsuarioComunidadObserver(Comunidad comunidad)
        {
            this.comunidad = comunidad;
        }

        @Override
        public void onSuccess(UsuarioComunidad usuarioComunidad)
        {
            Timber.d("onSuccess()");
            getContextualRouter().getActionFromContextNm(found_comu_single_for_current_neighbour)
                    .initActivity(activity, USERCOMU_LIST_OBJECT.getBundleForKey(usuarioComunidad));
        }

        @Override
        public void onError(@NonNull Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }

        @Override
        public void onComplete()
        {
            Timber.d("onComplete()");
            getContextualRouter().getActionFromContextNm(found_comu_single_for_current_user).initActivity(activity, COMUNIDAD_LIST_OBJECT.getBundleForKey(comunidad));
        }
    }

    final class ListComunidadesSearchObserver extends AbstractSingleObserver<List<Comunidad>> {

        private final Comunidad comunidad;

        ListComunidadesSearchObserver(Comunidad comunidad)
        {
            super(ViewerComuSearchResultsFr.this);
            this.comunidad = comunidad;
        }

        @Override
        public void onSuccess(@NonNull List<Comunidad> comunidades)
        {
            Timber.d("onSuccess()");
            if (!comunidades.isEmpty()) {
                onSuccessLoadList(comunidades);
            } else {
                onSuccessEmptyList(comunidad);
            }
        }
    }
}
