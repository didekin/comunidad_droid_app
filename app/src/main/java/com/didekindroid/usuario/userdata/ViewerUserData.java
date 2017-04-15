package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.api.Viewer;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.UIutils;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:27
 */
class ViewerUserData extends Viewer<View, CtrlerUserDataIf> implements ViewerUserDataIf,
        ActivityInitiatorIf {

    private final EditText emailView;
    private final EditText aliasView;
    private final EditText passwordView;
    final AtomicReference<UsuarioBean> usuarioBean;
    private final AtomicReference<Intent> intentForMenu;
    final AtomicReference<Usuario> oldUser;
    final AtomicReference<Usuario> newUser;


    ViewerUserData(View view, Activity activity)
    {
        super(view, activity, null);
        emailView = (EditText) view.findViewById(R.id.reg_usuario_email_editT);
        aliasView = (EditText) view.findViewById(R.id.reg_usuario_alias_ediT);
        passwordView = (EditText) view.findViewById(R.id.user_data_ac_password_ediT);
        oldUser = new AtomicReference<>(null);
        newUser = new AtomicReference<>(null);
        usuarioBean = new AtomicReference<>(null);
        intentForMenu = new AtomicReference<>(null);
    }

    static ViewerUserDataIf newViewerUserData(UserDataAc activity){
        Timber.d("newViewerUserData()");
        ViewerUserDataIf instance = new ViewerUserData(activity.acView, activity);
        instance.setController(new CtrlerUserData(instance));
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
        controller.loadUserData();
    }

    @Override
    public void processBackUserDataLoaded(@NonNull Usuario usuario)
    {
        Timber.d("onSuccessUserDataLoaded()");

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
        intentForMenu.compareAndSet(
                null,
                new Intent().putExtra(user_name.key, oldUser.get().getUserName())
        );
        activity.invalidateOptionsMenu();
    }

    @Override
    public AtomicReference<Intent> getIntentForMenu()
    {
        Timber.d("getIntentForMenu()");
        return intentForMenu;
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
                return controller.modifyUser(oldUser.get(), newUser.get());
            default:
                return false;
        }
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
    {
        Timber.d("processControllerError()");
        if (ui.getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage())) {
            makeToast(activity, R.string.password_wrong);
            return null;
        }
        return super.processControllerError(ui);
    }

    @Override
    public void initActivity(Bundle bundle)
    {
       Timber.d("initActivityWithBundle()");
        ActivityInitiator.class.cast(activity).initActivityWithBundle(bundle);
    }
}
