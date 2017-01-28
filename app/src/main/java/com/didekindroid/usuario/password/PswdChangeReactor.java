package com.didekindroid.usuario.password;


import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 15:09
 */

final class PswdChangeReactor implements PswdChangeReactorIf {

    static final PswdChangeReactorIf pswdChangeReactor = new PswdChangeReactor();

    private PswdChangeReactor()
    {
    }

    // ............................ OBSERVABLES ..................................

    private static Single<Integer> isPasswordChanged(final String newPassword)
    {   // TODO: test.
        Timber.d("isPasswordChanged()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.passwordChange(newPassword);
            }
        });
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Override
    public boolean passwordChangeRemote(PasswordChangeControllerIf controller, Usuario usuario)
    {
        Timber.d("passwordChangeRemote()");
        return controller.getSubscriptions().add(isPasswordChanged(usuario.getPassword())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(new PswdChangeSingleObserver(controller)));
    }

    // ............................ SUBSCRIBERS ..................................

    private static class PswdChangeSingleObserver extends DisposableSingleObserver<Integer> {

        private final PasswordChangeControllerIf controller;

        PswdChangeSingleObserver(PasswordChangeControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(Integer changedPassword)
        { // TODO: test.
            Timber.d("onNext: passwordUpdate = %d", changedPassword);
            controller.processBackChangedPswdRemote(changedPassword);
        }

        @Override
        public void onError(Throwable e)
        {  // TODO: test.
            Timber.d("onError");
            controller.processErrorInReactor(e);
        }
    }
}
