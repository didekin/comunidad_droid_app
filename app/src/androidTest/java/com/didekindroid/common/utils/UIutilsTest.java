package com.didekindroid.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.common.testutils.ActivityTestUtils;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.M;
import static com.didekindroid.common.utils.UIutils.SPAIN_LOCALE;
import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.common.utils.UIutils.formatTimeToString;
import static com.didekindroid.common.utils.UIutils.getIntFromStringDecimal;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static java.text.DateFormat.*;
import static java.text.DateFormat.MEDIUM;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.getDefault;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 31/03/15
 * Time: 15:16
 */
@RunWith(AndroidJUnit4.class)
public class UIutilsTest extends TestCase {

    Context context;

    @Before
    public void setUp()
    {
        context = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void clean()
    {
        ActivityTestUtils.cleanWithTkhandler();
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
        if (getDefault().equals(ENGLISH)) {
            assertThat(UIutils.formatDoubleZeroDecimal(12.34, context), is("12"));
        }
        if (getDefault().equals(SPAIN_LOCALE)) {
            assertThat(UIutils.formatDoubleZeroDecimal(12.34, context), is("12"));
        }
    }

    @Test
    public void testFormatDoubleToTwoDecimal()
    {
        assertThat(UIutils.formatDoubleTwoDecimals(12.34, context),
                is("12".concat(context.getString(R.string.decimal_separator)).concat("34")));
        assertThat(UIutils.formatDoubleTwoDecimals(12.3422, context),
                is("12".concat(context.getString(R.string.decimal_separator)).concat("34")));
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
        assertThat(UIutils.SharedPrefFiles.app_preferences_file.toString(),
                is("com.didekindroid.common.utils.UIutils.SharedPrefFiles.app_preferences_file"));
    }

    @Test
    public void testUpdateIsRegistered() throws Exception
    {
        updateIsRegistered(false, context);
        assertThat(isRegisteredUser(context), is(false));
        updateIsRegistered(true, context);
        assertThat(isRegisteredUser(context), is(true));
    }

    @Test
    public void testDefaultsEmulator()
    {
        TimeZone timeZone = new GregorianCalendar().getTimeZone();
        assertThat(timeZone.getID(), is("Europe/Brussels"));
        assertThat(getDefault(), is(SPAIN_LOCALE));
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
}