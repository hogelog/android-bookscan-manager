package org.hogel.android.bookscanmanager.app.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import com.google.inject.Inject;
import org.hogel.android.bookscanmanager.app.R;


public class BookListActivity extends RoboActionBarActivity {
    @Inject
    private LoginDialogFragment loginDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setSupportProgressBarIndeterminate(true);

        setContentView(R.layout.activity_book_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_book_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                loginDialogFragment.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
