package com.didekinaar.usuario.password;

import android.content.Intent;

import com.didekinaar.ActivitySubscriber;
import com.didekinaar.usuario.userdata.UserDataAc;

import java.util.Objects;
import java.util.concurrent.Callable;

import rx.Single;
import timber.log.Timber;

import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 15:09
 */

class PswdChangeAcObservable {

    // ............................ OBSERVABLES ..................................

    static Single<Integer> isPasswordChanged(final String newPassword)
    {   // TODO: test.
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDaoRemote.passwordChange(newPassword);
            }
        });
    }

    // ............................ SUBSCRIBERS ..................................

    static class PasswordChangeSubscriber extends ActivitySubscriber<Integer, PasswordChangeAc> {

        PasswordChangeSubscriber(PasswordChangeAc activity)
        {
            super(activity);
        }

        @Override
        public void onNext(Integer passwordUpdate)
        {   // TODO: test.
            Timber.d("onNext: passwordUpdate = %d", passwordUpdate);
            Objects.equals(passwordUpdate == 1, true);
            Intent intent = new Intent(activity, UserDataAc.class);
            activity.startActivity(intent);
        }
    }
}
