package com.didekinaar.usuario.delete;

import android.content.Intent;

import com.didekinaar.exception.UiException;

import java.util.Objects;
import java.util.concurrent.Callable;

import rx.Single;
import rx.Subscriber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */

class DeleteObservable {

    //    .................................... OBSERVABLES .................................

    static Single<Boolean> getDeleteMeSingle()
    {   // TODO: to test.
        return fromCallable(new DeleteMeCallable()).map(TKhandler.cleanTokenFunc);
    }

    //    .................................... CALLABLES .................................

    private static class DeleteMeCallable implements Callable<Boolean> {

        DeleteMeCallable()
        {
        }

        @Override
        public Boolean call() throws Exception
        {   // TODO: to test: devuelve true y devuelve false (no en BD).
            return usuarioDaoRemote.deleteUser();
        }
    }

    // ............................ SUBSCRIBERS ..................................

    static class DeleteMeSubscriber extends Subscriber<Boolean>{

        final DeleteMeAc activity;

        DeleteMeSubscriber(DeleteMeAc activity)
        {
            this.activity = activity;
        }

        @Override
        public void onCompleted()
        {   // TODO: test.
            activity.finish();
            unsubscribe();
        }

        @Override
        public void onError(Throwable e)
        {
            if (e instanceof UiException) {
                ((UiException) e).processMe(activity, new Intent());
                // TODO: en todos los 'processMe' de didekinaar hay que verificar que el mensaje en UiException es GENERIC_ERROR.
            }
        }

        @Override
        public void onNext(Boolean aBoolean)
        {   // TODO: test.
            Objects.equals(Boolean.TRUE, aBoolean);
            Intent intent = new Intent(activity, activity.defaultActivityClassToGo);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        }
    }
}
