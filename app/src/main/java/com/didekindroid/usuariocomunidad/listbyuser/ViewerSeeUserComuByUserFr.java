package com.didekindroid.usuariocomunidad.listbyuser;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.didekindroid.lib_one.api.AbstractSingleObserver;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.ViewerSelectList;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.List;

import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_ID;
import static com.didekindroid.comunidad.util.ComuContextualName.usercomu_just_selected;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static java.util.Collections.unmodifiableList;

public class ViewerSeeUserComuByUserFr extends
        ViewerSelectList<ListView, CtrlerSelectListIf<UsuarioComunidad>, UsuarioComunidad> {

    private ViewerSeeUserComuByUserFr(ListView view, Activity activity)
    {
        super(view, activity);
    }

    static ViewerSeeUserComuByUserFr newViewerSeeUserComuByUserFr(ListView view, Activity activity)
    {
        Timber.d("newViewerSeeUserComuByUserFr()");
        ViewerSeeUserComuByUserFr instance = new ViewerSeeUserComuByUserFr(view, activity);
        instance.setController(new CtrlerSeeUserComuByUser());
        return instance;
    }

    /* ==================================  VIEWER  =================================*/

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        initSelectedItemId(savedState);

        controller.loadItemsByEntitiyId(
                new AbstractSingleObserver<List<UsuarioComunidad>>(this) {
                    @Override
                    public void onSuccess(List<UsuarioComunidad> usuarioComunidads)
                    {
                        Timber.d("onSuccess()");
                        SeeUserComuByUserAdapter adapter = new SeeUserComuByUserAdapter(activity);
                        adapter.addAll(unmodifiableList(usuarioComunidads));
                        view.setAdapter(adapter);
                        view.setItemChecked(getSelectedPositionFromItemId(getBeanIdFunction()), true);
                    }
                }
        );

        view.setOnItemClickListener(
                (parent, viewClick, position, id) -> {
                    Timber.d("setOnItemClickListener()");
                    view.setItemChecked(position, true);
                    viewClick.setSelected(true);
                    UsuarioComunidad userComuIn = (UsuarioComunidad) view.getItemAtPosition(position);
                    try {
                        itemSelectedId = getBeanIdFunction().apply(userComuIn);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    getContextualRouter().getActionFromContextNm(usercomu_just_selected)
                            .initActivity(activity, USERCOMU_LIST_OBJECT.getBundleForKey(userComuIn));
                }
        );
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("savedState()");
        if (savedState == null) {
            savedState = new Bundle();
        }
        savedState.putLong(COMUNIDAD_LIST_ID.key, itemSelectedId);
        Timber.d("Comunidad key = %d", savedState.getLong(COMUNIDAD_LIST_ID.key));
    }

    /* ==================================  ViewerSelectedListIf  =================================*/

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null) {
            itemSelectedId = savedState.getLong(COMUNIDAD_LIST_ID.key, 0L);
        }
    }

    @Override
    public Function<UsuarioComunidad, Long> getBeanIdFunction()
    {
        Timber.d("getBeanIdFunction()");
        return usuarioComunidad -> usuarioComunidad.getComunidad().getC_Id();
    }
}
