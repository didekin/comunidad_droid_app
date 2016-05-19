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

import static com.didekindroid.common.utils.UIutils.SPAIN_LOCALE;
import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.common.utils.UIutils.formatTimeToString;
import static com.didekindroid.common.utils.UIutils.getIntFromStringDecimal;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
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
    public void testFormatTimeToString_1()
    {
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            assertThat(formatTimeToString(1455301148000L), is("12/2/2016"));
        }
        if (Locale.getDefault().equals(Locale.ENGLISH)) {
            assertThat(formatTimeToString(1455301148000L), is("Feb 12, 2016"));
        }
    }

    @Test
    public void testFormatTimeStampToString_1()
    {
        Timestamp timestamp = new Timestamp(1455301148000L);

        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            assertThat(formatTimeStampToString(timestamp), is("12/2/2016"));
        }
        if (Locale.getDefault().equals(Locale.ENGLISH)) {
            assertThat(formatTimeStampToString(timestamp), is("Feb 12, 2016"));
        }
    }

    @Test
    public void testFormatTimeStampToString_2()
    {
        // Verifico el método de validación de la fecha prevista por defecto para una resolución.
        long timestampLong = new Timestamp(1455301148000L).getTime();
        String fechaViewStr = context.getString(R.string.incid_resolucion_fecha_default_txt);
        assertThat(fechaViewStr.equals(formatTimeToString(timestampLong)),is(false));
        assertThat(fechaViewStr.equals(formatTimeToString(0L)),is(false));
        assertThat(fechaViewStr.equals(formatTimeToString(0)),is(false));
    }

    @Test
    public void testFormatTimeStampToString_3()
    {
        // Tests genéricos del API Java.

        Timestamp timestamp = new Timestamp(1455301148000L);

        String formatTime = DateFormat.getDateInstance(DateFormat.LONG, SPAIN_LOCALE).format(timestamp);
        assertThat(formatTime, is("12 de febrero de 2016"));

        formatTime = DateFormat.getDateInstance(DateFormat.MEDIUM, SPAIN_LOCALE).format(timestamp);
        assertThat(formatTime, is("12/2/2016"));

        formatTime = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH).format(timestamp);
        assertThat(formatTime, is("February 12, 2016"));

        formatTime = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(timestamp);
        assertThat(formatTime, is("Feb 12, 2016"));
    }

    @Test
    public void testFormatDoubleToZeroDecimal()
    {
        if (Locale.getDefault().equals(Locale.ENGLISH)) {
            assertThat(UIutils.formatDoubleZeroDecimal(12.34, context), is("12"));
        }
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
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
        if (Locale.getDefault().equals(Locale.ENGLISH)) {
            assertThat(getIntFromStringDecimal("123.45"), is(123));
        }
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
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
    public void testDefaultsEmulator(){
        TimeZone timeZone = new GregorianCalendar().getTimeZone();
        assertThat(timeZone.getID(), is("Europe/Brussels"));
        assertThat(Locale.getDefault(), is(SPAIN_LOCALE));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void testCalendarTime(){
        Date date1 = new Date();
        Calendar calendar1 = new GregorianCalendar();
        calendar1.add(Calendar.MINUTE,1);
        assertThat(Long.compare(date1.getTime(),calendar1.getTimeInMillis()) < 0, is(true));
    }
}