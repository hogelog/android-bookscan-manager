package org.hogel.android.bookscan_manager.app.activity;

import android.os.Bundle;
import roboguice.activity.RoboActivity;

import javax.inject.Inject;

public class BookscanPreferenceActivity extends RoboActivity {
    @Inject
    BookscanPreferenceFragment bookscanPreferenceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, bookscanPreferenceFragment)
            .commit();
    }
}
