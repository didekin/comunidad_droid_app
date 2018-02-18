package com.didekindroid.lib_one.usuario;

import org.junit.Test;

import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 17:34
 */
public class UsuarioMockDaoTest {

    @Test
    public void test_DeleteUser() throws Exception
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(UserTestData.comu_real_rodrigo).execute().body(), is(true));
        assertThat(usuarioMockDao.deleteUser(user_crodrigo.getUserName()).execute().body(), is(true));
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws Exception
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(UserTestData.comu_real_rodrigo).execute().body(), is(true));
        cleanOneUser(user_crodrigo);
    }
}