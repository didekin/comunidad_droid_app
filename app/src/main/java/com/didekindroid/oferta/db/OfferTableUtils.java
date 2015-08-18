package com.didekindroid.oferta.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.didekindroid.oferta.Ipsum;

/**
 * User: pedro
 * Date: 11/02/15
 * Time: 19:15
 */
public class OfferTableUtils {

    public static final String TAG = "OfferTableUtils";

    public static void insertOffer(DataBaseHelper dbHelper)
    {
        Log.d(TAG, "insertOffer()");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(OfferTable.INSTANCE.TITLE, Ipsum.testOffers[0].getTitle());
        values.put(OfferTable.INSTANCE.DESCRIPTION, Ipsum.testOffers[0].getDescription());

        long newId = db.insert(OfferTable.INSTANCE.TB_NAME, null, values);

        values.put(OfferTable.INSTANCE.TITLE, Ipsum.testOffers[1].getTitle());
        values.put(OfferTable.INSTANCE.DESCRIPTION, Ipsum.testOffers[1].getDescription());

        newId = db.insert(OfferTable.INSTANCE.TB_NAME, null, values);
    }

    public static Cursor queryOffers(DataBaseHelper dbHelper)
    {
        Log.d(TAG, "queryOffers()");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(
                OfferTable.INSTANCE.TB_NAME
                , OfferTable.INSTANCE.setColumnsAll
                , null
                , null
                , null
                , null
                , OfferTable.INSTANCE.sortColumnsAll
        );
        return cursor;
    }
}
