package com.didekindroid.usuario.firebase;

import android.content.SharedPreferences;

import com.didekindroid.api.Controller;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.security.IdentityCacher.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 14:23
 */
@SuppressWarnings({"WeakerAccess", "AnonymousInnerClassMayBeStatic"})
public class CtrlerFirebaseToken extends Controller implements CtrlerFirebaseTokenIf {

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
        SharedPreferences sharedPref = getIdentityCacher().getContext().getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        return sharedPref.getBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, false);
    }


    @Override
    public void updateIsGcmTokenSentServer(boolean isSentToServer)
    {
        Timber.d("updateIsGcmTokenSentServer(), isSentToServer = %b", isSentToServer);
        assertTrue(isRegisteredUser(), user_should_be_registered);
        SharedPreferences sharedPref = getIdentityCacher().getContext().getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, isSentToServer);
        editor.apply();
    }

    @Override
    public boolean checkGcmTokenAsync(DisposableSingleObserver<Integer> observer)
    {
        Timber.d("checkGcmTokenAsync()");
        return identityCacher.isRegisteredUser()
                && !isGcmTokenSentServer()
                && subscriptions.add(
                updatedGcmTkSingle()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }

    /**
     * Synchronous variant for the service InstanceIdService.
     * The method does not check if the gcmToken has been sent previously to database.
     *
     * @param observer is instantiated by the viewer who calls the controller.
     */
    @Override
    public boolean checkGcmTokenSync(DisposableSingleObserver<Integer> observer)
    {
        Timber.d("checkGcmTokenSync()");
        return identityCacher.isRegisteredUser() && subscriptions.add(updatedGcmTkSingle().subscribeWith(observer));
    }
}
