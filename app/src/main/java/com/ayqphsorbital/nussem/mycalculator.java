package com.ayqphsorbital.nussem;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by ang on 18/8/2015.
 */
public class mycalculator {

    String[] items = new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D+", "D", "F", "S", "U", "NIL"};
    private static final String KEY_ID = "_id";
    private static final String KEY_CODE = "ModuleCode";
    private static final String KEY_TITLE = "ModuleTitle";
    private static final String KEY_CREDIT = "ModuleCredit";
    private static final String KEY_SEMESTER = "SEMESTER";
    private static final String KEY_GRADE = "GRADE";

    public mycalculator(){}

    public int totalgradedmc(int semnum, Context context)
    {
        DatabaseHandler db = new DatabaseHandler(context);
        Cursor c = db.getAllModsFromSem(semnum);
        int totalgradedmc = 0;
        double gpa = 0;
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int position = c.getInt(c.getColumnIndex(KEY_GRADE));
            double gradeofmod =  convertpositiontograde(position);
            if(gradeofmod >= 0 )
            {
                int creditofmod = c.getInt(c.getColumnIndex(KEY_CREDIT));
                totalgradedmc += creditofmod;
            }

            c.moveToNext();
        }
        return totalgradedmc;
    }

    public double gapforsem(int semnum, Context context)
    {
        DatabaseHandler db = new DatabaseHandler(context);
        Cursor c = db.getAllModsFromSem(semnum);
        int totalgradedmc = 0;
        double gpa = 0;
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            int position = c.getInt(c.getColumnIndex(KEY_GRADE));
            double gradeofmod =  convertpositiontograde(position);
            if(gradeofmod >= 0 )
            {
                int creditofmod = c.getInt(c.getColumnIndex(KEY_CREDIT));
                gpa += gradeofmod*creditofmod;
                totalgradedmc += creditofmod;
            }

            c.moveToNext();
        }
        return gpa/totalgradedmc;

    }

    public int totalcreditinsem(int semnum, Context context)
    {
        DatabaseHandler db = new DatabaseHandler(context);
        db.getReadableDatabase();
        Cursor c = db.getAllModsFromSem(semnum);
        int total = 0;
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            total = total + c.getInt(c.getColumnIndex(KEY_CREDIT));
            c.moveToNext();
        }
        return total;

    }

    public double convertpositiontograde(int position)
    {
        switch(position){
            case 0: {
                return 5.0;
            }
            case 1: {
                return 5.0;
            }
            case 2: {
                return 4.5;
            }
            case 3: {
                return 4.0;
            }
            case 4: {
                return 3.5;
            }
            case 5: {
                return 3.0;
            }
            case 6: {
                return 2.5;
            }
            case 7: {
                return 2.0;
            }
            case 8: {
                return 1.5;
            }
            case 9: {
                return 1.0;
            }
            case 10: {
                return 0;
            }
            case 11: {
                return -1;
            }
            case 12: {
                return -2;
            }
            case 13: {
                return -3;
            }

        }

        return -4;

    }
}
