package com.didekindroid.incidencia.comment;

import android.app.Activity;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.CtrlerListIf;
import com.didekinlib.model.incidencia.dominio.IncidComment;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

public class CtrlerIncidComment extends Controller implements CtrlerListIf<IncidComment> {

    static void doErrorInCtrler(Throwable e, Activity activity)
    {
        routerInitializer.get()
                .getExceptionRouter()
                .getActionFromMsg(getUiExceptionFromThrowable(e).getErrorHtppMsg())
                .initActivity(activity);
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<IncidComment>> observer, long incidenciaId)
    {
        return getSubscriptions().add(
                incidenciaDao.seeCommentsByIncid(incidenciaId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean regIncidComment(DisposableSingleObserver<Integer> observer, IncidComment comment)
    {
        return getSubscriptions().add(
                incidenciaDao.regIncidComment(comment)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
