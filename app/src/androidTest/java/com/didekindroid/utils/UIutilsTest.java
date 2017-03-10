package com.didekindroid.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.didekindroid.util.UIutils;
import com.didekindroid.R;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.M;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.IdentityCacher.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.util.UIutils.SPAIN_LOCALE;
import static com.didekindroid.util.UIutils.formatDoubleZeroDecimal;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static com.didekindroid.util.UIutils.formatTimeToString;
import static com.didekindroid.util.UIutils.getIntFromStringDecimal;
import static java.text.DateFormat.LONG;
import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getDateInstance;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.getDefault;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 31/03/15
 * Time: 15:16
 */
public class UIutilsTest {

    private Context context;
    @Before
    public void setUp()
    {
        context = creator.get().getContext();
    }

    @Test
    public void testFormatTimeStampToString_1()
    {
        Timestamp timestamp = new Timestamp(1455301148000L);

        if (getDefault().equals(SPAIN_LOCALE)) {
            if (SDK_INT >= M) {
                assertThat(formatTimeStampToString(timestamp), is("12 feb. 2016"));
            }
            if (SDK_INT < M && SDK_INT > KITKAT) {
                assertThat(formatTimeStampToString(timestamp), is("12/2/2016"));
            }
            if (SDK_INT == KITKAT) {
                assertThat(formatTimeStampToString(timestamp), is("12/02/2016"));
            }
        }

        if (getDefault().equals(ENGLISH)) {
            assertThat(formatTimeStampToString(timestamp), is("Feb 12, 2016"));
        }
    }

    @Test
    public void testFormatTimeStampToString_2()
    {
        // Verifico el método de validación de la fecha prevista por defecto para una resolución.
        long timestampLong = new Timestamp(1455301148000L).getTime();
        String fechaViewStr = context.getString(R.string.incid_resolucion_fecha_default_txt);
        assertThat(fechaViewStr.equals(formatTimeToString(timestampLong)), is(false));
        assertThat(fechaViewStr.equals(formatTimeToString(0L)), is(false));
        assertThat(fechaViewStr.equals(formatTimeToString(0)), is(false));
    }

    @Test
    public void testFormatTimeStampToString_3()
    {
        // Tests genéricos del API Java.

        Timestamp timestamp = new Timestamp(1455301148000L);

        String formatTime = getDateInstance(LONG, SPAIN_LOCALE).format(timestamp);
        assertThat(formatTime, is("12 de febrero de 2016"));

        if (SDK_INT >= M) {
            formatTime = getDateInstance(MEDIUM, SPAIN_LOCALE).format(timestamp);
            assertThat(formatTime, is("12 feb. 2016"));
        }
        if (SDK_INT < M && SDK_INT > KITKAT) {
            formatTime = getDateInstance(MEDIUM, SPAIN_LOCALE).format(timestamp);
            assertThat(formatTime, is("12/2/2016"));
        }
        if (SDK_INT == KITKAT) {
            formatTime = getDateInstance(MEDIUM, SPAIN_LOCALE).format(timestamp);
            assertThat(formatTime, is("12/02/2016"));
        }

        formatTime = getDateInstance(LONG, ENGLISH).format(timestamp);
        assertThat(formatTime, is("February 12, 2016"));

        formatTime = getDateInstance(MEDIUM, ENGLISH).format(timestamp);
        assertThat(formatTime, is("Feb 12, 2016"));
    }

    @Test
    public void testFormatDoubleToZeroDecimal()
    {
        Locale locale = getLocale();

        if (locale.equals(ENGLISH) || locale.equals(ENGLISH)) {
            assertThat(formatDoubleZeroDecimal(12.34, context), is("12"));
        }
    }

    @Test
    public void testFormatDoubleToTwoDecimal()
    {
        Locale locale = getLocale();

        if (locale.equals(SPAIN_LOCALE) || locale.equals(ENGLISH)) {
            assertThat(UIutils.formatDoubleTwoDecimals(12.34, context),
                    is("12".concat(context.getString(R.string.decimal_separator)).concat("34")));
            assertThat(UIutils.formatDoubleTwoDecimals(12.342, context),
                    is("12".concat(context.getString(R.string.decimal_separator)).concat("342")));
        }
    }

    @Test
    public void testGetIntFromStringDecimal() throws ParseException
    {
        if (getDefault().equals(ENGLISH)) {
            assertThat(getIntFromStringDecimal("123.45"), is(123));
        }
        if (getDefault().equals(SPAIN_LOCALE)) {
            assertThat(getIntFromStringDecimal("123,45"), is(123));
        }

        try {
            getIntFromStringDecimal("");
            fail();
        } catch (ParseException pe) {
            assertThat(pe, instanceOf(ParseException.class));
        }
    }

    @Test
    public void testNameFile() throws Exception
    {
        assertThat(app_preferences_file.toString(),
                is("com.didekindroid.security.IdentityCacher.SharedPrefFiles.app_preferences_file"));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void testCalendarTime()
    {
        Date date1 = new Date();
        Calendar calendar1 = new GregorianCalendar();
        calendar1.add(Calendar.MINUTE, 1);
        assertThat(Long.compare(date1.getTime(), calendar1.getTimeInMillis()) < 0, is(true));
    }

    @SuppressWarnings("deprecation")
    private Locale getLocale()
    {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }
}