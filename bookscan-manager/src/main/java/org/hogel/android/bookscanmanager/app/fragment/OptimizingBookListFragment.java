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
import com.squareup.otto.Subscribe;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.event.SyncOptimizingBooksEvent;
import org.hogel.android.bookscanmanager.app.util.BusProvider;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.android.bookscanmanager.app.util.Toasts;
import org.hogel.android.bookscanmanager.app.view.adapter.ListScrollAdapter;
import org.hogel.android.bookscanmanager.app.view.adapter.OptimizingBookListAdapter;
import org.hogel.bookscan.BookscanClient;
import org.hogel.bookscan.model.OptimizingBook;
import org.hogel.bookscan.reqeust.RequestErrorListener;
import org.hogel.bookscan.reqeust.RequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

public class OptimizingBookListFragment extends RoboFragment {
    private static final Logger LOG = LoggerFactory.getLogger(OptimizingBookListFragment.class);

    @Inject
    Context context;

    @Inject
    BookscanClient client;

    @Inject
    FragmentManager fragmentManager;

    @Inject
    LoginDialogFragment loginDialogFragment;

    @Inject
    Preferences preferences;

    @InjectView(R.id.swipe_container)
    private SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.book_list)
    private ListView bookListView;

    private final List<OptimizingBook> books = new ArrayList<>();

    private ArrayAdapter<OptimizingBook> booksAdapter;

    private View noItemView;

    public OptimizingBookListFragment() {
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

        booksAdapter = new OptimizingBookListAdapter(context, books);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        noItemView = inflater.inflate(R.layout.optimizing_book_list_no_item, null, false);
        return inflater.inflate(R.layout.fragment_optimizing_book_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookListView.addHeaderView(noItemView);
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

        client
            .fetchOptimizingBooks()
            .listener(new RequestListener<List<OptimizingBook>>() {
                @Override
                public void success(List<OptimizingBook> result) {
                    BusProvider.post(SyncOptimizingBooksEvent.success(result));
                }
            })
            .error(new RequestErrorListener() {
                @Override
                public void error(Exception e) {
                    LOG.error(e.getMessage(), e);
                    BusProvider.post(SyncOptimizingBooksEvent.failure());
                }
            })
            .execute();
    }

    @Subscribe
    public void syncBooksSuccess(SyncOptimizingBooksEvent.Success success) {
        setProgress(false);

        books.clear();
        books.addAll(success.getBooks());
        if (books.isEmpty()) {
            noItemView.setVisibility(View.GONE);
        } else {
            noItemView.setVisibility(View.VISIBLE);
        }
        booksAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void syncBooksFailure(SyncOptimizingBooksEvent.Failure failure) {
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
