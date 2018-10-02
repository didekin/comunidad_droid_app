package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.core.edit.ViewerIncidEditMinFr.newViewerIncidEditMinFr;
import static java.util.Objects.requireNonNull;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_edit_nopower_fr, container, false);
        return frView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedInstanceState);

        viewerInjector = (InjectorOfParentViewerIf) getActivity();

        viewer = newViewerIncidEditMinFr(frView, requireNonNull(viewerInjector).getInjectedParentViewer());
        viewer.doViewInViewer(savedInstanceState, resolBundle);
        viewerInjector.setChildInParentViewer(viewer);

        initViewerImportancia(savedInstanceState);
    }
}
