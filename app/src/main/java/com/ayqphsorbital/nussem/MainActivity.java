package com.ayqphsorbital.nussem;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends AppCompatActivity implements
        MainPage.OnFragmentInteractionListener,
        OneSemOne.OnFragmentInteractionListener,
        OneSemTwo.OnFragmentInteractionListener,
        SearchView.OnQueryTextListener {

    private DrawerLayout dlDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Fragment fragment = null;
    private Class fragmentClass;
    private FragmentManager fragmentManager;
    private AsyncHttpClient client;
    DatabaseHandler db;
    DBupdate BackgroundDBupdater;
    private static final String QUERY_URL = "http://api.nusmods.com/2015-2016/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        dlDrawer.setDrawerListener(drawerToggle);


        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        drawerToggle.syncState();

        db = new DatabaseHandler(this);


        //Recents suggestions for searchbar
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }


        //Setting first loading page to be a new frame
        fragmentClass = MainPage.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        //Create Database
        if (db.getModsCount()<10) {
            BackgroundDBupdater = new DBupdate();
            BackgroundDBupdater.run();
        };


        //creating the button to add semesters





    }



    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        fragmentClass = OneSemOne.class;
        switch (menuItem.getItemId()) {
            case R.id.main_page:
                fragmentClass = MainPage.class;
                break;
            case R.id.one_sem_one:
                fragmentClass = OneSemOne.class;
                break;
            case R.id.one_sem_two:
                fragmentClass = OneSemTwo.class;
                break;
            case R.id.nav_third_fragment:
                //  fragmentClass = ThirdFragment.class;
                break;
            default:
                fragmentClass = MainPage.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        dlDrawer.closeDrawers();
    }



    private class DBupdate implements Runnable {



        @Override
        public void run() {

            // Moves the current Thread into the background
            //setThreadPriority(THREAD_PRIORITY_BACKGROUND);

            Log.d("UPDATING UPDATING", "DATABASE..");
            UpdateNUSModList();

        }

            /*if (db.getModsCount() == 0 ) {


                Log.d("UPDATING UPDATING", "DATABASE..");
                UpdateNUSModList();
            }
        else {

                Log.d("DATABASE EXISTS ", "Reading ..");
                ReadDB();
            }
        }
           */
        private void UpdateNUSModList() {


            // Create a client to perform networking
            client = new AsyncHttpClient();

            // Have the client get a JSONArray of data
            // and define how to respond
            client.get(QUERY_URL + "moduleList.json",
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(final JSONArray jsonArray) {
                            //Displays results

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d("Insert: ", "Inserting ..");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    String ModuleCode = jsonArray.getJSONObject(i).optString("ModuleCode");
                                    String ModuleTitle = jsonArray.getJSONObject(i).optString("ModuleTitle");
                                    db.addMod(new ModuleInfo(ModuleCode, ModuleTitle));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                                    Log.d("Insert: ", "Finished inserting DB ..");

                        }
                            }).start();

                            Toast.makeText(getApplicationContext(), "Success ", Toast.LENGTH_LONG).show();
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
        private void ReadDB(){

            // Reading all contacts
            Log.d("Reading: ", "Reading all contacts..");
            List<ModuleInfo> contacts = db.getAllMods();

            for (ModuleInfo cn : contacts) {
                String log = "Id: "+cn.getID()+" ,Code: " + cn.getModuleCode() + " ,Title: " + cn.getModuleTitle();
                // Writing Contacts to log
                Log.d("Name: ", log);
            }
        }
    }





    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, dlDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                dlDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Button button = (Button) findViewById(R.id.addsem);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


}