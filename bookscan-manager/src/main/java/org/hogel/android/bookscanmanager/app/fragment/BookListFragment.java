package org.hogel.android.bookscanmanager.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.squareup.otto.Subscribe;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.activity.BookDetailActivity;
import org.hogel.android.bookscanmanager.app.dao.DatabaseHelper;
import org.hogel.android.bookscanmanager.app.dao.record.BookRecord;
import org.hogel.android.bookscanmanager.app.event.SyncBooksEvent;
import org.hogel.android.bookscanmanager.app.util.BusProvider;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.android.bookscanmanager.app.util.Toasts;
import org.hogel.android.bookscanmanager.app.view.adapter.BookListAdapter;
import org.hogel.bookscan.AsyncBookscanClient;
import org.hogel.bookscan.listener.FetchBooksListener;
import org.hogel.bookscan.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.inject.InjectView;

import java.sql.SQLException;
import java.util.List;

public class BookListFragment extends BookListTabFragment {
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

    @InjectView(R.id.swipe_container)
    private SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.book_list)
    private ListView bookListView;

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
        BusProvider.unregister(this);
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

        booksAdapter = new BookListAdapter(context, books);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookListView.setAdapter(booksAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = books.get(position).getFilename();
                startActivity(BookDetailActivity.createIntent(context, filename));
            }
        });
        bookListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition;
                if (bookListView.getChildCount() == 0) {
                    topRowVerticalPosition = 0;
                } else {
                    topRowVerticalPosition = bookListView.getChildAt(0).getTop();
                }
                swipeContainer.setEnabled(topRowVerticalPosition >= 0);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                loginAndSyncBookList();
            }
        });
    }

    private void loginAndSyncBookList() {
        if (!client.isLogin()) {
            loginDialogFragment.show();
        } else {
            syncBookList();
        }
    }

    private void syncBookList() {
        setProgress(true);

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
        setProgress(false);

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
        setProgress(false);

        Toasts.show(context, R.string.action_sync_fail);
    }

    private void setProgress(boolean isProgress) {
        final ActionBarActivity activity = (ActionBarActivity) getActivity();
        if (activity != null) {
            activity.setSupportProgressBarIndeterminateVisibility(isProgress);
        }
    }
}
