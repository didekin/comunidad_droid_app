package com.didekindroid.exception;

/**
 * User: pedro@didekin
 * Date: 08/10/15
 * Time: 11:08
 */

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.didekindroid.usuario.activity.ComuSearchAc;
import com.didekindroid.usuario.activity.LoginAc;

import static com.didekindroid.utils.UIutils.isRegisteredUser;
import static com.didekindroid.utils.UIutils.makeToast;

/**
 * Exceptions to be dealt with in the user interface
 */
public class UiException extends Exception {

    private final UiAction action;
    private final int resourceId;

    public UiException(UiAction uiAction, int resourceId)
    {
        action = uiAction;
        this.resourceId = resourceId;
    }

    public UiAction getAction()
    {
        return action;
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public enum UiAction {

        LOGIN {
            @Override
            public void doAction(Activity activity, int toastResource)
            {
                if (isRegisteredUser(activity)) {
                    Intent intent = new Intent(activity, LoginAc.class);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    SEARCH_COMU.doAction(activity, toastResource);
                }
            }
        },
        SEARCH_COMU {
            @Override
            public void doAction(Activity activity, int toastResource)
            {
                makeToast(activity, toastResource, Toast.LENGTH_SHORT);
                Intent intent = new Intent(activity, ComuSearchAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        },;

        public abstract void doAction(Activity activity, int toastResource);
    }
}
