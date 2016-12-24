package com.didekinaar.usuario.login;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 10:30
 */
interface LoginControllerIf {
    int getCounterWrong();
    void setCounterWrong(int counterWrong);
    void doLoginValidate();
    void doDialogPositiveClick(String email);
    void doDialogNegativeClick();
}
