package com.didekindroid.usuario.beanfiller;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

/**
 * User: pedro@didekin
 * Date: 03/06/15
 * Time: 10:55
 */
public class UserAndComuFiller {

    public static UsuarioBean makeUsuarioBeanFromView(View usuarioRegView)
    {
        UsuarioBean usuarioBean = new UsuarioBean(
                ((TextView) usuarioRegView.findViewById(R.id.reg_usuario_email_editT)).getText()
                        .toString(),
                ((TextView) usuarioRegView.findViewById(R.id.reg_usuario_alias_ediT)).getText()
                        .toString(),
                ((TextView) usuarioRegView.findViewById(R.id.reg_usuario_password_ediT)).getText()
                        .toString(),
                ((TextView) usuarioRegView.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText()
                        .toString(),
                ((TextView) usuarioRegView.findViewById(R.id.reg_usuario_phone_prefix_ediT)).getText()
                        .toString(),
                ((TextView) usuarioRegView.findViewById(R.id.reg_usuario_phone_editT)).getText()
                        .toString()
        );

        return usuarioBean;
    }

    public static void makeComunidadBeanFromView(View comunidadSearchView, ComunidadBean comunidadBean)
    {
       comunidadBean.setNombreVia(((EditText) comunidadSearchView
               .findViewById(R.id.comunidad_nombre_via_editT)).getText().toString());
        comunidadBean.setNumeroString(((EditText) comunidadSearchView
                .findViewById(R.id.comunidad_numero_editT)).getText().toString());
        comunidadBean.setSufijoNumero(((EditText) comunidadSearchView
                .findViewById(R.id.comunidad_sufijo_numero_editT)).getText().toString());
    }

    public static UsuarioComunidadBean makeUsuarioComunidadBeanFromView(View usuarioComunidadRegView
            , ComunidadBean comunidadBean, UsuarioBean usuarioBean)
    {

        View usuarioRegView = usuarioComunidadRegView.findViewById(R.id.reg_usuario_include);

        UsuarioComunidadBean usuarioComunidadBean = new UsuarioComunidadBean(
                comunidadBean,
                usuarioBean,
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_portal_ed)).getText()
                        .toString(),
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_escalera_ed)).getText()
                        .toString(),
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_planta_ed)).getText()
                        .toString(),
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_puerta_ed)).getText()
                        .toString(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_pre))
                        .isChecked(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_admin))
                        .isChecked(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_pro))
                        .isChecked(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_inq))
                        .isChecked()
        );

        return usuarioComunidadBean;
    }
}
