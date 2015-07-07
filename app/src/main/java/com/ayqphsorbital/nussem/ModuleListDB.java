package com.ayqphsorbital.nussem;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by HanSiang on 07/07/2015.
 * http://wptrafficanalyzer.in/blog/adding-custom-search-suggestions-to-search-dialog-from-sqlite-database-in-android/
 */
public class ModuleListDB {

    private static final String DBNAME = "ModuleList";
    private static final int VERSION = 1;
    private DatabaseHandler mDatabaseHandler;
    private static final String FIELD_ID = "_id";
    private static final String FIELD_MODULECODE = "ModuleCode";
    private static final String FIELD_MODULETITLE = "ModuleTitle";
    private HashMap<String, String> mModMap;

    public ModuleListDB(Context context){

    }


}
