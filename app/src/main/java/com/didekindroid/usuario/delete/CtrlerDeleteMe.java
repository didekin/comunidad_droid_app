package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.didekindroid.api.CtrlerIdentity;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.security.IdentityCacher;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.cleanTokenAndUnregisterFunc;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_have_been_deleted;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
@SuppressWarnings("WeakerAccess")
class CtrlerDeleteMe extends CtrlerIdentity<View> implements CtrlerDeleteMeIf {

    private final ActivityInitiatorIf rootViewReplacer;

    CtrlerDeleteMe(Activity activity)
    {
        this(TKhandler, activity);
    }

    private CtrlerDeleteMe(IdentityCacher identityCacher, Activity activity)
    {
        super(null, identityCacher);
        rootViewReplacer = (ActivityInitiatorIf) activity;
    }

    @Override
    public boolean deleteMeRemote()
    {
        Timber.d("deleteMeRemote()");
        return subscriptions.add(getDeleteMeSingle()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(new DeleteMeSingleObserver(this)));
    }

    @Override
    public void onSuccessDeleteMeRemote(boolean isDeleted)
    {
        Timber.d("onSuccessDeleteMeRemote()");
        assertTrue(isDeleted, user_should_have_been_deleted);
        rootViewReplacer.initActivity(new Bundle());
    }

    // ................................. OBSERVABLES ...............................

    static Single<Boolean> getDeleteMeSingle()
    {
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                Timber.d("fromCallable(), thread: %s", Thread.currentThread().getName());
                return usuarioDao.deleteUser();
            }
        }).map(cleanTokenAndUnregisterFunc);
    }

    // .............................. SUBSCRIBERS ..................................

    static class DeleteMeSingleObserver extends DisposableSingleObserver<Boolean> {

        private final CtrlerDeleteMeIf controller;

        DeleteMeSingleObserver(CtrlerDeleteMeIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(Boolean isDeleted)
        {
            Timber.d("onSuccess(), Thread: %s", Thread.currentThread().getName());
            controller.onSuccessDeleteMeRemote(isDeleted);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorCtrl, Thread: %s", Thread.currentThread().getName());
            controller.onErrorCtrl(e);
        }
    }
}
