package com.didekindroid.usuario;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import com.didekindroid.common.IoHelper;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.comunidad.dominio.Comunidad;
import com.didekindroid.usuario.comunidad.dominio.Usuario;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;
import com.didekindroid.usuario.login.dominio.AccessToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.common.UserIntentExtras.USUARIO_COMUNIDAD_REG;
import static com.didekindroid.usuario.login.TokenHandler.TKhandler;
import static com.didekindroid.usuario.login.TokenHandler.refresh_token_filename;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * User: pedro
 * Date: 16/07/15
 * Time: 14:25
 */
@RunWith(AndroidJUnit4.class)
public class UserDataAcTest {

    UserDataAc mActivity;
    Context context;
    File refreshTkFile;

    @Rule
    public ActivityTestRule<UserDataAc> mActivityRule = new ActivityTestRule<>(UserDataAc.class, true, false);

    @Before
    public void setUp() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = new File(context.getFilesDir(), refresh_token_filename);
    }

    @Test
    public void testWithUserComunidad()
    {
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad("pedro@pedro.us", "psw_pedro", "pedro");
        Intent intent = new Intent();
        intent.putExtra(USUARIO_COMUNIDAD_REG.extra, usuarioComunidad);

        assertThat(refreshTkFile.exists(), is(false));
        mActivity = mActivityRule.launchActivity(intent);

        assertThat(mActivity, notNullValue());
        onView(withId(R.id.reg_usuario_layout)).check(matches(isDisplayed()));

        assertThat(refreshTkFile.exists(), is(true));
        AccessToken tokenPedro = TKhandler.getAccessTokenInCache();
        assertThat(tokenPedro,notNullValue());
        assertThat(IoHelper.readStringFromFile(refreshTkFile), is(tokenPedro.getRefresh_token()));
    }

    @Test
    public void testWithoutUserComunidad()
    {
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad("juan@juan.us", "psw_juan", "juan");
        Usuario usuario = ServOne.signUp(usuarioComunidad);

        AccessToken token = ServOne.getPasswordUserToken(usuario.getUserName(), usuario.getPassword());

        assertThat(refreshTkFile.exists(), is(false));
        TKhandler.initKeyCacheAndBackupFile(token);

        assertThat(refreshTkFile.exists(), is(true));
        AccessToken tokenJuan = TKhandler.getAccessTokenInCache();
        assertThat(tokenJuan,notNullValue());
        assertThat(IoHelper.readStringFromFile(refreshTkFile),is(tokenJuan.getRefresh_token()));

        mActivity = mActivityRule.launchActivity(new Intent());
        onView(withId(R.id.reg_usuario_layout)).check(matches(isDisplayed()));

        // TODO: lanzar la actividad y hacer aserciones.
    }

    @After
    public void tearDown() throws Exception
    {
        if (refreshTkFile.exists()) {
            refreshTkFile.delete();
        }
        boolean isDeleted = ServOne.deleteUser();
    }

//    ...........  AUXLIARY METHODS ............

    private UsuarioComunidad makeUsuarioComunidad(String userName, String password, String alias)
    {
        Comunidad comunidad = new Comunidad("Calle", "Real", (short) 5, "Bis",
                new Municipio(new Provincia((short) 3), (short) 13));
        Usuario usuario = new Usuario(userName, alias, password, (short) 0, 0);
        return new UsuarioComunidad(comunidad, usuario, "portal", "esc", "plantaX",
                "door", "pro");
    }
}