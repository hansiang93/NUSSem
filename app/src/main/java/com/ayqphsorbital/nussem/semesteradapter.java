package com.ayqphsorbital.nussem;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
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

        DatabaseHandler db = new DatabaseHandler(context);
        db.getReadableDatabase();
        Cursor mCursor = db.getAllModsFromSem(Integer.parseInt(semnum)); //the integer.parseInt changes a string into an integer
        myListAdapter = new CustomAdapter(context, mCursor, 0, Integer.parseInt(semnum));
        modulelist.setAdapter(myListAdapter);

    }


    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.semadapter, parent, false);
    }
}