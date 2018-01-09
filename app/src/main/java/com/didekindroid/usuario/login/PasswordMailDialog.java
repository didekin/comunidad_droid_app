package com.didekindroid.usuario.login;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;

import com.didekindroid.R;
import com.didekindroid.usuario.UsuarioBean;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioBundleKey.usuario_object;

/**
 * User: pedro@didekin
 * Date: 19/12/2017
 * Time: 13:16
 */
public class PasswordMailDialog extends DialogFragment {

    public static PasswordMailDialog newInstance(UsuarioBean usuarioBean)
    {
        Timber.d("newInstance()");
        PasswordMailDialog dialog = new PasswordMailDialog();
        Bundle bundle = new Bundle();
        if (usuarioBean != null && usuarioBean.getUsuario() != null) {
            bundle.putSerializable(usuario_object.key, usuarioBean.getUsuario());
        }
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
    {
        Timber.d("onCreateDialog()");
        final ViewerLogin viewerLogin = ((LoginAc) getActivity()).viewerLogin;
        AlertDialog.Builder builder = new AlertDialog.Builder(viewerLogin.getActivity(), R.style.alertDialogTheme);

        builder.setMessage(R.string.send_password_by_mail_dialog)
                .setPositiveButton(
                        R.string.send_password_by_mail_YES,
                        (dialog, id) -> {
                            dialog.dismiss();
                            viewerLogin.doDialogPositiveClick((Usuario) getArguments().getSerializable(usuario_object.key));
                        }
                );
        return builder.create();
    }
}
