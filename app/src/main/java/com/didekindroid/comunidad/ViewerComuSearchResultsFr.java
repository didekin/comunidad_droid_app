package com.didekindroid.comunidad;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserComuAc;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 17/06/17
 * Time: 16:29
 */
final class ViewerComuSearchResultsFr extends Viewer<ListView, CtrlerComunidad> {

    private ViewerComuSearchResultsFr(@NonNull View frView, @NonNull AppCompatActivity activity)
    {
        super((ListView) frView.findViewById(android.R.id.list), activity, null);
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
        controller.loadComunidadesFound(new ListComunidadesSearchObserver(comunidadToSearch), comunidadToSearch);
    }

    // ==================================  HELPERS =================================

    public void replaceComponent(@NonNull Bundle bundle, Class<? extends Activity> nextActivity)
    {
        Timber.d("replaceComponent()");
        new ActivityInitiator(activity).initAcWithBundle(bundle, nextActivity);
    }

    @SuppressWarnings("WeakerAccess")
    void onSuccessLoadList(@NonNull List<Comunidad> comunidades)
    {
        Timber.d("onSuccessLoadList()");
        ArrayAdapter<Comunidad> adapter = new ComuSearchResultsListAdapter(getActivity());
        adapter.addAll(comunidades);
        view.setAdapter(adapter);
    }

    @SuppressWarnings("WeakerAccess")
    void onSuccessEmptyList(Comunidad comunidad)
    {
        Timber.d("onSuccessEmptyList()");
        makeToast(activity, R.string.no_result_search_comunidad);
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(COMUNIDAD_SEARCH.key, comunidad);
        if (controller.isRegisteredUser()) {
            replaceComponent(bundle, RegComuAndUserComuAc.class);
        } else {
            replaceComponent(bundle, RegComuAndUserAndUserComuAc.class);
        }
        activity.finish();
    }

    @SuppressWarnings("WeakerAccess")
    class ComuSearchResultListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View clickView, int position, long id)
        {
            Timber.d("onItemClick()");
            view.setItemChecked(position, true);
            clickView.setSelected(true);
            final Comunidad comunidadSelect = (Comunidad) view.getItemAtPosition(position);

            if (!controller.isRegisteredUser()) {
                Bundle bundle = new Bundle(1);
                bundle.putSerializable(COMUNIDAD_LIST_OBJECT.key, comunidadSelect);
                replaceComponent(bundle, RegUserAndUserComuAc.class);
            } else {
                controller.getUserComu(new UsuarioComunidadObserver(comunidadSelect), comunidadSelect);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
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
            Bundle bundle = new Bundle(1);
            bundle.putSerializable(USERCOMU_LIST_OBJECT.key, usuarioComunidad);
            replaceComponent(bundle, UserComuDataAc.class);
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
            Bundle bundle = new Bundle(1);
            bundle.putSerializable(COMUNIDAD_LIST_OBJECT.key, comunidad);
            replaceComponent(bundle, RegUserComuAc.class);
        }
    }

    @SuppressWarnings("WeakerAccess")
    final class ListComunidadesSearchObserver extends DisposableSingleObserver<List<Comunidad>> {

        private final Comunidad comunidad;

        ListComunidadesSearchObserver(Comunidad comunidad)
        {
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

        @Override
        public void onError(@NonNull Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }
    }
}
