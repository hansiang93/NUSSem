package com.ayqphsorbital.nussem;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by HanSiang on 30/06/2015.
 */

public class MySuggestionProvider extends ContentProvider {
    public final static String AUTHORITY = "com.ayqphsorbital.nussem.MySuggestionProvider";
    public final static Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY+ "/modules");

    private DatabaseHandler db;

    @Override
    public boolean onCreate() {

        db = new DatabaseHandler(getContext());
        db.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        c = db.getModules(selectionArgs);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}