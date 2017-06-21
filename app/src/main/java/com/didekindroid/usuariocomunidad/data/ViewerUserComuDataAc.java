package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.ViewerParent;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserComuFr;
import com.didekindroid.util.ConnectionUtils;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.userComu_should_be_deleted;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.userComu_should_be_modified;
import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.UsuarioServConstant.IS_USER_DELETED;

/**
 * User: pedro@didekin
 * Date: 01/06/17
 * Time: 09:27
 */

final class ViewerUserComuDataAc extends ViewerParent<View, CtrlerUsuarioComunidad> {

    @SuppressWarnings("WeakerAccess")
    UsuarioComunidad userComuIntent;
    Menu acMenu;

    private ViewerUserComuDataAc(View view, AppCompatActivity activity)
    {
        super(view, activity);
    }

    static ViewerUserComuDataAc newViewerUserComuDataAc(UserComuDataAc activity)
    {
        Timber.d("newViewerUserComuDataAc()");
        ViewerUserComuDataAc instance = new ViewerUserComuDataAc(activity.acView, activity);
        instance.setController(new CtrlerUsuarioComunidad());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, @NonNull Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        userComuIntent = UsuarioComunidad.class.cast(viewBean);

        Button mModifyButton = (Button) view.findViewById(R.id.usercomu_data_ac_modif_button);
        mModifyButton.setOnClickListener(new ModifyButtonListener());

        Button mDeleteButton = (Button) view.findViewById(R.id.usercomu_data_ac_delete_button);
        mDeleteButton.setOnClickListener(new DeleteButtonListener());
    }

    // .............................. HELPERS ..................................

    /**
     * Option 'comu_data_ac_mn' is only visible if the user is the oldest (oldest fecha_alta) UsuarioComunidad in
     * this comunidad, or has the roles adm or pre.
     * <p/>
     */
    @SuppressWarnings("WeakerAccess")
    void updateActivityMenu()
    {
        Timber.d("updateActivityMenu()");
        MenuItem comuDataItem = acMenu.findItem(R.id.comu_data_ac_mn);
        comuDataItem.setVisible(true);
        comuDataItem.setEnabled(true);
        activity.onPrepareOptionsMenu(acMenu);
    }

    /**
     * This method is called from the activity.
     */
    void setAcMenu(Menu menu)
    {
        Timber.d("setAcMenu()");
        assertTrue(menu != null, bean_fromView_should_be_initialized);
        acMenu = menu;
        controller.checkIsOldestAdmonUser(new AcMenuObserver(), userComuIntent.getComunidad());
    }

    // .............................. LISTENERS ..................................

    @SuppressWarnings("WeakerAccess")
    class ModifyButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            UsuarioComunidad usuarioComunidad =
                    getChildViewer(ViewerRegUserComuFr.class).getUserComuFromViewer(errorBuilder, userComuIntent.getComunidad(), null);
            if (usuarioComunidad == null) {
                makeToast(activity, errorBuilder.toString());
            } else if (!ConnectionUtils.isInternetConnected(activity)) {
                makeToast(activity, R.string.no_internet_conn_toast);
            } else {
                controller.modifyUserComu(new ModifyUserComuObserver(), usuarioComunidad);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class DeleteButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            controller.deleteUserComu(new DeleteUserComuObserver(), userComuIntent.getComunidad());
        }
    }

// .............................. SUBSCRIBERS ..................................

    @SuppressWarnings("WeakerAccess")
    class AcMenuObserver extends UserComuDataObserver<Boolean> {
        @Override
        public void onSuccess(Boolean isOldestUser)
        {
            Timber.d("onSuccess()");
            if (isOldestUser) {
                updateActivityMenu();
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class ModifyUserComuObserver extends UserComuDataObserver<Integer> {
        @Override
        public void onSuccess(Integer rowsUpdated)
        {
            Timber.d("onSuccess()");
            assertTrue(rowsUpdated == 1, userComu_should_be_modified);
            new ActivityInitiator(activity).initAcWithBundle(new Bundle(0));
        }
    }

    @SuppressWarnings("WeakerAccess")
    class DeleteUserComuObserver extends UserComuDataObserver<Integer> {
        @Override
        public void onSuccess(Integer rowsUpdated)
        {
            Timber.d("onSuccess()");
            boolean isUserDeleted = rowsUpdated == IS_USER_DELETED;
            boolean isUserComuDeleted = rowsUpdated == 1;
            assertTrue(isUserDeleted || isUserComuDeleted, userComu_should_be_deleted);
            if (isUserDeleted) {
                Intent intent = new Intent(activity, ComuSearchAc.class);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else {
                new ActivityInitiator(activity).initAcWithBundle(new Bundle(0));
            }
        }
    }

    abstract class UserComuDataObserver<T> extends DisposableSingleObserver<T> {
        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorObserver(), Thread: %s", Thread.currentThread().getName());
            onErrorInObserver(e);
        }
    }
}
