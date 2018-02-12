package com.didekindroid.usuario.firebase;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.exception.UiExceptionRouterIf;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.router.UiExceptionRouter.uiException_router;

/**
 * User: pedro@didekin
 * Date: 09/03/17
 * Time: 21:11
 */
public class ViewerFirebaseToken extends Viewer<View, CtrlerFirebaseTokenIf> implements
        ViewerFirebaseTokenIf<View> {


    public static ViewerFirebaseTokenIf<View> newViewerFirebaseToken(AppCompatActivity activity)
    {
        Timber.d("newViewerFirebaseToken()");
        ViewerFirebaseTokenIf<View> viewer = new ViewerFirebaseToken(activity);
        viewer.setController(new CtrlerFirebaseToken());
        return viewer;
    }

    protected ViewerFirebaseToken(AppCompatActivity activity)
    {
        super(null, activity, null);
    }

    @Override
    public void checkGcmTokenAsync()
    {
        Timber.d("checkGcmTokenAsync()");
        controller.checkGcmTokenAsync(new RegGcmTokenObserver());
    }

    // .............................. ViewerIf ..................................

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return uiException_router;
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
