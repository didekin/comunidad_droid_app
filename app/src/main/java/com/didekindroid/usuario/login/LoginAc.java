package com.didekindroid.usuario.login;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.router.ComponentReplacerIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.router.ActivityRouter;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * User: pedro
 * Date: 15/12/14
 * Time: 10:04
 * <p>
 * Preconditions:
 * 1. The user is not necessarily registered: she might have erased the security app data.
 * Results:
 * 1a. If successful, the activity ComuSearchAc is presented and the security data are updated.
 * 1b. If the userName doesn't exist, the user is invited to register.
 * 1c. If the userName exists, but the passowrd is not correct, after three failed intents,  a new passord is sent
 * by mail, after her confirmation.
 */
public class LoginAc extends AppCompatActivity implements ComponentReplacerIf {

    View acView;
    ViewerLoginIf viewerLogin;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.i("Entered onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.login_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewerLogin = ViewerLogin.newViewerLogin(this);
        viewerLogin.doViewInViewer(savedInstanceState, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(savedState);
        viewerLogin.saveState(savedState);
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewerLogin.clearSubscriptions();
    }

    @Override
    public void replaceComponent(Bundle bundle)
    {
        Timber.d("initActivityWithBundle()");
        new ActivityInitiator(this).initActivityWithFlag(bundle, FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                ActivityRouter.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ============================================================
    //    ................ ERROR DIALOG .................
    // ============================================================

    public static class PasswordMailDialog extends DialogFragment {

        public static PasswordMailDialog newInstance(Usuario usuario)
        {
            Timber.d("newInstance()");
            PasswordMailDialog dialog = new PasswordMailDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable(usuario_object.key, usuario);
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
        {
            Timber.d("onCreateDialog()");
            return ((LoginAc) getActivity()).viewerLogin.doDialogInViewer(this);
        }
    }
}
