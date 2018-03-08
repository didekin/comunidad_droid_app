package com.didekindroid.lib_one.usuario.notification;

import android.app.Activity;
import android.view.View;

import com.didekindroid.lib_one.api.Viewer;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 09/03/17
 * Time: 21:11
 */
@SuppressWarnings("WeakerAccess")
public class ViewerNotifyToken extends Viewer<View, CtrlerNotifyTokenIf> implements
        ViewerNotifyTokenIf<View> {


    protected ViewerNotifyToken(Activity activity)
    {
        super(null, activity, null);
    }

    public static ViewerNotifyTokenIf<View> newViewerFirebaseToken(Activity activity)
    {
        Timber.d("newViewerFirebaseToken()");
        ViewerNotifyTokenIf<View> viewer = new ViewerNotifyToken(activity);
        viewer.setController(new CtrlerNotifyToken());
        return viewer;
    }

    @Override
    public void checkGcmTokenAsync()
    {
        Timber.d("checkGcmTokenAsync()");
        controller.checkGcmTokenAsync(new RegGcmTokenObserver());
    }

    // .............................. ViewerIf ..................................

    // ............................ SUBSCRIBERS ..................................

    public class RegGcmTokenObserver extends DisposableSingleObserver<Integer> {

        @Override
        public void onSuccess(Integer isUpdated)
        {
            Timber.d("onSuccess(%d)", isUpdated);
            if (isUpdated > 0) {
                controller.getTkCacher().updateIsGcmTokenSentServer(true);
            }
        }

        @Override
        public void onError(Throwable error)
        {
            Timber.d("onErrorObserver(): %s", error.getMessage());
            controller.getTkCacher().updateIsGcmTokenSentServer(false);
            onErrorInObserver(error);
        }
    }
}
