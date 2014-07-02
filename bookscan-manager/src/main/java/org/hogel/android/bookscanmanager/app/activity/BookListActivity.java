package org.hogel.android.bookscanmanager.app.activity;

import org.hogel.android.bookscanmanager.app.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.view.Window;

import roboguice.activity.RoboFragmentActivity;


public class BookListActivity extends RoboFragmentActivity {
    private static final Logger LOG = LoggerFactory.getLogger(BookListActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_book_list);
    }
}
