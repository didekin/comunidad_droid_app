package com.didekindroid.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.didekindroid.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * User: pedro@didekin
 * Date: 19/06/15
 * Time: 12:30
 */
public class IoHelper {

    private static final String TAG = IoHelper.class.getCanonicalName();
    public static final int TIPO_VIA_FILE_SIZE = 322;

    private IoHelper()
    {
    }

    public static List<String> doArrayFromFile(Context context)
    {

        Log.i(TAG, "In doArrayFromFile()");

        final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.tipos_vias);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> tipos = new ArrayList<>(TIPO_VIA_FILE_SIZE);

        try {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.length() < 2) {
                    continue;
                }
                line = line.trim();

                tipos.add(line.substring(0, 1) + line.substring(1).toLowerCase());
            }

        } catch (IOException e) {
            UIutils.doRuntimeException(e, TAG);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                UIutils.doRuntimeException(e, TAG);
            }
        }

        Log.i(TAG, "Done doArrayFromFile()");

        return tipos;
    }

    public static String readStringFromFile(File file)
    {
        Log.d(TAG, "readStringFromFile()");

        RandomAccessFile readableRefreshTkFile;
        byte[] bytesRefreshToken = null;
        try {
            readableRefreshTkFile = new RandomAccessFile(file, "r");
            bytesRefreshToken = new byte[(int) readableRefreshTkFile.length()];
            readableRefreshTkFile.readFully(bytesRefreshToken);
            readableRefreshTkFile.close();
        } catch (IOException e) {
            UIutils.doRuntimeException(e, e.getLocalizedMessage());
        }
        return new String(bytesRefreshToken);
    }

    public static void writeFileFromString(String stringToWrite, File fileToWrite)
    {
        Log.d(TAG, "writeFileFromString()");
        FileOutputStream stringFileStream;
        try {
            stringFileStream = new FileOutputStream(fileToWrite);
            stringFileStream.write(stringToWrite.getBytes());
            stringFileStream.close();
        } catch (IOException e) {
            UIutils.doRuntimeException(e, e.getLocalizedMessage());
        }
    }


}
