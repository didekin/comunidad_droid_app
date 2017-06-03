package com.didekindroid.usuario.firebase;

import android.app.Activity;
import android.view.View;

import com.didekindroid.api.Viewer;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 09/03/17
 * Time: 21:11
 */
public class ViewerFirebaseToken extends Viewer<View, CtrlerFirebaseTokenIf> implements
        ViewerFirebaseTokenIf<View> {


    protected ViewerFirebaseToken(Activity activity)
    {
        super(null, activity, null);
    }

    public static ViewerFirebaseTokenIf<View> newViewerFirebaseToken(Activity activity)
    {
        Timber.d("newViewerFirebaseToken()");
        ViewerFirebaseTokenIf<View> viewer = new ViewerFirebaseToken(activity);
        viewer.setController(new CtrlerFirebaseToken());
        return viewer;
    }

    @Override
    public void checkGcmTokenAsync()
    {
        Timber.d("checkGcmTokenAsync()");
        controller.checkGcmTokenAsync(new RegGcmTokenObserver());
    }

    // ............................ SUBSCRIBERS ..................................

    @SuppressWarnings("WeakerAccess")
    public class RegGcmTokenObserver extends DisposableSingleObserver<Integer> {

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
            Timber.d("onErrorObserver(): %s", error.getMessage());
            controller.updateIsGcmTokenSentServer(false);
            onErrorInObserver(error);
        }
    }
}
