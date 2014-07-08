package org.hogel.android.bookscanmanager.app.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.bookscan.BookscanDownloadManager;
import org.hogel.android.bookscanmanager.app.dao.BookDaoHelper;
import org.hogel.bookscan.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.sql.SQLException;

public class BookDetailFragment extends RoboFragment {
    private static final Logger LOG = LoggerFactory.getLogger(BookDetailFragment.class);

    public static final String ARG_ITEM_ID = "item_id";

    @Inject
    private BookscanDownloadManager downloadManager;

    @Inject
    private BookDaoHelper bookDaoHelper;

    @Inject
    private FragmentManager fragmentManager;

    @InjectView(R.id.book_title)
    private TextView bookTitleView;

    @InjectView(R.id.book_image)
    private ImageView bookImageView;

    @InjectView(R.id.download_button)
    private Button downloadButton;

    private Book book;

    public static BookDetailFragment createFragment(String filename) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, filename);
        fragment.setArguments(args);
        return fragment;
    }

    private String getArgumentFilename() {
        return getArguments().getString(ARG_ITEM_ID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            book = bookDaoHelper.dao().queryForId(getArgumentFilename()).toBook();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
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

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadManager.download(book);
                }
            });
        }
    }
}
