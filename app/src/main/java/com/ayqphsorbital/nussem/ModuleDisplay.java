package com.ayqphsorbital.nussem;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ang on 13/8/2015.
 */
public class ModuleDisplay{

    private LayoutInflater cursorInflater;

    private static final String KEY_CODE = "ModuleCode";
    private static final String KEY_TITLE = "ModuleTitle";
    private static final String KEY_CREDIT = "ModuleCredit";
    private static final String KEY_PREREQUISITES = "PREREQ";
    Button removemod;
    int semnum;




    // Default constructor
    public ModuleDisplay(final Context context, final LinearLayout ParentView, Cursor cursor, int sem) {
        semnum = sem;

        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        cursorInflater.inflate(R.layout.adapter, null);
        cursor.moveToFirst();

        mycalculator calc = new mycalculator();
        DatabaseHandler db = new DatabaseHandler(context);

        while(!cursor.isAfterLast())
        {
            //Initializing the Childview before adding it into the LinearParent View
            //The ChildView displays all the module information
            View ChildView =  cursorInflater.inflate(R.layout.adapter, null);

            TextView modulecode = (TextView) ChildView.findViewById(R.id.adapter_ModuleCode);
            TextView modulename = (TextView) ChildView.findViewById(R.id.adapter_ModuleName);
            TextView modulecredit = (TextView) ChildView.findViewById(R.id.adapter_ModuleCredit);

            final String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            final String code = cursor.getString(cursor.getColumnIndex(KEY_CODE));
            String credit = cursor.getInt(cursor.getColumnIndex(KEY_CREDIT)) + " MC";
            String prereq = cursor.getString(cursor.getColumnIndex(KEY_PREREQUISITES));

            String[] prereqlist = calc.convertStringToArray(prereq);
            if(!db.satisfyprereq(prereqlist, semnum))
            {
                ChildView.setBackgroundColor(context.getResources().getColor(R.color.red));
            }

            modulecode.setText(code);
            modulecredit.setText(credit);
            modulename.setText(title);



            removemod = (Button) ChildView.findViewById(R.id.adapter_crossbutton);
            removemod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        DatabaseHandler db = new DatabaseHandler(context);
                        db.getWritableDatabase();
                        ModuleInfo mod = new ModuleInfo(code, title);
                        db.deleteModfromsem(mod, semnum);
                        Cursor mCursor = db.getAllModsFromSem(semnum);
                        ParentView.removeAllViews();
                        ModuleDisplay renew = new ModuleDisplay(context, ParentView, mCursor, semnum);


                        CharSequence text = "Module Removed";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }


                }
            });

            ChildView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {


                    Intent data = new Intent(context, SearchResultsActivity.class);
                    data.putExtra("SPECIAL",true);
                    data.putExtra("QUERY", code);
                    CharSequence text = "module info";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    context.startActivity(data);

                    return true;
                }

            });


            ParentView.addView(ChildView); //Adding the Childview to the linear parent view
            cursor.moveToNext();
        }

    }





}

