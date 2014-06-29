package org.hogel.android.bookscanmanager.app.activity;

import com.j256.ormlite.dao.Dao;

import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.bookscan.BookscanDownloadManager;
import org.hogel.android.bookscanmanager.app.dao.DatabaseHelper;
import org.hogel.android.bookscanmanager.app.dao.record.BookRecord;
import org.hogel.bookscan.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.SQLException;

import javax.inject.Inject;

import roboguice.fragment.RoboFragment;

/**
 * A fragment representing a single Book detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends RoboFragment implements View.OnClickListener {
    private static final Logger LOG = LoggerFactory.getLogger(BookDetailFragment.class);

    public static final String ARG_ITEM_ID = "item_id";

    @Inject
    private BookscanDownloadManager downloadManager;

    @Inject
    private DatabaseHelper databaseHelper;

    private Dao<BookRecord, String> bookDao;

    private Book book;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookDao = databaseHelper.getBookDao();
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            try {
                book = bookDao.queryForId(getArguments().getString(ARG_ITEM_ID)).toBook();
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);

        if (book != null) {
            ((TextView) rootView.findViewById(R.id.book_detail)).setText(book.getFilename());

            Button downloadButton = (Button) rootView.findViewById(R.id.download_button);
            downloadButton.setOnClickListener(this);
        }

        return rootView;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.download_button:
                downloadManager.download(book);
                break;
        }
    }
}
