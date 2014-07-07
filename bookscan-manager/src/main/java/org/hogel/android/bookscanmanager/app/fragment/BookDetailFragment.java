package org.hogel.android.bookscanmanager.app.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.j256.ormlite.dao.Dao;
import com.squareup.picasso.Picasso;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.bookscan.BookscanDownloadManager;
import org.hogel.android.bookscanmanager.app.dao.DatabaseHelper;
import org.hogel.android.bookscanmanager.app.dao.record.BookRecord;
import org.hogel.bookscan.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.sql.SQLException;

public class BookDetailFragment extends RoboFragment implements View.OnClickListener {
    private static final Logger LOG = LoggerFactory.getLogger(BookDetailFragment.class);

    public static final String ARG_ITEM_ID = "item_id";

    @Inject
    private BookscanDownloadManager downloadManager;

    @Inject
    private DatabaseHelper databaseHelper;

    @InjectView(R.id.book_title)
    private TextView bookTitleView;

    @InjectView(R.id.book_image)
    private ImageView bookImageView;

    @InjectView(R.id.download_button)
    private Button downloadButton;

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
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (book != null) {
            bookTitleView.setText(book.getFilename());
            String imageUrl = book.getImageUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(getActivity()).load(imageUrl).into(bookImageView);
            }

            downloadButton.setOnClickListener(this);
        }
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
