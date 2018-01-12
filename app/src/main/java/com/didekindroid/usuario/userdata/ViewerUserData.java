package com.didekindroid.usuario.userdata;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.AbstractSingleObserver;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.usuario.dao.CtrlerUsuario;
import com.didekindroid.usuario.dao.CtrlerUsuarioIf;
import com.didekindroid.usuariocomunidad.register.PasswordSentDialog;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.afterModifiedUserAlias;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkInternet;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:27
 */
final class ViewerUserData extends Viewer<View, CtrlerUsuarioIf> implements ViewerUserDataIf, ActivityInitiatorIf {

    final EditText emailView;
    final EditText aliasView;
    final EditText passwordView;
    final AtomicReference<UsuarioBean> usuarioBean;
    final AtomicReference<Usuario> oldUser;
    final AtomicReference<Usuario> newUser;


    private ViewerUserData(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
        emailView = view.findViewById(R.id.reg_usuario_email_editT);
        aliasView = view.findViewById(R.id.reg_usuario_alias_ediT);
        passwordView = view.findViewById(R.id.password_validation_ediT);
        oldUser = new AtomicReference<>(null);
        newUser = new AtomicReference<>(null);
        usuarioBean = new AtomicReference<>(null);
    }

    static ViewerUserData newViewerUserData(UserDataAc activity)
    {
        Timber.d("newViewerUserData()");
        ViewerUserData instance = new ViewerUserData(activity.acView, activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        controller.loadUserData(new AbstractSingleObserver<Usuario>(this) {
            @Override
            public void onSuccess(Usuario usuario)
            {
                processBackUserDataLoaded(usuario);
            }
        });
    }

    @Override
    public void processBackUserDataLoaded(@NonNull Usuario usuario)
    {
        Timber.d("processBackUserDataLoaded()");

        oldUser.set(usuario);

        Button modifyButton = view.findViewById(R.id.user_data_modif_button);
        modifyButton.setOnClickListener(v -> {
            if (checkUserData()) {
                modifyUserData(whatDataChangeToMake());
            }
        });

        emailView.setText(oldUser.get().getUserName());
        aliasView.setText(oldUser.get().getAlias());
        passwordView.setHint(R.string.user_data_ac_password_hint);
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
        Timber.d("email: %s", usuarioBean.get().getUserName());
        Timber.d("alias: %s", usuarioBean.get().getAlias());

        StringBuilder errorBuilder = getErrorMsgBuilder(activity);
        if (!usuarioBean.get().validateUserNameAliasPswd(activity.getResources(), errorBuilder)) {
            Timber.d("checkUserData(): %s", errorBuilder.toString());
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        return checkInternet(activity);
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
                return controller.modifyUserName(
                        new AbstractSingleObserver<Boolean>(this) {
                            @Override
                            public void onSuccess(Boolean isCompleted)
                            {
                                Timber.d("onSuccess(), isCompleted == %s", isCompleted.toString());
                                DialogFragment newFragment = PasswordSentDialog.newInstance(newUser.get());
                                newFragment.show(activity.getFragmentManager(), "passwordMailDialog");
                            }
                        },
                        oldUser.get(),
                        newUser.get());
            case alias_only:
                return controller.modifyUserAlias(
                        new AbstractSingleObserver<Boolean>(this) {
                            @Override
                            public void onSuccess(Boolean isCompleted)
                            {
                                Timber.d("onSuccess(), isCompleted == %s", isCompleted.toString());
                                assertTrue(isCompleted, "AbstractSingleObserver.onSuccess() should be TRUE");
                                initAcFromRouter(null, afterModifiedUserAlias);
                            }
                        },
                        oldUser.get(),
                        newUser.get());
            default:
                return false;
        }
    }

    // ================================= Viewer callbacks ==================================

    @SuppressWarnings("ThrowableNotThrown")
    @Override
    public void onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        if (getUiExceptionFromThrowable(error).getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage())) {
            makeToast(activity, R.string.password_wrong);
        } else {
            super.onErrorInObserver(error);
        }
    }
}
