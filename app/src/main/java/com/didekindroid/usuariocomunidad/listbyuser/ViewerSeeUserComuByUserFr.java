package com.didekindroid.usuariocomunidad.listbyuser;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.ViewerSelectList;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import io.reactivex.functions.Function;
import timber.log.Timber;

public class ViewerSeeUserComuByUserFr extends
        ViewerSelectList<ListView, CtrlerSelectListIf<UsuarioComunidad>, UsuarioComunidad> {

    private ViewerSeeUserComuByUserFr(ListView view, Activity activity)
    {
        super(view, activity);
    }

    static ViewerSeeUserComuByUserFr newViewerSeeUserComuByUserFr(ListView view, Activity activity){
        Timber.d("newViewerSeeUserComuByUserFr()");
        ViewerSeeUserComuByUserFr instance = new ViewerSeeUserComuByUserFr(view, activity);
        instance.setController(new CtrlerSeeUserComuByUser());
        return instance;
    }

    // ==================================  VIEWER  =================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {

    }

    @Override
    public Function<UsuarioComunidad, Long> getBeanIdFunction()
    {
        return null;
    }

    /* ==================================  ViewerSelectedListIf  =================================*/
}
