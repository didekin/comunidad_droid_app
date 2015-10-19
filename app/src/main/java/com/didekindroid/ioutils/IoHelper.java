package com.didekindroid.ioutils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.didekindroid.R;
import com.didekindroid.uiutils.UIutils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
        int pkCounter = 0;

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

        RandomAccessFile readableRefreshTkFile = null;
        byte[] bytesRefreshToken = null;
        try {
            readableRefreshTkFile = new RandomAccessFile(file, "r");
            bytesRefreshToken = new byte[(int) readableRefreshTkFile.length()];
            readableRefreshTkFile.readFully(bytesRefreshToken);
            readableRefreshTkFile.close();
        } catch (FileNotFoundException e) {
            UIutils.doRuntimeException(e, e.getLocalizedMessage());
        } catch (IOException e) {
            UIutils.doRuntimeException(e, e.getLocalizedMessage());
        }
        return new String(bytesRefreshToken);
    }

    public static void writeFileFromString(String stringToWrite, File fileToWrite)
    {
        Log.d(TAG, "writeFileFromString()");
        FileOutputStream stringFileStream = null;
        try {
            stringFileStream = new FileOutputStream(fileToWrite);
            stringFileStream.write(stringToWrite.getBytes());
            stringFileStream.close();
        } catch (FileNotFoundException e) {
            UIutils.doRuntimeException(e, e.getLocalizedMessage());
        } catch (IOException e) {
            UIutils.doRuntimeException(e, e.getLocalizedMessage());
        }
    }


    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     *
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    public static InputStream downloadUrl(String urlString) throws IOException
    {
        Log.d(TAG, "donwloadUrl()");

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream InputStream containing HTML from targeted site.
     * @param len    Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException
    {
        Log.d(TAG, "readIt()");

        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
