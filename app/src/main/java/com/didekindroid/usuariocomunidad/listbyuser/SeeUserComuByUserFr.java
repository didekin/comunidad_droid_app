package com.didekindroid.usuariocomunidad.listbyuser;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuContextualName.usercomu_just_selected;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.checkPostExecute;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.usercomu_list_should_be_initialized;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static java.util.Objects.requireNonNull;

/**
 * Preconditions:
 * <p/>
 * 1. Every object UsuarioComunidad, in the list supplied to the adapter, has a fully initialized Usuario and
 * Comunidad, as well as the rest of the data.
 * <p/>
 * Postconditions:
 * <p/>
 * 1. An object UsuarioComunidad is passed to the listener activity.
 */
public class SeeUserComuByUserFr extends Fragment {

    public SeeUserComuByUserAdapter mAdapter;
    Activity activity;
    ListView frView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        // Loading the data...
        new UserComuByUserLoader().execute();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = (ListView) inflater.inflate(R.layout.see_user_by_user_list_fr, container, false);
        frView.setItemsCanFocus(true);
        return frView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        frView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    frView.setItemChecked(position, true);
                    view.setSelected(true);
                    routerInitializer.get().getContextRouter().getActionFromContextNm(usercomu_just_selected)
                            .initActivity(
                                    requireNonNull(getActivity()),
                                    USERCOMU_LIST_OBJECT.getBundleForKey((Serializable) frView.getItemAtPosition(position))
                            );
                }
        );
    }

// .......... Interface to communicate with the Activity ...................

    public View getFrView()
    {
        Timber.d("getFrView()");
        return frView;
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    static class UserComuByUserLoader extends AsyncTask<Void, Void, List<UsuarioComunidad>> {

        UiException uiException;

        @Override
        protected List<UsuarioComunidad> doInBackground(Void... aVoid)
        {
            Timber.d("UserComuByUserLoader.doInBackground()");

            List<UsuarioComunidad> usuarioComunidades;
            usuarioComunidades = userComuDao.seeUserComusByUser().blockingGet();
            return usuarioComunidades;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(List<UsuarioComunidad> usuarioComunidades)
        {
            Timber.d("onPostExecute()");
            if (checkPostExecute(activity)) return;    // TODO: descomentar y modificar.

            if (uiException != null) {  // action: LOGIN.
                Timber.d("UserComuByUserLoader.onPostExecute(): uiException != null");
                assertTrue(usuarioComunidades == null, usercomu_list_should_be_initialized);
                routerInitializer.get().getExceptionRouter().getActionFromMsg(uiException.getErrorHtppMsg())
                        .initActivity(activity);
            }
            if (usuarioComunidades != null) {
                Timber.d("UserComuByUserLoader.onPostExecute(): usuarioComunidades != null");
                mAdapter = new SeeUserComuByUserAdapter(getActivity());
                mAdapter.addAll(usuarioComunidades);
                frView.setAdapter(mAdapter);
            }
        }
    }
}
