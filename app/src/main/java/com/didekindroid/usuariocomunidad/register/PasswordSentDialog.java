package com.didekindroid.usuariocomunidad.register;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_name_should_be_initialized;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 16/11/2017
 * Time: 12:42
 */
public class PasswordSentDialog extends DialogFragment {

    public static PasswordSentDialog newInstance(Usuario usuario)
    {
        Timber.d("newInstance()");

        PasswordSentDialog sentDialog = new PasswordSentDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(usuario_object.key, usuario);
        sentDialog.setArguments(bundle);
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
                            new ActivityInitiator(getActivity()).initAcWithBundle(bundle);
                        }
                );
        return builder.create();
    }
}
