package org.hogel.android.bookscanmanager.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.squareup.otto.Subscribe;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.dao.DatabaseHelper;
import org.hogel.android.bookscanmanager.app.dao.record.OptimizedBookRecord;
import org.hogel.android.bookscanmanager.app.event.SyncOptimizedBooksEvent;
import org.hogel.android.bookscanmanager.app.util.BusProvider;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.android.bookscanmanager.app.util.Toasts;
import org.hogel.android.bookscanmanager.app.view.adapter.ListScrollAdapter;
import org.hogel.android.bookscanmanager.app.view.adapter.OptimizedBookListAdapter;
import org.hogel.bookscan.AsyncBookscanClient;
import org.hogel.bookscan.listener.FetchOptimizedBooksListener;
import org.hogel.bookscan.model.OptimizedBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.inject.InjectView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OptimizedBookListFragment extends BookListTabFragment {
    private static final Logger LOG = LoggerFactory.getLogger(OptimizedBookListFragment.class);

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

    private Dao<OptimizedBookRecord, String> bookDao;

    private final List<OptimizedBook> books = new ArrayList<>();

    private ArrayAdapter<OptimizedBook> booksAdapter;

    public OptimizedBookListFragment() {
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

        bookDao = databaseHelper.getOptimizedBookDao();

        try {
            List<OptimizedBookRecord> bookRecords = bookDao.queryForAll();
            for (OptimizedBookRecord bookRecord : bookRecords) {
                books.add(bookRecord.toOptimizedBook());
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

        booksAdapter = new OptimizedBookListAdapter(context, books);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_optimized_book_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookListView.setAdapter(booksAdapter);
        bookListView.setOnScrollListener(new ListScrollAdapter() {
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
                syncBookList();
            }
        });
    }


    private void syncBookList() {
        if (!client.isLogin()) {
            loginDialogFragment.show();
            return;
        }

        setProgress(true);

        client.fetchOptimizedBooks(new FetchOptimizedBooksListener() {
            @Override
            public void onSuccess(List<OptimizedBook> optimizedBooks) {
                BusProvider.post(SyncOptimizedBooksEvent.success(optimizedBooks));
            }

            @Override
            public void onError(Exception e) {
                LOG.error(e.getMessage(), e);
                BusProvider.post(SyncOptimizedBooksEvent.failure());
            }
        });
    }

    @Subscribe
    public void syncBooksSuccess(SyncOptimizedBooksEvent.Success success) {
        setProgress(false);

        try {
            databaseHelper.clearTable(OptimizedBookRecord.class);
            for (OptimizedBook book : success.getBooks()) {
                bookDao.create(new OptimizedBookRecord(book));
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
    public void syncBooksFailure(SyncOptimizedBooksEvent.Failure failure) {
        setProgress(false);

        Toasts.show(context, R.string.action_sync_fail);
    }

    private void setProgress(boolean isProgress) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.setProgressBarIndeterminateVisibility(isProgress);
        }
    }
}
