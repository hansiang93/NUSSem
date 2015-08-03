package com.ayqphsorbital.nussem;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by HanSiang on 30/06/2015.
 */
public class SearchResultsActivity extends AppCompatActivity {


    private TextView ResultsList;
    private Toolbar toolbar;
    private AsyncHttpClient client;
    private static final String QUERY_URL = "http://api.nusmods.com/2015-2016/modules/";
    Button addmodule;
    boolean moduleexist = false;
    String ModuleCode;
    String ModuleTitle;
    String Department;
    String ModuleDescription;
    String ModuleCredit;
    String Workload;
    String Prerequisite;
    String Preclusion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        ResultsList = (TextView) findViewById(R.id.list);


        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setHomeButtonEnabled(true); //Adds back button to results page


        Intent intent  = getIntent();
        handleIntent(intent);

        addmodule = (Button) findViewById(R.id.search_addmodule);
        addmodule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(moduleexist)
                {
                    Context context = getApplicationContext();
                    int credit = Integer.parseInt(ModuleCredit);

                    ModuleInfo mod1 = new ModuleInfo(ModuleCode, ModuleTitle, credit);

                    DatabaseHandler db = new DatabaseHandler(context);
                    db.getWritableDatabase();
                    db.addModtoSem(mod1);
                    CharSequence text = "Module Added";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Module Not Found";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }

            }
        });



    }

    //Search functions
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            showResults(query);
        }

    }

    private void showResults(String query) {
        // Query your data set and show results
        // ...
        queryNUSMods(query);

    }




    private void queryNUSMods(String searchString) {

        // Prepare your search string to be put in a URL
        // It might have reserved characters or something
        final String[] data = {""};
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // if this fails for some reason, let the user know why
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Create a client to perform networking
        client = new AsyncHttpClient();

        // Have the client get a JSONArray of data
        // and define how to respond
        client.get(QUERY_URL + urlString.toUpperCase() + "/index.json",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        //Displays results
                        moduleexist = true;
                        ModuleCode = jsonObject.optString("ModuleCode");
                        ModuleTitle = jsonObject.optString("ModuleTitle");
                        Department = jsonObject.optString("Department");
                        ModuleDescription = jsonObject.optString("ModuleDescription");
                        ModuleCredit = jsonObject.optString("ModuleCredit");
                        Workload = jsonObject.optString("Workload");
                        Prerequisite = jsonObject.optString("Prerequisite");
                        Preclusion = jsonObject.optString("Preclusion");
                        data[0] += "Module Code: " + ModuleCode + "\n \nModule Title: " + ModuleTitle
                                + "\n \nDepartment: " + Department + "\n \nModule Description: " +
                                ModuleDescription + "\n \nModule Credit: " + ModuleCredit + "\n \nWorkload"
                                + Workload + "\n \nPre-Requisite: " + Prerequisite + "\n \nPreclusion: " +
                                Preclusion + "\n";
                        ResultsList.setText(data[0]);
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                        // Display a "Toast" message
                        // to announce the failure
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                        // Log error message
                        // to help solve any problems
                        Log.e("omg android", statusCode + " " + throwable.getMessage());
                    }
                });

    }

}