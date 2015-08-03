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

public class CustomAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    private static final String KEY_CODE = "ModuleCode";
    private static final String KEY_TITLE = "ModuleTitle";
    private static final String KEY_CREDIT = "ModuleCredit";
    Button removemod;

    // Default constructor
    public CustomAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView modulecode = (TextView) view.findViewById(R.id.adapter_ModuleCode);
        TextView modulename = (TextView) view.findViewById(R.id.adapter_ModuleName);
        TextView modulecredit = (TextView) view.findViewById(R.id.adapter_ModuleCredit);

        final String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
        final String code = cursor.getString(cursor.getColumnIndex(KEY_CODE));
        String credit = cursor.getString(cursor.getColumnIndex(KEY_CREDIT));

        modulecode.setText(code);
        modulecredit.setText(credit);
        modulename.setText(title);

        removemod = (Button) view.findViewById(R.id.adapter_crossbutton);
        removemod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.getWritableDatabase();
                    ModuleInfo mod = new ModuleInfo(code, title);
                    db.deleteModfromsem(mod);
                    Cursor mCursor = db.getAllModsFromSem();
                    changeCursor(mCursor);

                    CharSequence text = "Module Removed";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }


            }
        });
    }


    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                // R.layout.list_row is your xml layout for each row
                return cursorInflater.inflate(R.layout.adapter, parent, false);
            }
        }