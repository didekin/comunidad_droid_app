package com.didekindroid.usuario.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.didekindroid.api.Controller;
import com.didekindroid.security.IdentityCacher;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.security.IdentityCacher.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 14:23
 */
@SuppressWarnings({"WeakerAccess", "AnonymousInnerClassMayBeStatic"})
public class CtrlerFirebaseToken extends Controller implements CtrlerFirebaseTokenIf {

    private final ViewerFirebaseTokenIf<View> viewer;


    public CtrlerFirebaseToken(ViewerFirebaseTokenIf<View> viewer)
    {
        this(viewer, TKhandler);
    }

    CtrlerFirebaseToken(ViewerFirebaseTokenIf<View> viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
        this.viewer = viewer;
    }

    //    .................................... OBSERVABLES .................................

    /**
     * Postconditions: the user's gcm token in database is updated.
     *
     * @return a Single with an item == 1 if the gcmToken is updated.
     */
    static Single<Integer> updatedGcmTkSingle()
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

    //    .................................... INSTANCE METHODS .................................

    @Override
    public boolean isGcmTokenSentServer()
    {
        Timber.d("isGcmTokenSentServer()");
        Context context = viewer.getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        return sharedPref.getBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, false);
    }


    @Override
    public void updateIsGcmTokenSentServer(boolean isSentToServer)
    {
        Timber.d("updateIsGcmTokenSentServer(), isSentToServer = %b", isSentToServer);
        assertTrue(isRegisteredUser(), user_should_be_registered);
        Context context = viewer.getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, isSentToServer);
        editor.apply();
    }

    @Override
    public boolean checkGcmToken()
    {
        Timber.d("checkGcmToken()");
        return identityCacher.isRegisteredUser()
                && !isGcmTokenSentServer()
                && subscriptions.add(
                updatedGcmTkSingle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribeWith(new RegGcmTokenObserver(this)));
    }

    /**
     * Synchronous variant for the service InstanceIdService.
     * The method does not check if the gcmToken has been sent previously to database.
     */
    @Override
    public boolean checkGcmTokenSync()
    {
        Timber.d("checkGcmTokenSync()");

        return identityCacher.isRegisteredUser() && subscriptions.add(updatedGcmTkSingle().subscribeWith(new RegGcmTokenObserver(this) {
            @Override
            public void onError(Throwable error)
            {
                Timber.d("onErrorCtrl(): %s", error.getMessage());
            }
        }));
    }

    // ............................ SUBSCRIBERS ..................................

    static class RegGcmTokenObserver extends DisposableSingleObserver<Integer> {

        private final CtrlerFirebaseTokenIf controller;

        RegGcmTokenObserver(CtrlerFirebaseTokenIf controller)
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
        }

        @Override
        public void onError(Throwable error)
        {
            Timber.d("onErrorCtrl(): %s", error.getMessage());
            controller.updateIsGcmTokenSentServer(false);
            controller.onErrorCtrl(error);
        }
    }
}
