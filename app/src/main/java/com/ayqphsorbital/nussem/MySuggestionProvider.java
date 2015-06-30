package com.ayqphsorbital.nussem;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by HanSiang on 30/06/2015.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.ayqphsorbital.nussem.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}