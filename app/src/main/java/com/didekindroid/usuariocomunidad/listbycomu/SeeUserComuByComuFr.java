package com.didekindroid.usuariocomunidad.listbycomu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;


/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:38
 *
 * <p>
 * Preconditions:
 * 1. the user is registered.
 * 2. a long comunidadId is passed as an intent key.
 */
public class SeeUserComuByComuFr extends Fragment {

    ViewerSeeUserComuByComu viewer;
    View frView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.see_usercomu_by_comu_list_fr, container, false);
        return frView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
        // Precondition:
        assertTrue(getActivity().getIntent().hasExtra(COMUNIDAD_ID.key), intent_extra_should_be_initialized);
        ComunidadBean comunidadBean = new ComunidadBean();
        comunidadBean.setComunidadId(getActivity().getIntent().getLongExtra(COMUNIDAD_ID.key, 0L));
        viewer = ViewerSeeUserComuByComu.newViewerUserComuByComu(frView, (AppCompatActivity) getActivity());
        viewer.doViewInViewer(savedInstanceState, comunidadBean);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}
