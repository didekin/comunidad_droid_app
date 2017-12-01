package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.api.ChildViewersInjectorIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.edit.ViewerIncidEditMinFr.newViewerIncidEditMinFr;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */
public class IncidEditMinFr extends IncidEditFr {

    ViewerIncidEditMinFr viewer;

    static IncidEditMinFr newInstance(IncidAndResolBundle resolBundle)
    {
        Timber.d("newInstance()");
        IncidEditMinFr fr = new IncidEditMinFr();
        Bundle argsFragment = new Bundle();
        argsFragment.putSerializable(INCID_RESOLUCION_BUNDLE.key, resolBundle);
        fr.setArguments(argsFragment);
        return fr;
    }

    @Override
    protected ViewerIncidEditFr getViewerIncidEdit()
    {
        Timber.d("getViewerIncidEdit()");
        return viewer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_edit_nopower_fr, container, false);
        return frView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedInstanceState);

        viewerInjector = (ChildViewersInjectorIf) getActivity();

        viewer = newViewerIncidEditMinFr(frView, viewerInjector.getParentViewer());
        viewer.doViewInViewer(savedInstanceState, resolBundle);
        viewerInjector.setChildInParentViewer(viewer);

        initViewerImportancia(savedInstanceState);
    }
}
