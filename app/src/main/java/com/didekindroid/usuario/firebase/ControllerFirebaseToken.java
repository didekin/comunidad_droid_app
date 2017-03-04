package com.didekindroid.usuario.firebase;

import com.didekindroid.ControllerAbs;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;
import com.didekindroid.security.IdentityCacher;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.firebase.ControllerFirebaseToken.FirebaseTokenReactor.tokenReactor;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 14:23
 */
@SuppressWarnings("WeakerAccess")
public class ControllerFirebaseToken extends ControllerAbs implements ControllerFirebaseTokenIf {

    private final ViewerFirebaseTokenIf viewer;
    private final FirebaseTokenReactorIf reactor;
    private final IdentityCacher identityCacher;


    public ControllerFirebaseToken(ViewerFirebaseTokenIf viewer)
    {
        this(viewer, tokenReactor, TKhandler);
    }

    ControllerFirebaseToken(ViewerFirebaseTokenIf viewer, FirebaseTokenReactorIf reactor, IdentityCacher identityCacher)
    {
        this.viewer = viewer;
        this.reactor = reactor;
        this.identityCacher = identityCacher;
    }

    @Override
    public void checkGcmToken()
    {
        Timber.d("checkGcmToken()");
        if (!identityCacher.isRegisteredUser() || identityCacher.isGcmTokenSentServer()) {
            return;
        }
        reactor.checkGcmToken(this);
    }

    @Override
    public void checkGcmTokenSync()
    {
        Timber.d("checkGcmTokenSync()");
        if (identityCacher.isRegisteredUser()){
            reactor.checkGcmTokenSync(this);
        }
    }

    @Override
    public void updateIsGcmTokenSentServer(boolean toUpdadteToken)
    {
        Timber.d("updateIsGcmTokenSentServer()");
        identityCacher.updateIsGcmTokenSentServer(toUpdadteToken);
    }

    @Override
    public ViewerFirebaseTokenIf getViewer()
    {
        return viewer;
    }

    //  =====================================================================================================
    //    .................................... REACTOR .................................
    //  =====================================================================================================

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    final static class FirebaseTokenReactor implements FirebaseTokenReactorIf {

        static final FirebaseTokenReactorIf tokenReactor = new FirebaseTokenReactor();

        //    .................................... OBSERVABLES .................................

        /**
         * Preconditions: the user is registered.
         * Postconditions: the user's gcm token in database is updated.
         *
         * @return a Single with an item == 1 if the gcmToken is updated.
         */
        Single<Integer> updatedGcmTkSingle()
        {
            return Single.fromCallable(new Callable<Integer>() {
                                           @Override
                                           public Integer call() throws Exception
                                           {
                                               String token = FirebaseInstanceId.getInstance().getToken();
                                               return usuarioDao.modifyUserGcmToken(token);
                                           }
                                       }
            );
        }

        // .................................... SUBSCRIPTIONS ..................................

        @Override
        public boolean checkGcmToken(ControllerFirebaseTokenIf controller)
        {
            Timber.d("checkGcmToken()");

            return controller.getSubscriptions().add(
                    updatedGcmTkSingle()
                            .subscribeOn(Schedulers.io())
                            .observeOn(mainThread())
                            .subscribeWith(new RegGcmTokenObserver(controller)));
        }

        /**
         * Synchronous variant for the service InstanceIdService.
         * The method does not check if the gcmToken has been sent previously to database.
         */
        @Override
        public void checkGcmTokenSync(ControllerFirebaseTokenIf controller)
        {
            Timber.d("checkGcmTokenSync()");

            updatedGcmTkSingle().subscribe(new RegGcmTokenObserver(controller){
                @Override
                public void onError(Throwable error)
                {
                    Timber.d("onError(): %s", error.getMessage());
                }
            });
        }

        // ............................ SUBSCRIBERS ..................................

        class RegGcmTokenObserver extends DisposableSingleObserver<Integer> {

            private final ControllerFirebaseTokenIf controller;

            public RegGcmTokenObserver(ControllerFirebaseTokenIf controller)
            {
                this.controller = controller;
            }

            @Override
            public void onSuccess(Integer isUpdated)
            {
                Timber.d("onSuccess(%d)", isUpdated);
                if (isUpdated > 0) {
                    controller.updateIsGcmTokenSentServer(true);
                }
                controller.clearSubscriptions();
            }

            @Override
            public void onError(Throwable error)
            {
                Timber.d("onError(): %s", error.getMessage());
                controller.updateIsGcmTokenSentServer(false);
                controller.processReactorError(error);
                controller.clearSubscriptions();
            }
        }
    }
}
