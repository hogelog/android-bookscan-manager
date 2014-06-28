package org.hogel.android.bookscanmanager.app.activity;

import com.google.common.collect.Lists;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.dao.DatabaseHelper;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.android.bookscanmanager.app.util.Toasts;
import org.hogel.bookscan.AsyncBookscanClient;
import org.hogel.bookscan.listener.FetchBooksListener;
import org.hogel.bookscan.listener.LoginListener;
import org.hogel.bookscan.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import roboguice.fragment.RoboListFragment;

//import org.hogel.android.bookscanmanager.app.bookscan.BookscanClient;
//import org.hogel.android.bookscanmanager.app.bookscan.model.Book;

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
    private static final Logger LOG = LoggerFactory.getLogger(BookListFragment.class);

    @Inject
    private Context context;

    @Inject
    private AsyncBookscanClient client;

    @Inject
    private FragmentManager fragmentManager;
    @Inject
    private LoginDialogFragment loginDialogFragment;

    @Inject
    private DatabaseHelper databaseHelper;

    @Inject
    private Preferences preferences;

    private Dao<Book, String> bookDao;

    private final List<Book> books = Lists.newArrayList();

    private ArrayAdapter<Book> booksAdapter;

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    public interface Callbacks {
        public void onItemSelected(String id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    public BookListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookDao = databaseHelper.getBookDao();

        try {
            books.addAll(bookDao.queryForAll());
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

        booksAdapter = new ArrayAdapter<>(
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

        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        mCallbacks.onItemSelected(books.get(position).getFilename());
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
                loginDialogFragment.show();
                return true;
            case R.id.action_sync:
                if (!client.isLogin()) {
                    if (preferences.hasLoginPreference()) {
                        client.login(preferences.getLoginMail(), preferences.getLoginPass(), new LoginListener() {
                            @Override
                            public void onSuccess() {
                                syncBookList();
                            }

                            @Override
                            public void onError(Exception e) {
                                LOG.error(e.getMessage(), e);
                                Toasts.show(context, R.string.action_login_fail);
                            }
                        });
                    } else {
                        loginDialogFragment.show();
                    }
                } else {
                    syncBookList();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncBookList() {
        client.fetchBooks(new FetchBooksListener() {
            @Override
            public void onSuccess(List<Book> fetchBooks) {
                try {
                    TableUtils.clearTable(databaseHelper.getConnectionSource(), Book.class);
                    for (Book book : fetchBooks) {
                        bookDao.create(book);
                    }
                    books.clear();
                    books.addAll(fetchBooks);
                    booksAdapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                    Toasts.show(context, R.string.action_sync_fail);
                }
            }

            @Override
            public void onError(Exception e) {
                LOG.error(e.getMessage(), e);
                Toasts.show(context, R.string.action_sync_fail);
            }
        });
    }
}