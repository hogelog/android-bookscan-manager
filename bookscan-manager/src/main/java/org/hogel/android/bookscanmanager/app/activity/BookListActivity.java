package org.hogel.android.bookscanmanager.app.activity;

import android.os.Bundle;
import android.view.Window;
import org.hogel.android.bookscanmanager.app.R;


public class BookListActivity extends RoboActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setSupportProgressBarIndeterminate(true);

        setContentView(R.layout.activity_book_list);
    }
}
