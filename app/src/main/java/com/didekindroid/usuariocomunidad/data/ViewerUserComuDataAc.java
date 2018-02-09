package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.lib_one.api.ParentViewerInjected;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserComuFr;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.LeadRouter.afterModifiedUserComu;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.userComu_should_be_deleted;
import static com.didekinlib.http.usuario.UsuarioServConstant.IS_USER_DELETED;

/**
 * User: pedro@didekin
 * Date: 01/06/17
 * Time: 09:27
 */

final class ViewerUserComuDataAc extends ParentViewerInjected<View, CtrlerUsuarioComunidad> {

    @SuppressWarnings("WeakerAccess")
    UsuarioComunidad userComuIntent;
    Menu acMenu;
    AtomicBoolean showComuDataMn = new AtomicBoolean(false);

    ViewerUserComuDataAc(View view, AppCompatActivity activity)
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

        Button mModifyButton = view.findViewById(R.id.usercomu_data_ac_modif_button);
        mModifyButton.setOnClickListener(new ModifyButtonListener());

        Button mDeleteButton = view.findViewById(R.id.usercomu_data_ac_delete_button);
        mDeleteButton.setOnClickListener(new DeleteButtonListener());
    }

    // .............................. HELPERS ..................................

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
                controller.modifyUserComu(new ModifyUserComuObserver(usuarioComunidad.hasAdministradorAuthority()), usuarioComunidad);
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

        /**
         * Postcondition: flag for showing the ComunidadDataAc menu option is initialized.
         */
        @Override
        public void onSuccess(Boolean hasComuDataModPower)
        {
            Timber.d("onSuccess()");
            showComuDataMn.set(hasComuDataModPower);
        }
    }

    @SuppressWarnings("WeakerAccess")
    class ModifyUserComuObserver extends UserComuDataObserver<Integer> {

        /**
         * This variable flags the adm powers of the user.
         */
        private final boolean upDateMenu;

        ModifyUserComuObserver(boolean upDateMenu)
        {
            this.upDateMenu = upDateMenu;
        }

        /**
         * Postcondition: flag for showing the ComunidadDataAc menu option is updated.
         * It may happen that if a user change her roles to NO ADM roles, although she continues to be the oldest user,
         * the flag would signal NOT to show the ComunidadDataAc menu option.
         */
        @Override
        public void onSuccess(Integer rowsUpdated)
        {
            Timber.d("onSuccess()");
            showComuDataMn.set(rowsUpdated == 1 && upDateMenu);
            Timber.d("Update menu = %b", showComuDataMn.get());
            afterModifiedUserComu.initActivity(activity);
        }
    }

    @SuppressWarnings("WeakerAccess")
    class DeleteUserComuObserver extends UserComuDataObserver<Integer> {
        @Override
        public void onSuccess(Integer rowsUpdated)
        {
            Timber.d("onSuccess()");
            boolean isUserDeleted = (rowsUpdated == IS_USER_DELETED);
            boolean isUserComuDeleted = (rowsUpdated == 1);
            assertTrue(isUserDeleted || isUserComuDeleted, userComu_should_be_deleted);
            if (isUserDeleted) {
                Intent intent = new Intent(activity, ComuSearchAc.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else {
                afterModifiedUserComu.initActivity(activity);
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
