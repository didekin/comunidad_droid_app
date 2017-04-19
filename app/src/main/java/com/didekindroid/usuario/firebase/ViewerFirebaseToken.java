package com.didekindroid.usuario.firebase;

import android.app.Activity;
import android.view.View;

import com.didekindroid.api.Viewer;

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
        ViewerFirebaseTokenIf<View> viewer = new ViewerFirebaseToken(activity);
        viewer.setController(new CtrlerFirebaseToken(viewer));
        return viewer;
    }

    @Override
    public void checkGcmTokenAsync()
    {
        controller.checkGcmToken();
    }
}
