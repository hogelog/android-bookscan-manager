package org.hogel.android.bookscan_manager.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import org.hogel.android.bookscan_manager.app.R;
import org.hogel.android.bookscan_manager.app.bookscan.BookscanClient;
import org.hogel.android.bookscan_manager.app.bookscan.CookieManager;
import org.hogel.android.bookscan_manager.app.bookscan.model.Book;
import org.hogel.android.bookscan_manager.app.dao.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.fragment.RoboListFragment;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

/**
 * A list fragment representing a list of Books. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link BookDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class BookListFragment extends RoboListFragment {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelper.class);

    @Inject
    private Context context;

    @Inject
    private BookscanClient bookscanClient;
    @Inject
    private CookieManager cookieManager;

    @Inject
    private FragmentManager fragmentManager;
    @Inject
    private LoginDialogFragment loginDialogFragment;

    @Inject
    private Injector injector;

    @Inject
    private DatabaseHelper databaseHelper;

    private Dao<Book, String> bookDao;

    private final List<Book> books = Lists.newArrayList();

    private ArrayAdapter<Book> booksAdapter;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String hash) {
        }
    };

    public BookListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper.getWritableDatabase();
        bookDao = databaseHelper.getBookDao();

        try {
            books.addAll(bookDao.queryForAll());
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

        booksAdapter = new ArrayAdapter<Book>(
            context,
            android.R.layout.simple_list_item_activated_1,
            android.R.id.text1,
            books);
        setListAdapter(booksAdapter);

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        mCallbacks.onItemSelected(books.get(position).getHash());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.actions_book_list, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                loginDialogFragment.show(fragmentManager, "login");
                return true;
            case R.id.action_sync:
                if (!bookscanClient.isLogin()) {
                    bookscanClient.login();
                }
                syncBookList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncBookList() {
        List<Book> fetchBooks = bookscanClient.fetchBookList();
        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(), Book.class);
            for (Book book : fetchBooks) {
                bookDao.create(book);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        books.clear();
        books.addAll(fetchBooks);
        booksAdapter.notifyDataSetChanged();
    }
}
