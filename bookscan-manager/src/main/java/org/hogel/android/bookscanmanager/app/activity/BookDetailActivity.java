package org.hogel.android.bookscanmanager.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.google.inject.Inject;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.fragment.BookDetailFragment;
import org.hogel.bookscan.model.Book;
import roboguice.activity.RoboFragmentActivity;


public class BookDetailActivity extends RoboFragmentActivity {

    @Inject
    private FragmentManager fragmentManager;

    public static Intent createIntent(Context context, Book book) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(BookDetailFragment.ARG_ITEM_ID, book.getFilename());
        return intent;
    }

    private String getArgumentFilename() {
        return getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            final String filename = getArgumentFilename();
            BookDetailFragment fragment = BookDetailFragment.createFragment(filename);
            fragmentManager
                .beginTransaction()
                .add(R.id.book_detail_container, fragment)
                .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
