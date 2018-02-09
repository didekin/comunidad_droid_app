package com.didekindroid.usuario.firebase;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.lib_one.api.Viewer;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 09/03/17
 * Time: 21:11
 */
public class ViewerFirebaseToken extends Viewer<View, CtrlerFirebaseTokenIf> implements
        ViewerFirebaseTokenIf<View> {


    protected ViewerFirebaseToken(AppCompatActivity activity)
    {
        super(null, activity, null);
    }

    public static ViewerFirebaseTokenIf<View> newViewerFirebaseToken(AppCompatActivity activity)
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
