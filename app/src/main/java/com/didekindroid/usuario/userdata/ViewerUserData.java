package com.didekindroid.usuario.userdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.UIutils;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_name_uID_should_be_initialized;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:27
 */
final class ViewerUserData extends Viewer<View, CtrlerUserDataIf> implements ViewerUserDataIf {

    final EditText emailView;
    final EditText aliasView;
    final EditText passwordView;
    final AtomicReference<UsuarioBean> usuarioBean;
    final AtomicReference<Usuario> oldUser;
    final AtomicReference<Usuario> newUser;


    private ViewerUserData(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
        emailView = (EditText) view.findViewById(R.id.reg_usuario_email_editT);
        aliasView = (EditText) view.findViewById(R.id.reg_usuario_alias_ediT);
        passwordView = (EditText) view.findViewById(R.id.user_data_ac_password_ediT);
        oldUser = new AtomicReference<>(null);
        newUser = new AtomicReference<>(null);
        usuarioBean = new AtomicReference<>(null);
    }

    static ViewerUserData newViewerUserData(UserDataAc activity)
    {
        Timber.d("newViewerUserData()");
        ViewerUserData instance = new ViewerUserData(activity.acView, activity);
        instance.setController(new CtrlerUserModified());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        controller.loadUserData(new UserDataObserver<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario)
            {
                Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
                assertTrue(usuario.getuId() > 0L && usuario.getUserName() != null, user_name_uID_should_be_initialized);
                processBackUserDataLoaded(usuario);
            }
        });
    }

    @Override
    public void processBackUserDataLoaded(@NonNull Usuario usuario)
    {
        Timber.d("processBackUserDataLoaded()");

        oldUser.set(usuario);

        Button modifyButton = (Button) view.findViewById(R.id.user_data_modif_button);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClickLinkToImportanciaUsers()");
                if (checkUserData()) {
                    modifyUserData(whatDataChangeToMake());
                }
            }
        });

        emailView.setText(oldUser.get().getUserName());
        aliasView.setText(oldUser.get().getAlias());
        passwordView.setHint(R.string.user_data_ac_password_hint);
        activity.setIntent(new Intent().putExtra(user_name.key, oldUser.get().getUserName()));
    }

    /**
     * Preconditions:
     * 1. The user has typed and pressed modifyButton.
     */
    @Override
    public boolean checkUserData()
    {
        Timber.d("checkUserData()");

        usuarioBean.set(new UsuarioBean(
                emailView.getText().toString(),
                aliasView.getText().toString(),
                passwordView.getText().toString(),
                null)
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(activity);
        if (!usuarioBean.get().validateLoginData(activity.getResources(), errorBuilder)) {
            Timber.d("checkUserData(): %s", errorBuilder.toString());
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        return !UIutils.checkInternet(activity);
    }

    /**
     * Preconditions:
     * 1. Typed user data has been validated.
     */
    @Override
    public UserChangeToMake whatDataChangeToMake()
    {
        Timber.d("whatDataChangeToMake()");

        if (oldUser.get().getAlias().equals(usuarioBean.get().getAlias()) && oldUser.get().getUserName().equals(usuarioBean.get().getUserName())) {
            return nothing;
        }

        // Password in screen and old userName will be used for authentication.
        oldUser.set(new Usuario.UsuarioBuilder()
                .copyUsuario(oldUser.get())
                .password(usuarioBean.get().getPassword())
                .build());

        // Inicializo datos en nuevo usuario y añado PK.
        newUser.set(new Usuario.UsuarioBuilder()
                .copyUsuario(usuarioBean.get().getUsuario())
                .uId(oldUser.get().getuId())
                .build());

        if (!oldUser.get().getAlias().equals(newUser.get().getAlias())
                && oldUser.get().getUserName().equals(newUser.get().getUserName())) {
            return alias_only;
        }
        if (!oldUser.get().getUserName().equals(newUser.get().getUserName())) {
            return userName;
        }
        return nothing;
    }

    /**
     * @return true if the new subscription has been successfully added to the controller's set.
     */
    @Override
    public boolean modifyUserData(UserChangeToMake userChangeToMake)
    {
        Timber.d("modifyUserData()");
        switch (userChangeToMake) {
            case nothing:
                makeToast(activity, R.string.no_user_data_to_be_modified);
                return false;
            case userName:
            case alias_only:
                return controller.modifyUser(new UserDataObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isCompleted)
                    {
                        Timber.d("onSuccess(), isCompleted == %s", isCompleted.toString());
                        assertTrue(isCompleted, "UserDataObserver.onSuccess() should be TRUE");
                        replaceComponent(new Bundle());
                    }
                }, oldUser.get(), newUser.get());
            default:
                return false;
        }
    }

    @Override
    public ActionForUiExceptionIf onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        if (getUiExceptionFromThrowable(error).getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage())) {
            makeToast(activity, R.string.password_wrong);
            return null;
        }
        return super.onErrorInObserver(error);
    }

    public void replaceComponent(Bundle bundle)
    {
        Timber.d("initActivityWithBundle()");
        new ActivityInitiator(activity).initActivityWithBundle(bundle);
    }

    // .............................. SUBSCRIBERS ..................................

    abstract class UserDataObserver<T> extends DisposableSingleObserver<T> {

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorObserver(), Thread for subscriber: %s", Thread.currentThread().getName());
            onErrorInObserver(e);
        }
    }
}
