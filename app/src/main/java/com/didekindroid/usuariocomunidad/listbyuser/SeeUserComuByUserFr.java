package com.didekindroid.usuariocomunidad.listbyuser;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.newComunidadUserComu;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.userComuItemSelected;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.usercomu_list_should_be_initialized;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;

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
public class SeeUserComuByUserFr extends Fragment implements ActivityInitiatorIf {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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
                    Bundle bundle = new Bundle(1);
                    bundle.putSerializable(USERCOMU_LIST_OBJECT.key, (Serializable) frView.getItemAtPosition(position));
                    initAcFromRouter(bundle, userComuItemSelected);
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

    @SuppressWarnings("WeakerAccess")
    class UserComuByUserLoader extends AsyncTask<Void, Void, List<UsuarioComunidad>> {

        UiException uiException;

        @Override
        protected List<UsuarioComunidad> doInBackground(Void... aVoid)
        {
            Timber.d("UserComuByUserLoader.doInBackground()");

            List<UsuarioComunidad> usuarioComunidades = null;
            try {
                usuarioComunidades = userComuDaoRemote.seeUserComusByUser();
            } catch (UiException e) {
                uiException = e;
            }
            return usuarioComunidades;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(List<UsuarioComunidad> usuarioComunidades)
        {
            Timber.d("onPostExecute()");
            if (checkPostExecute(activity)) return;

            if (uiException != null) {  // action: LOGIN.
                Timber.d("UserComuByUserLoader.onPostExecute(): uiException != null");
                assertTrue(usuarioComunidades == null, usercomu_list_should_be_initialized);
                uiException.processMe(getActivity());
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
