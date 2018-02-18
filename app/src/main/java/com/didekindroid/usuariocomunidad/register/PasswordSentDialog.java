package com.didekindroid.usuariocomunidad.register;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;

import com.didekindroid.R;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.UserContextualName.pswd_just_sent_to_user;
import static com.didekindroid.lib_one.usuario.UsuarioAssertionMsg.user_name_should_be_initialized;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 16/11/2017
 * Time: 12:42
 */
public class PasswordSentDialog extends DialogFragment {

    public static PasswordSentDialog newInstance(Bundle usuarioBundle)
    {
        Timber.d("newInstance()");

        PasswordSentDialog sentDialog = new PasswordSentDialog();
        sentDialog.setArguments(usuarioBundle);
        return sentDialog;
    }

    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
    {
        Timber.d("onCreateDialog()");
        // Preconditions
        Usuario usuarioArg = (Usuario) getArguments().getSerializable(usuario_object.key);
        assertTrue(usuarioArg != null && usuarioArg.getUserName() != null, user_name_should_be_initialized);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogTheme);

        builder.setMessage(R.string.receive_password_by_mail_dialog)
                .setPositiveButton(
                        R.string.continuar_button_rot,
                        (dialog, id) -> {
                            dialog.dismiss();
                            Bundle bundle = new Bundle(1);
                            bundle.putString(user_name.key, usuarioArg.getUserName());
                            routerInitializer.get().getContextRouter().getActionFromContextNm(pswd_just_sent_to_user)
                                    .initActivity(getActivity(), bundle);
                        }
                );
        return builder.create();
    }
}
