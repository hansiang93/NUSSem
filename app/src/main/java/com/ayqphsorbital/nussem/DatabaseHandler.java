package com.ayqphsorbital.nussem;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.design.widget.TabLayout;
import android.widget.SimpleCursorAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HanSiang on 07/07/2015.
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 18;

    // Database Name
    private static final String DATABASE_NAME = "ModuleList";

    // Contacts table name
    private static final String TABLE_MODULES = "ModuleInfo";
    private static final String SEM_ONE = "Sem_One";
    private static final String OVERVIEW = "Overview";

    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_CODE = "ModuleCode";
    private static final String KEY_TITLE = "ModuleTitle";
    private static final String KEY_CREDIT = "ModuleCredit";
    private static final String KEY_SEMESTER = "SEMESTER";
    private static final String KEY_GRADE = "GRADE";
    private static final String KEY_PREREQUISITES = "GRADE";


    private HashMap<String, String> mAliasMap;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // This HashMap is used to map table fields to Custom Suggestion fields
        mAliasMap = new HashMap<String, String>();

        // Unique id for the each Suggestions ( Mandatory )
        mAliasMap.put("_ID", KEY_ID + " as " + "_id" );

        // Text for Suggestions ( Mandatory )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, KEY_CODE + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);

// Icon for Suggestions ( Optional )
        mAliasMap.put( SearchManager.SUGGEST_COLUMN_TEXT_2, KEY_TITLE + " as " + SearchManager.SUGGEST_COLUMN_TEXT_2);

        // This value will be appended to the Intent data on selecting an item from Search result or Suggestions ( Optional )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, KEY_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
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
                + KEY_TITLE + " TEXT," + KEY_CREDIT + " INTEGER," + KEY_GRADE + " INTEGER,"
                + KEY_PREREQUISITES + "TEXT" + ")";
        db.execSQL(CREATE_SEM1_TABLE);

        String CREATE_OVERVIEW_TABLE = "CREATE TABLE " + OVERVIEW + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SEMESTER + " TEXT"
                + ")";
        db.execSQL(CREATE_OVERVIEW_TABLE);

        //adding semester one to the overview table, otherwise there will be a bug
        //when you try to add modules but there is no overview table to display it
        ContentValues values = new ContentValues();
        String countQuery = "SELECT  * FROM " + OVERVIEW;
        Cursor cursor = db.rawQuery(countQuery, null);
        int numberOfSem = cursor.getCount();
        numberOfSem++;
        String semesterNumber = "" + numberOfSem;
        values.put(KEY_SEMESTER, semesterNumber);
        db.insert(OVERVIEW, null, values);

    }

    private void createtable(int semnumber)
    {
        SQLiteDatabase db = getWritableDatabase();
        String tablename = "SEM_"+semnumber;
        db.execSQL("DROP TABLE IF EXISTS " + tablename);
        String CREATE_SEM1_TABLE = "CREATE TABLE " + tablename + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CODE + " TEXT,"
                + KEY_TITLE + " TEXT," + KEY_CREDIT + " INTEGER," + KEY_GRADE + " INTEGER"
                + KEY_PREREQUISITES + "TEXT" +")";
        db.execSQL(CREATE_SEM1_TABLE);
        return;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODULES);
        db.execSQL("DROP TABLE IF EXISTS " + SEM_ONE);
        db.execSQL("DROP TABLE IF EXISTS " + OVERVIEW);

        // Create tables again
        onCreate(db);
    }

    //adding a module to semester 1
    public void addModtoSem (ModuleInfo Mod, int semnum) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, Mod.getModuleCode()); // Module Code
        values.put(KEY_TITLE, Mod.getModuleTitle()); // Module Title
        values.put(KEY_CREDIT, Mod.getModuleCredit());
        values.put(KEY_GRADE, 13);


        // Inserting Row
        if(semnum == 1) {
            db.insert(SEM_ONE, null, values);
            db.close(); // Closing database connection
        }
        else
        {
            String tablename = "SEM_"+semnum;
            db.insert(tablename, null, values);
            db.close();

        }
    }

    //deleting a module from semester 1
    public void deleteModfromsem (ModuleInfo Mod, int semnum) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(semnum ==1) {

            String whereclause = KEY_CODE + "= '" + Mod.getModuleCode() + "'";
            String[] arguments = {};
            db.delete(SEM_ONE, whereclause, arguments);
            db.close();
        }
        else
        {
            String tablename = "SEM_"+semnum;
            String whereclause = KEY_CODE + "= '" + Mod.getModuleCode() + "'";
            String[] arguments = {};
            db.delete(tablename, whereclause, arguments);
            db.close();

        }

    }

    // Getting All Contacts
    public Cursor getAllModsFromSem(int semnum) {

        String tablename = "SEM_"+semnum;

        if(semnum == 1) {

            String selectQuery = "SELECT " + "*" + " FROM " + SEM_ONE;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            return cursor;
        }
        else
        {
            String selectQuery = "SELECT " + "*" + " FROM " + tablename;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            return cursor;

        }
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

        Cursor cursor = db.query(TABLE_MODULES, new String[]{KEY_ID,
                        KEY_CODE, KEY_TITLE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
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
                new String[]{String.valueOf(Mod.getID())});

    }

    // Deleting single contact
    public void deleteMod (ModuleInfo Mod) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MODULES, KEY_ID + " = ?",
                new String[]{String.valueOf(Mod.getID())});
        db.close();

    }


    public void addSemester () {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String countQuery = "SELECT  * FROM " + OVERVIEW;
        Cursor cursor = db.rawQuery(countQuery, null);
        int numberOfSem = cursor.getCount();
        numberOfSem++;
        createtable(numberOfSem);

        String semesterNumber = "" + numberOfSem;

        values.put(KEY_SEMESTER, semesterNumber);
        db.insert(OVERVIEW, null, values);
        db.close(); // Closing database connection
    }

    public void deletesem(int semnum)
    {
        SQLiteDatabase db = getWritableDatabase();
        if(semnum == 1)
        {
            return;
        }
        else {
            String tablename = "SEM_"+semnum;
            db.execSQL("DROP TABLE IF EXISTS " + tablename);
        }

    }

    public Cursor getSemesterCursor ()
    {

        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "SELECT  * FROM " + OVERVIEW;
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor;

    }

    public boolean existinallsem(ModuleInfo mod)
    {
        SQLiteDatabase db = getReadableDatabase();

        String countQuery = "SELECT  * FROM " + OVERVIEW;
        Cursor cursor = db.rawQuery(countQuery, null);
        int numberOfSem = cursor.getCount();

        String modulename = mod.getModuleTitle();

        for(int i = 1; i <= numberOfSem; i++ )
        {
            if(i==1) //This is done because sem_one table has a different name from all the other semesters
            {
                if(existinsem(mod,SEM_ONE))
                    return true;
            }
            else
            {
                String tablename = "SEM_"+i;
                if(existinsem(mod, tablename))
                    return true;
            }
        }
        return false;
    }

    public boolean existinsem(ModuleInfo mod, String tablename)
    {
        SQLiteDatabase db = getReadableDatabase();
        String modulename = mod.getModuleTitle();
        String command = "SELECT * FROM " + tablename + " WHERE " + KEY_TITLE + " = '" + modulename + "'";
        Cursor data = db.rawQuery(command, null);
        return data.moveToFirst();

    }

    public void editgrade(int semnum, String modulecode, int position)
    {
        SQLiteDatabase db = getReadableDatabase();
        String tablename;
        if(semnum == 1)
        {
            tablename = SEM_ONE;
        }
        else {
            tablename = "SEM_" + semnum;
        }
        ContentValues cv = new ContentValues();
        cv.put(KEY_GRADE, position);
        int i = db.update(tablename, cv, KEY_CODE + "= '" + modulecode + "'", null);
        return;

    }

    public int getgrade (int semnum, String modulecode){
        SQLiteDatabase db = getReadableDatabase();
        String tablename;
        if(semnum == 1)
        {
            tablename = SEM_ONE;
        }
        else {
            tablename = "SEM_" + semnum;
        }
        String command = "SELECT * FROM " + tablename + " WHERE " + KEY_CODE + " = '" + modulecode + "'";
        Cursor data = db.rawQuery(command, null);
        int columnpos = data.getColumnIndex(KEY_GRADE);
        boolean testing = data.moveToFirst();
        int gradepos = data.getInt(columnpos);
        return gradepos;


    }

<<<<<<< HEAD
    public boolean iftableexist(String tableName)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public ArrayList<String> actualprereq(ArrayList<String> testing)
    {


        String[] selectionArgs = new String[testing.size()];
        selectionArgs = testing.toArray(selectionArgs);
        SQLiteDatabase db = getReadableDatabase();
        String command =  KEY_CODE + " =?";
        String[] columns = {KEY_CODE};
        ArrayList<String> actual = new ArrayList();

        for(int i = 0; i < testing.size(); i++) {

            String[] variable = {selectionArgs[i]};
            Cursor data = db.query(TABLE_MODULES, columns, command, variable, null, null, null, null);
            if(data.moveToFirst())
            {
                actual.add(data.getString(data.getColumnIndex(KEY_CODE)));
            }
        }

        return actual;
    }

=======
        public Cursor getModules(String[] selectionArgs){
            String selection = KEY_CODE + " like ? ";
>>>>>>> origin/master

            if(selectionArgs!=null){
                selectionArgs[0] = "%"+selectionArgs[0]+"%";
            }
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setProjectionMap(mAliasMap);

            queryBuilder.setTables(TABLE_MODULES);

            Cursor c = queryBuilder.query(this.getReadableDatabase(),
                    new String[] { "_ID",
                            SearchManager.SUGGEST_COLUMN_TEXT_1 ,
                            SearchManager.SUGGEST_COLUMN_TEXT_2 ,
                            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID } ,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    KEY_CODE + " asc ","10"
            );
            return c;

        }

}
