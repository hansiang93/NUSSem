package com.ayqphsorbital.nussem;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by ang on 17/8/2015.
 */
public class SemesterOverview extends Fragment {

    //required empty constructor
    public SemesterOverview()
    {}
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View rootview = inflater.inflate(R.layout.jgkj, container, false);

        /*

        String[] fromColumns = new String[]{"ModuleCode"};
        int[] toViews = {android.R.id.text1};

        DatabaseHandler db = new DatabaseHandler(getActivity());
        db.getReadableDatabase();
        Cursor mCursor = db.getAllModsFromSem(1);
        ListView modulelist = (ListView)rootview .findViewById(android.R.id.list);



        myListAdapter = new CustomAdapter(getActivity(), mCursor, 0,1);
        modulelist.setAdapter(myListAdapter);

        return rootview;


    }

*/


}
