package com.didekindroid.repository;

/**
 * User: pedro@didekindroid
 * Date: 05/05/15
 * Time: 09:47
 */
public enum SqLiteTypes {

    TEXT(" TEXT"),

    NUM(" NUMERIC"),

    INT(" INTEGER"),

    REAL(" REAL"),

    NONE(" NONE");

    public final String name;

    SqLiteTypes(String name)
    {
        this.name = name;
    }
}
