package org.hogel.android.bookscanmanager.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.squareup.otto.Subscribe;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.dao.DatabaseHelper;
import org.hogel.android.bookscanmanager.app.dao.record.BookRecord;
import org.hogel.android.bookscanmanager.app.event.LoginEvent;
import org.hogel.android.bookscanmanager.app.event.SyncBooksEvent;
import org.hogel.android.bookscanmanager.app.util.BusProvider;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.android.bookscanmanager.app.util.Toasts;
import org.hogel.bookscan.AsyncBookscanClient;
import org.hogel.bookscan.listener.FetchBooksListener;
import org.hogel.bookscan.listener.LoginListener;
import org.hogel.bookscan.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.fragment.RoboListFragment;

import java.sql.SQLException;
import java.util.List;

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

    private Dao<BookRecord, String> bookDao;

    private final List<Book> books = Lists.newArrayList();

    private ArrayAdapter<Book> booksAdapter;

    public BookListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.register(this);
    }

    @Override
    public void onPause() {
        BusProvider.register(this);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookDao = databaseHelper.getBookDao();

        try {
            List<BookRecord> bookRecords = bookDao.queryForAll();
            for (BookRecord bookRecord : bookRecords) {
                books.add(bookRecord.toBook());
            }
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
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        String filename = books.get(position).getFilename();
        Intent detailIntent = new Intent(getActivity(), BookDetailActivity.class);
        detailIntent.putExtra(BookDetailFragment.ARG_ITEM_ID, filename);
        startActivity(detailIntent);
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
                        final String loginMail = preferences.getLoginMail();
                        final String loginPass = preferences.getLoginPass();
                        client.login(loginMail, loginPass, new LoginListener() {
                            @Override
                            public void onSuccess() {
                                BusProvider.post(LoginEvent.success(loginMail, loginPass));
                            }

                            @Override
                            public void onError(Exception e) {
                                LOG.error(e.getMessage(), e);
                                BusProvider.post(LoginEvent.failure());
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

    @Subscribe
    public void loginSuccess(LoginEvent.Success success) {
        preferences.putCookies(client.getCookies());
        syncBookList();
    }

    @Subscribe
    public void loginFailure(LoginEvent.Failure failure) {
        Toasts.show(context, R.string.action_login_fail);
    }

    private void syncBookList() {
        client.fetchBooks(new FetchBooksListener() {
            @Override
            public void onSuccess(List<Book> fetchBooks) {
                BusProvider.post(SyncBooksEvent.success(fetchBooks));
            }

            @Override
            public void onError(Exception e) {
                LOG.error(e.getMessage(), e);
                BusProvider.post(SyncBooksEvent.failure());
            }
        });
    }

    @Subscribe
    public void syncBooksSuccess(SyncBooksEvent.Success success) {
        try {
            databaseHelper.clearTable(BookRecord.class);
            for (Book book : success.getBooks()) {
                bookDao.create(new BookRecord(book));
            }
            books.clear();
            books.addAll(success.getBooks());
            booksAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            Toasts.show(context, R.string.action_sync_fail);
        }
    }

    @Subscribe
    public void syncBooksFailure(SyncBooksEvent.Failure failure) {
        Toasts.show(context, R.string.action_sync_fail);
    }
}
