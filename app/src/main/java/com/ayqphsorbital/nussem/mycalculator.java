package com.ayqphsorbital.nussem;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

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
    public static String strSeparator = ",";

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

    public ArrayList<String> getprereq(String prereq)
    {
       ArrayList<String> preqeulist = new ArrayList();
        String temp = "";
        int i = 0;

        while(i < prereq.length())
        {
            switch(prereq.charAt(i)) {

                 case ' ': {
                    preqeulist.add(temp);
                    temp = "";
                    i++;
                    break;

                }

                case'/': {
                    preqeulist.add(temp);
                    temp = "";
                    i++;
                    break;

                }
                case 'o': {
                    if ((i + 1 < prereq.length()) && ((prereq.charAt(i) == 'r') || (prereq.charAt(i) == 'R'))) {
                        preqeulist.add(temp);
                        temp = "";
                        i++;
                        break;

                    }
                }
                case 'O': {
                    if ((i + 1 < prereq.length()) && ((prereq.charAt(i) == 'r') || (prereq.charAt(i) == 'R'))) {
                        preqeulist.add(temp);
                        temp = "";
                        i++;
                        break;

                    }
                }
                default:{
                    temp+=prereq.charAt(i);
                    i++;
                }
            }

        }

        preqeulist.add(temp);

        if(preqeulist.size() > 10 )
        {
            //This might seem weird, but the reason for this is because there are some modules
            //whose prerequisite string is very long, and that is because in their prerequisite string, they
            //write GCE O levels and A level requirements. On top of these requirements, they also included
            //specific modules should you not clear these requirements. So if we simply send the string as it is
            // many students would not clear the module requirement even though they have cleared the O level and A level
            //requirement. Thus for simplicity sake, we take all these mods which can be cleared by
            //O or A level requirements, to have no prerequisite.
            ArrayList<String> emptylist = new ArrayList();
            return emptylist;
        }

        return preqeulist;
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


    public String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public String[] convertStringToArray(String str){
        if(str == null)
        {
            String[] temp = {};
            return temp;
        }
        String[] arr = str.split(strSeparator);
        return arr;
    }
}
