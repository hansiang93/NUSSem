package com.ayqphsorbital.nussem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HanSiang on 07/07/2015.
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 6;

    // Database Name
    private static final String DATABASE_NAME = "ModuleList";

    // Contacts table name
    private static final String TABLE_MODULES = "ModuleInfo";
    private static final String SEM_ONE = "Sem_One";

    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_CODE = "ModuleCode";
    private static final String KEY_TITLE = "ModuleTitle";
    private static final String KEY_CREDIT = "ModuleCredit";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_MODULES_TABLE = "CREATE TABLE " + TABLE_MODULES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CODE + " TEXT,"
                + KEY_TITLE + " TEXT" + ")";
        db.execSQL(CREATE_MODULES_TABLE);

        String CREATE_SEM1_TABLE = "CREATE TABLE " + SEM_ONE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CODE + " TEXT,"
                + KEY_TITLE + " TEXT," + KEY_CREDIT + " INTEGER" + ")";
        db.execSQL(CREATE_SEM1_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODULES);
        db.execSQL("DROP TABLE IF EXISTS " + SEM_ONE);

        // Create tables again
        onCreate(db);
    }

    //adding a module to semester 1
    public void addModtoSem (ModuleInfo Mod) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, Mod.getModuleCode()); // Module Code
        values.put(KEY_TITLE, Mod.getModuleTitle()); // Module Title
        //values.put(KEY_CREDIT, Mod.getModuleCredit());

        // Inserting Row
        db.insert(TABLE_MODULES, null, values);
        db.close(); // Closing database connection
    }

    //deleting a module from semester 1
    public void deleteModfromsem (ModuleInfo Mod) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SEM_ONE, KEY_ID + " = ?",
                new String[]{String.valueOf(Mod.getID())});
        db.close();

    }

    // Getting All Contacts
    public Cursor getAllModsFromSem() {

        ModuleInfo test1= new ModuleInfo("cs1020", "testing", 4);

        addModtoSem(test1);

        String selectQuery = "SELECT " +  "*" + " FROM " + TABLE_MODULES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }


    // Adding new contact
    public void addMod(ModuleInfo Mod) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CODE, Mod.getModuleCode()); // Module Code
        values.put(KEY_TITLE, Mod.getModuleTitle()); // Module Title

        // Inserting Row
        db.insert(TABLE_MODULES, null, values);
        db.close(); // Closing database connection
    }



    // Getting single contact
    public ModuleInfo getMod(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MODULES, new String[] { KEY_ID,
                        KEY_CODE, KEY_TITLE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ModuleInfo Mod = new ModuleInfo(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return Mod
        return Mod;
    }

    // Getting All Contacts
    public List<ModuleInfo> getAllMods() {

        List<ModuleInfo> moduleList = new ArrayList<ModuleInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MODULES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModuleInfo Mod = new ModuleInfo();
                Mod.setID(Integer.parseInt(cursor.getString(0)));
                Mod.setModuleCode(cursor.getString(1));
                Mod.setModuleTitle(cursor.getString(2));
                // Adding Mod to list
                moduleList.add(Mod);
            } while (cursor.moveToNext());
        }

        // return contact list
        return moduleList;



    }

    // Getting contacts Count
    public int getModsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_MODULES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();

    }
    // Updating single contact
    public int updateMod(ModuleInfo Mod) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CODE, Mod.getModuleCode());
        values.put(KEY_TITLE, Mod.getModuleTitle());

        // updating row
        return db.update(TABLE_MODULES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(Mod.getID()) });

    }

    // Deleting single contact
    public void deleteMod (ModuleInfo Mod) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MODULES, KEY_ID + " = ?",
                new String[] { String.valueOf(Mod.getID()) });
        db.close();

    }



}
