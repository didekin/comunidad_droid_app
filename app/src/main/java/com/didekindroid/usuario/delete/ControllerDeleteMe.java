package com.didekindroid.usuario.delete;

import android.view.View;

import com.didekindroid.ControllerAbs;
import com.didekindroid.ManagerIf.ViewerIf;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.cleanTokenAndUnregisterFunc;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_have_been_deleted;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.delete.ControllerDeleteMe.ReactorDeleteMe.deleteReactor;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
class ControllerDeleteMe extends ControllerAbs implements ControllerDeleteMeIf {

    private final ReactorDeleteMeIf reactor;
    private final ViewerIf<View, Object> viewer;

    ControllerDeleteMe(ViewerIf<View, Object> viewer)
    {
        this(viewer, deleteReactor);
    }

    @SuppressWarnings("WeakerAccess")
    ControllerDeleteMe(ViewerIf<View, Object> viewer, ReactorDeleteMeIf reactor)
    {
        super();
        this.reactor = reactor;
        this.viewer = viewer;
    }

    @Override
    public boolean unregisterUser()
    {
        Timber.d("unregisterUser()");
        return reactor.deleteMeInRemote(this);
    }

    @Override
    public void processBackDeleteMeRemote(boolean isDeleted)
    {
        Timber.d("processBackDeleteMeRemote()");
        assertTrue(isDeleted, user_should_have_been_deleted);
        viewer.replaceView(null);
    }

    @Override
    public ViewerFirebaseTokenIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }

    // ============================================================================================
    // ......................................... REACTOR ..........................................
    // ============================================================================================

    static final class ReactorDeleteMe implements ControllerDeleteMeIf.ReactorDeleteMeIf {

        static final ReactorDeleteMeIf deleteReactor = new ReactorDeleteMe();

        private ReactorDeleteMe()
        {
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

        // ............................ SUBSCRIPTIONS ..................................

        @Override
        public boolean deleteMeInRemote(ControllerDeleteMeIf controller){
            return controller.getSubscriptions().add(getDeleteMeSingle()
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .subscribeWith(new DeleteMeSingleObserver(controller)));
        }

        // .............................. SUBSCRIBERS ..................................

        static class DeleteMeSingleObserver extends DisposableSingleObserver<Boolean> {

            private final ControllerDeleteMeIf controller;

            DeleteMeSingleObserver(ControllerDeleteMeIf controller)
            {
                this.controller = controller;
            }

            @Override
            public void onSuccess(Boolean isDeleted)
            {
                Timber.d("onSuccess(), Thread: %s", Thread.currentThread().getName());
                controller.processBackDeleteMeRemote(isDeleted);
            }

            @Override
            public void onError(Throwable e)
            {
                Timber.d("onError, Thread: %s", Thread.currentThread().getName());
                controller.processReactorError(e);
            }
        }
    }
}
