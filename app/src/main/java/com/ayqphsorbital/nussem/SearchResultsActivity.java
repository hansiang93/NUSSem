package com.ayqphsorbital.nussem;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
public class SearchResultsActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener {


    private TextView ResultsList;
    private TextView ModCode;
    private TextView ModTitle;
    private Toolbar toolbar;
    private AsyncHttpClient client;
    private static final String QUERY_URL = "http://api.nusmods.com/2015-2016/modules/";
    Button addmodule;
    boolean moduleexist = false;
    private static final String SEM_ONE = "Sem_One";
    String ModuleCode;
    String ModuleTitle;
    String Department;
    String ModuleDescription;
    String ModuleCredit;
    String Workload;
    String Prerequisite;
    String Preclusion;
    int key = 1;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        ResultsList = (TextView) findViewById(R.id.list);
        ModCode = (TextView) findViewById(R.id.ModCode);
        ModTitle = (TextView) findViewById(R.id.ModTitle);


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
                    DatabaseHandler db = new DatabaseHandler(context);
                    ModuleInfo mod1 = new ModuleInfo(ModuleCode, ModuleTitle, credit);

                    if(!db.existinallsem(mod1)) {

                        CharSequence text = "Select semester for module to be added";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent semIntent = new Intent();
                        semIntent.setClass(SearchResultsActivity.this, MainActivity.class);
                        semIntent.putExtra("query", true);
                        startActivityForResult(semIntent, key);


                    }
                    else{
                        CharSequence text = "Module Already Added!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String semnum = data.getStringExtra("Semester");
        DatabaseHandler db = new DatabaseHandler(this);
        int credit = Integer.parseInt(ModuleCredit);
        ModuleInfo mod1 = new ModuleInfo(ModuleCode, ModuleTitle, credit);
        db.addModtoSem(mod1, Integer.parseInt(semnum));
        CharSequence text = "Module Added";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();

        Intent gobacktomain = new Intent(this, MainActivity.class);
        gobacktomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(gobacktomain);
        finish();
    }

    //Search functions
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            setTitle(query.toUpperCase());

            showResults(query);
        }

        //This is to allow long pressing of the ModuleDisplay adapter
        //to also search for the mod. 
        if (intent.getBooleanExtra("SPECIAL", false)) {
            String query = intent.getStringExtra("QUERY");
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            setTitle(query.toUpperCase());

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
                        ModCode.setText(ModuleCode);
                        ModuleTitle = jsonObject.optString("ModuleTitle");
                        ModTitle.setText(ModuleTitle);
                        Department = jsonObject.optString("Department");
                        ModuleDescription = jsonObject.optString("ModuleDescription");
                        ModuleCredit = jsonObject.optString("ModuleCredit");
                        Workload = jsonObject.optString("Workload");
                        Prerequisite = jsonObject.optString("Prerequisite");
                        Preclusion = jsonObject.optString("Preclusion");
                        data[0] += "Department: " + Department + "\n \nModule Description: " +
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}