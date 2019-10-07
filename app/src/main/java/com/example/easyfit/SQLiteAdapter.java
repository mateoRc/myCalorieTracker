package com.example.easyfit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class SQLiteAdapter {

    //Ime i verzija BP
    private static final String databaseName = "mojplan";
    private static final int databaseVersion = 54;

    //Varijable baze podataka
    private final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    //konstruktor
    public SQLiteAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    //DatabaseHelper klasa
    private  static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, databaseName, null, databaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                //Kreiranje tablica
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS diary (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " diary_cal_date DATE," +
                        " diary_cal INT);");

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS eaten_cal (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " eaten_cal_id INT," +
                        " eaten_cal_date DATE," +
                        " eaten_cal INT);");

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS food_entry (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " fe_name VARCHAR," +
                        " fe_date DATE," +
                        " fe_food_id INTEGER," +
                        " fe_calories DOUBLE);");

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS food (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " food_id INT, " +
                        " food_name VARCHAR," +
                        " food_description VARCHAR," +
                        " food_calories DOUBLE);");

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " user_name VARCHAR," +
                        " user_height INT," +
                        " user_weight INT," +
                        " user_age INT," +
                        " user_gender INT," +
                        " user_activity INT," +
                        " user_goal INT, " +
                        " user_BMR DATE, " +
                        " user_BMI DOUBLE, " +
                        " user_date DATE);");

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS food");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS food_entry");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS eaten_cal");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS diary");
            onCreate(sqLiteDatabase);
        }
    }

    //otvaranje baze podataka
    public SQLiteAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return  this;
    }

    //zatvaranje baze podataka
    public void close() {
        dbHelper.close();
    }

    //unos podataka u tablicu
    public void add(String table, String fields, String values) {
        db.execSQL("INSERT INTO " + table + "(" + fields + ") VALUES (" + values + ")");
    }

    //broj unosa
    public int countAllRecordsNotes(String table) {
        Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + table + "", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    //metoda za provjeru unosa (osiguravamo da podaci koje unosimo nisu opasni za bazu)
    public String checkEntry(String value) {
        boolean isNumeric = false;
        try{
            Double myDouble = Double.parseDouble(value);
            isNumeric = true;
        }
        catch (NumberFormatException e) {
            System.out.println("Could not parse " + e);
        }
        if (!isNumeric) {
            //escapes special chars in a string for use in an SQL statement
            if (value != null && value.length() > 0){
                value = value.replace("\\", "\\\\");
                value = value.replace("'", "\\'");
                value = value.replace("\0", "\\0");
                value = value.replace("\n", "\\n");
                value = value.replace("\r", "\\r");
                value = value.replace("\"", "\\\"");
                value = value.replace("\\x1a", "\\Z");
            }
        }
        value = "'" + value + "'";
        return value;
    }

    public int checkEntry(int value) {
        return value;
    }

    public double checkEntry(double value) {
        return value;
    }

    //odabir elemenata iz određene tablice
    //u obliku:
    /*
    Cursor cursor;
    String fields[] = new String[] {
               "category_id",
               ...
               ...
    };
     */

    public Cursor select(String table, String[] fields) throws SQLException
    {
        Cursor cursor = db.query(table, fields, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor selectWhere(String table, String[] fields, String a, String b) throws SQLException {
        Cursor cursor = db.query(table, fields, a + "=" + b, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //brisanje elemenata  tablice
    public int delete(String table, String pKey, long rowID) throws SQLException {
        return db.delete(table, pKey + "=" + rowID, null);
    }

    //brisanje svih elemenata iz tablice
    public void deleteAll(String table) {
         db.delete(table, null, null);
    }

    //ažuriranje elemenata u talbici
    public boolean updateFields(String table, String pk, long rowID, String fields[], String values[]) throws SQLException {

        ContentValues args = new ContentValues();
        int arraySize = fields.length;
        for (int x = 0; x < arraySize; x++) {

            //values[x] = values[x].substring(1, values[x].length()-1);   //briše '' nakon što se pokrene checkEntry()

            args.put(fields[x], values[x]);
        }

        return db.update(table, args, pk + "=" + rowID, null) > 0;
    }
}
