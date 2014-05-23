package org.hogel.android.bookscan_manager.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import org.hogel.android.bookscan_manager.app.R;
import org.hogel.android.bookscan_manager.app.bookscan.BookscanClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboFragmentActivity;

import javax.inject.Inject;


/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link BookListFragment} and the item details
 * (if present) is a {@link BookDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link BookListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class BookListActivity extends RoboFragmentActivity
        implements BookListFragment.Callbacks {
    private static final Logger LOG = LoggerFactory.getLogger(BookListActivity.class);

    @Inject
    private BookscanClient bookscanClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_book_list);
    }

    /**
     * Callback method from {@link BookListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        Intent detailIntent = new Intent(this, BookDetailActivity.class);
        detailIntent.putExtra(BookDetailFragment.ARG_ITEM_ID, id);
        startActivity(detailIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
