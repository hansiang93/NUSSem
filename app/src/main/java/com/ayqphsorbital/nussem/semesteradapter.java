package com.ayqphsorbital.nussem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ang on 12/8/2015.
 */
public class semesteradapter extends CursorAdapter  {

    private LayoutInflater cursorInflater;
    ListView modulelist;
    CustomAdapter myListAdapter;
    private static final String KEY_SEMESTER = "SEMESTER";
    String semnum;
    private Fragment fragment = null;
    private Class fragmentClass;
    private FragmentManager fragmentManager;
    String semview = "semesterview";
    private static final String KEY_PREREQUISITES = "PREREQ";


    // Default constructor
    public semesteradapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public void bindView(final View view, final Context context, Cursor cursor) {

        TextView semesternum = (TextView) view.findViewById(R.id.semtemp);
        modulelist = (ListView) view.findViewById(R.id.listviewtemp);

        semnum = cursor.getString(cursor.getColumnIndex(KEY_SEMESTER));
        String semtext = "Semester " + semnum;
        semesternum.setText(semtext);
        semesternum.setTag(Integer.parseInt(semnum));

        DatabaseHandler db = new DatabaseHandler(context);
        db.getReadableDatabase();
        Cursor mCursor = db.getAllModsFromSem(Integer.parseInt(semnum)); //the integer.parseInt changes a string into an integer
        final LinearLayout parentlinear = (LinearLayout) view.findViewById(R.id.semadapter_linear);
        parentlinear.removeAllViews();
        ModuleDisplay lineardisplay = new ModuleDisplay(context,parentlinear,mCursor,Integer.parseInt(semnum));

        semesternum.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                fragmentClass = semesterview.class;


                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment

                Bundle bundle = new Bundle();

                int position = (Integer) v.getTag(); //get the position which the button is clicked. Refers to which sem it is
                bundle.putInt("Semester", position);
                fragment.setArguments(bundle);
                fragmentManager = ((Activity) context).getFragmentManager();

                //semview is a tag given to the fragment that is used when we try to press the back button on this fragment
                // the main activity will see if this fragment is in view when the back button is pressed
                // if it is, rather than closing the app, it will launch the overview fragment
                // can look at main activity on back pressed
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, semview).commit();


                return true;
            }

        });

        semesternum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                {
                    Intent data = ((Activity)context).getIntent();
                    if(data.getBooleanExtra("AddingModule", false))
                    {
                        //geting the module data from the intent
                        String ModuleCode = data.getStringExtra("modulecode");
                        String ModuleTitle = data.getStringExtra("moduletitle");
                        String ModuleCredit = data.getStringExtra("modulecredit");
                        String prerequisite = data.getStringExtra(KEY_PREREQUISITES);

                        DatabaseHandler db = new DatabaseHandler(context);
                        int credit = Integer.parseInt(ModuleCredit);
                        ModuleInfo mod1 = new ModuleInfo(ModuleCode, ModuleTitle, credit, prerequisite);
                        int position = (Integer) v.getTag(); //get the position which the button is clicked. Refers to which sem it is
                        db.addModtoSem(mod1, position);
                        Cursor mCursor = db.getAllModsFromSem(position);
                        parentlinear.removeAllViews();

                        //the refreshing of the linear layout view is in the constructor of moduledisplay
                        ModuleDisplay renew = new ModuleDisplay(context, parentlinear, mCursor, position); //Refreshes the linear layout view in the semester "position"

                        CharSequence text = "Module Added";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        data.removeExtra("AddingModule");


                    }

                }


            }
        });

    }


    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        View view = cursorInflater.inflate(R.layout.semadapter, parent, false);



        return view;

    }
}