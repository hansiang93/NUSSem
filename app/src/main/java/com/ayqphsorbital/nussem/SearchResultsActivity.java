package com.ayqphsorbital.nussem;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
                        String ModuleCode = jsonObject.optString("ModuleCode");
                        String ModuleTitle = jsonObject.optString("ModuleTitle");
                        String Department = jsonObject.optString("Department");
                        String ModuleDescription = jsonObject.optString("ModuleDescription");
                        String ModuleCredit = jsonObject.optString("ModuleCredit");
                        String Workload = jsonObject.optString("Workload");
                        String Prerequisite = jsonObject.optString("Prerequisite");
                        String Preclusion = jsonObject.optString("Preclusion");
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