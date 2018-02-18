package com.didekindroid.lib_one.usuario.notification;

import com.didekindroid.lib_one.api.Controller;
import com.google.firebase.iid.FirebaseInstanceId;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 14:23
 */
@SuppressWarnings({"WeakerAccess", "AnonymousInnerClassMayBeStatic"})
public class CtrlerNotifyToken extends Controller implements CtrlerNotifyTokenIf {

    //    .................................... OBSERVABLES .................................

    /**
     * Postconditions: the user's gcm token in database is updated.
     *
     * @return a Single with an item == 1 if the gcmToken is updated.
     */
    static Single<Integer> updatedGcmTkSingle()
    {
        return Single.fromCallable(() -> {
                    String token = FirebaseInstanceId.getInstance().getToken();
                    return usuarioDaoRemote.modifyUserGcmToken(token);
                }
        );
    }

    //    .................................... INSTANCE METHODS .................................


    @Override
    public boolean checkGcmTokenAsync(DisposableSingleObserver<Integer> observer)
    {
        Timber.d("checkGcmTokenAsync()");
        return getTkCacher().isRegisteredUser()
                && !getTkCacher().isGcmTokenSentServer()
                && getSubscriptions().add(
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
        return getTkCacher().isRegisteredUser() && getSubscriptions().add(updatedGcmTkSingle().subscribeWith(observer));
    }
}
