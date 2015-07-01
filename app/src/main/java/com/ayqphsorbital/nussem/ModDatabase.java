package com.ayqphsorbital.nussem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by HanSiang on 01/07/2015.
 */
public class ModDatabase extends SQLiteOpenHelper {

    public ModDatabase(Context context)
    {
        super(context, "ModuleDatabase.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableEmp="create table emp(id text,name text,salary text)";
        db.execSQL(tableEmp);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void insertData(String id,String name, String salary)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("id",id);
        values.put("name",name);
        values.put("salary",salary);
        sqLiteDatabase.insert("emp",null,values);
    }
    public ArrayList fetchData()
    {
        ArrayList<String>stringArrayList=new ArrayList<String>();
        String fetchdata="select * from emp";
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(fetchdata, null);
        if(cursor.moveToFirst()){
            do
            {
                stringArrayList.add(cursor.getString(0));
                stringArrayList.add(cursor.getString(1));
                stringArrayList.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return stringArrayList;
    }
}