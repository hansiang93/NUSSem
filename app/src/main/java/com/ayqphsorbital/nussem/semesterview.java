package com.ayqphsorbital.nussem;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by ang on 17/8/2015.
 */
public class semesterview extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView mListView;
    CustomAdapter myListAdapter;
    int _semnum;
    private OnFragmentInteractionListener mListener;
    TextView _semheader;
    TextView _gpa;
    TextView _totalcredit;
    LinearLayout _modulelist;
    private static final String KEY_CODE = "ModuleCode";
    private static final String KEY_TITLE = "ModuleTitle";
    private static final String KEY_CREDIT = "ModuleCredit";
    private LayoutInflater cursorInflater;


    double Aplus = 5.0;
    double Agrade = 5.0;
    double Aminus = 4.5;
    double Bplus = 4.0;
    double Bgrade = 3.5;
    double Bminus = 3.0;
    double Cplus = 2.5;
    double Cgrade = 2.0;
    double Dplus = 1.5;
    double Dgrade = 1.0;
    double Fgrade = 0;
    double satisfactory = -10;
    double unsatisfactor = -10;



    public semesterview() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Bundle bundle = this.getArguments();

        //geting what semester is being long clicked. so as to launch the correct semester in this frag
        _semnum = bundle.getInt("Semester");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.semesterview, container, false);
        _semheader = (TextView) rootview.findViewById(R.id.semview_header);
        _gpa = (TextView) rootview.findViewById(R.id.semview_GPA);
        _totalcredit = (TextView) rootview.findViewById(R.id.semview_mctotal);
        _modulelist = (LinearLayout) rootview.findViewById(R.id.semview_modlist);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        db.getReadableDatabase();
        Cursor mCursor = db.getAllModsFromSem(_semnum);

        String creditstaken = "Total Credits taken = " + totalcredit(mCursor) + "MC";
        _totalcredit.setText(creditstaken);

        _semheader.setText("Semester " + _semnum);

        populateview(_modulelist, mCursor);

        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onsemesterviewFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onsemesterviewFragmentInteraction(Uri uri);
    }

    protected int totalcredit(Cursor c)
    {
        int total = 0;
        c.moveToFirst();

        while(!c.isAfterLast())
        {
            total = total + c.getInt(c.getColumnIndex(KEY_CREDIT));
            c.moveToNext();
        }
        return total;

    }

    protected void populateview (LinearLayout view, Cursor c)
    {
        c.moveToFirst();
        cursorInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        cursorInflater.inflate(R.layout.semesterview, null);

        while(!c.isAfterLast()) {
            View ChildView = cursorInflater.inflate(R.layout.modwithgradeadapter, null);

            TextView modulecode = (TextView) ChildView.findViewById(R.id.adapter_ModuleCode);
            TextView modulename = (TextView) ChildView.findViewById(R.id.adapter_ModuleName);
            TextView modulecredit = (TextView) ChildView.findViewById(R.id.adapter_ModuleCredit);

            final String title = c.getString(c.getColumnIndex(KEY_TITLE));
            final String code = c.getString(c.getColumnIndex(KEY_CODE));
            String credit = c.getInt(c.getColumnIndex(KEY_CREDIT)) + "MC";

            modulecode.setText(code);
            modulecredit.setText(credit);
            modulename.setText(title);

            Spinner dropdown = (Spinner) ChildView.findViewById(R.id.spinner1);
            String[] items = new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D+", "D", "F", "S", "U", "NIL"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
            dropdown.setAdapter(adapter);
            view.addView(ChildView);
            DatabaseHandler db = new DatabaseHandler(getActivity());
            int gradepos = db.getgrade(_semnum, code);
            dropdown.setSelection(gradepos);

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DatabaseHandler db = new DatabaseHandler(getActivity());
                    db.editgrade(_semnum, code, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }


            });


                    c.moveToNext();
        }


    }





}



