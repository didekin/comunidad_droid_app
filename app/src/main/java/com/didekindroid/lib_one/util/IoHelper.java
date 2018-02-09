package com.didekindroid.lib_one.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 19/06/15
 * Time: 12:30
 */
public final class IoHelper {

    private IoHelper()
    {
    }

    public static List<String> doArrayFromFile(Context context, int rawResourceId)
    {
        Timber.i("In doArrayFromFile()");

        final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(rawResourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> tipos = new ArrayList<>();

        try {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.length() < 2) {
                    continue;
                }
                tipos.add(lineToLowerCase(line));
            }

        } catch (IOException e) {
            Timber.e(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Timber.e(e.getMessage());
            }
        }

        Timber.i("Done doArrayFromFile()");

        return tipos;
    }

    public static String readStringFromFile(File file)
    {
        Timber.d("readStringFromFile()");

        RandomAccessFile readableRefreshTkFile;
        byte[] bytesRefreshToken;
        try {
            readableRefreshTkFile = new RandomAccessFile(file, "r");
            bytesRefreshToken = new byte[(int) readableRefreshTkFile.length()];
            readableRefreshTkFile.readFully(bytesRefreshToken);
            readableRefreshTkFile.close();
        } catch (IOException e) {
            Timber.e(e.getLocalizedMessage(), e.getMessage());
            throw new RuntimeException(e);
        }
        return new String(bytesRefreshToken).trim();
    }

    public static void writeFileFromString(String stringToWrite, File fileToWrite)
    {
        Timber.d("writeFileFromString()");
        FileOutputStream stringFileStream;
        try {
            stringFileStream = new FileOutputStream(fileToWrite);
            stringFileStream.write(stringToWrite.getBytes());
            stringFileStream.close();
        } catch (IOException e) {
            Timber.e(e.getLocalizedMessage(), e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static String lineToLowerCase(String line)
    {
        String lineTrim = line.trim();
        return lineTrim.substring(0,1) + lineTrim.substring(1).toLowerCase(Locale.getDefault());
    }
}
