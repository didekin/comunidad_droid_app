package com.didekinaar.usuario.delete;

import android.content.Intent;

import com.didekinaar.ActivitySubscriber;

import java.util.Objects;
import java.util.concurrent.Callable;

import rx.Single;

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
    {   // TODO: to test: devuelve true y devuelve false (no en BD).
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDaoRemote.deleteUser();
            }
        }).map(TKhandler.cleanTokenFunc);
    }

    // ............................ SUBSCRIBERS ..................................

    static class DeleteMeSubscriber extends ActivitySubscriber<Boolean, DeleteMeAc> {

        DeleteMeSubscriber(DeleteMeAc activity)
        {
            super(activity);
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
