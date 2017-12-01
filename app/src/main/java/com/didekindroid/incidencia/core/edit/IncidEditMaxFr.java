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

import static com.didekindroid.incidencia.core.edit.ViewerIncidEditMaxFr.newViewerIncidEditMaxFr;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */
public class IncidEditMaxFr extends IncidEditFr {

    ViewerIncidEditMaxFr viewer;

    static IncidEditMaxFr newInstance(IncidAndResolBundle resolBundle)
    {
        Timber.d("newInstance()");
        IncidEditMaxFr fr = new IncidEditMaxFr();
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
        frView = inflater.inflate(R.layout.incid_edit_maxpower_fr, container, false);
        return frView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewerInjector = (ChildViewersInjectorIf) getActivity();

        viewer = newViewerIncidEditMaxFr(frView, viewerInjector.getParentViewer());
        viewer.doViewInViewer(savedInstanceState, resolBundle);
        viewerInjector.setChildInParentViewer(viewer);

        initViewerImportancia(savedInstanceState);
    }
}
