package com.didekindroid.router;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.lib_one.usuario.UserTestData;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestData;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.usuario.UserContextualName.default_no_reg_user;
import static com.didekindroid.lib_one.usuario.UserContextualName.user_name_just_modified;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.router.ContextualAction.searchForComu;
import static com.didekindroid.router.ContextualAction.showPswdSentMessage;
import static com.didekindroid.router.ContextualRouter.context_router;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/02/2018
 * Time: 15:08
 */
@RunWith(AndroidJUnit4.class)
public class ContextualActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    private ActivityMock activity;
    private ContextualRouterIf router = context_router;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @Test
    public void test_default_no_reg_user() throws Exception
    {
        RouterActionIf action = router.getActionFromContextNm(default_no_reg_user);
        assertThat(action, is(searchForComu));

        activity.runOnUiThread(() -> action.initActivity(activity, null, 0));
        waitAtMost(2, SECONDS).until(() -> activity.isFinishing());
    }

    @Test
    public void test_user_name_just_modified() throws Exception
    {
        RouterActionIf action = router.getActionFromContextNm(user_name_just_modified);
        assertThat(action, is(showPswdSentMessage));

        Bundle bundle = new Bundle(1);
        bundle.putSerializable(usuario_object.key, signUpAndUpdateTk(COMU_ESCORIAL_JUAN));
        activity.runOnUiThread(() -> action.initActivity(activity, bundle, 0));
        checkTextsInDialog(R.string.receive_password_by_mail_dialog, R.string.continuar_button_rot);

        cleanOptions(CLEAN_JUAN);
    }
}