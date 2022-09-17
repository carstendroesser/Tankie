package com.carstendroesser.tanky.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;

import com.carstendroesser.tanky.models.Price;

import java.util.List;

/**
 * Created by carstendrosser on 29.06.16.
 */
public class Database extends SQLiteOpenHelper {

    // CONSTANTS

    private static final String PRICES_TABLENAME = "prices";
    private static final String PRICE_ID = "price_id";
    private static final String PRICE_TYPE = "price_type";
    private static final String PRICE_VALUE = "price_value";
    private static final String PRICE_TIME = "price_time";

    private static final String PRICES_CREATE_TABLE
            = "CREATE TABLE "
            + PRICES_TABLENAME
            + " ("
            + PRICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PRICE_TYPE + " VARCHAR(100), "
            + PRICE_VALUE + " REAL,"
            + PRICE_TIME + " INTEGER);";

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    // MEMBERS

    private Context mContext;
    private static Database mInstance;
    private SQLiteDatabase mDatabase;

    // CONSTRUCTOR

    /**
     * It is a singleton and it shall not be possible to have
     * multiple instances.
     *
     * @param pContext we need that
     */
    private Database(Context pContext) {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = pContext;
    }

    // SQLiteOpenHelper

    @Override
    public void onCreate(SQLiteDatabase pDatabase) {
        pDatabase.execSQL(PRICES_CREATE_TABLE);
        mDatabase = pDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDatabase, int pOldVersion, int pNewVersion) {
        // empty
    }

    // PUBLIC-API

    /**
     * Method to get the one and only instance.
     *
     * @param pContext we need that
     * @return the singletong of this class
     */
    public static Database getInstance(Context pContext) {
        if (mInstance == null) {
            mInstance = new Database(pContext);
        }
        return mInstance;
    }

    /**
     * Insert prices into the database the database can calculate the
     * best prices with then.
     *
     * @param pPrices   a list of prices to insert into the database
     * @param pCallback callback to get notified as soon as the insertion has finished
     */
    public void insertPrices(final List<Price> pPrices, final DatabaseCallback pCallback) {
        // asynchron insertion. no problem because of SQLiteOpenHelper is threadsafe
        new Thread(new Runnable() {
            @Override
            public void run() {
                // make sure we have an open database
                if (mDatabase == null) {
                    mDatabase = getWritableDatabase();
                }

                // ADD ALL THE PRICES!
                for (Price price : pPrices) {
                    ContentValues values = new ContentValues();
                    values.put(PRICE_TYPE, price.getPriceType());
                    values.put(PRICE_VALUE, price.getPriceValue());
                    values.put(PRICE_TIME, price.getPriceTime());

                    mDatabase.insert(PRICES_TABLENAME, null, values);
                }

                // call the callback in the main-ui-thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        pCallback.onPricesInserted();
                    }
                });
            }
        }).start();
    }

    /**
     * Gets the best average price of all time-periods of one hour.
     *
     * @param pType     the fuel-type
     * @param pCallback gets notified when the calculation is finished
     */
    public void getBestPriceFor(final String pType, final DatabaseCallback pCallback) {
        // do it asynchron
        new Thread(new Runnable() {

            @Override
            public void run() {
                String query = "SELECT price_type, MIN(price_value), price_time " +
                        "FROM (SELECT price_type, AVG(price_value) AS price_value, price_time " +
                        "FROM prices WHERE price_type = '" + pType +
                        "' GROUP BY price_time)";

                // make a query
                final Cursor cursor = getReadableDatabase().rawQuery(query, null);
                Price price = null;

                // get the result
                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    price = new Price(cursor.getString(0), cursor.getDouble(1), cursor.getInt(2));
                }

                final Price priceToReturn = price;

                // call the callbacks in the main-ui-thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        pCallback.onBestPriceCalculated(priceToReturn);
                    }
                });
            }
        }).start();
    }

    /**
     * Callback used to get notified about database-actions.
     */
    public interface DatabaseCallback {
        /**
         * Called as soon as new prices were inserted.
         */
        void onPricesInserted();

        /**
         * Called as soon as prices were calculated.
         *
         * @param pPrice the best average price/time
         */
        void onBestPriceCalculated(Price pPrice);
    }

}
