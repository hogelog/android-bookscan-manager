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
import org.hogel.android.bookscanmanager.app.dao.OptimizedBookDaoHelper;
import org.hogel.android.bookscanmanager.app.dao.record.OptimizedBookRecord;
import org.hogel.bookscan.model.Book;
import org.hogel.bookscan.model.OptimizedBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDetailFragment extends RoboFragment {
    private static final Logger LOG = LoggerFactory.getLogger(BookDetailFragment.class);

    public static final String ARG_ITEM_ID = "item_id";

    @Inject
    private BookscanDownloadManager downloadManager;

    @Inject
    private BookDaoHelper bookDaoHelper;

    @Inject
    private OptimizedBookDaoHelper optimizedBookDaoHelper;

    @Inject
    private FragmentManager fragmentManager;

    @InjectView(R.id.book_title)
    private TextView bookTitleView;

    @InjectView(R.id.book_image)
    private ImageView bookImageView;

    @InjectView(R.id.download_button)
    private Button downloadButton;

    @InjectView(R.id.optimized_books_container)
    private View optimizedBooksContainer;

    @InjectView(R.id.optimized_books_list)
    private ViewGroup optimizedBooksList;

    private Book book;

    private List<OptimizedBook> optimizedBooks = new ArrayList<>();

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
            String filename = getArgumentFilename();
            book = bookDaoHelper.dao().queryForId(filename).toBook();
            List<OptimizedBookRecord> optimizedBookRecords = optimizedBookDaoHelper.dao().queryForAll();
            for (OptimizedBookRecord optimizedBookRecord : optimizedBookRecords) {
                if (optimizedBookRecord.getFilename().endsWith(filename)) {
                    optimizedBooks.add(optimizedBookRecord.toOptimizedBook());
                }
            }
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

        if (optimizedBooks.size() > 0) {
            optimizedBooksContainer.setVisibility(View.VISIBLE);
            for (OptimizedBook optimizedBook : optimizedBooks) {
                LayoutInflater layoutInflater = getLayoutInflater(savedInstanceState);
                View optimizedBookView = createOptimizedBookView(layoutInflater, optimizedBook);
                optimizedBooksList.addView(optimizedBookView);
            }
        } else {
            optimizedBooksContainer.setVisibility(View.GONE);
        }
    }

    private View createOptimizedBookView(LayoutInflater layoutInflater, final OptimizedBook optimizedBook) {
        View optimizedBookView = layoutInflater.inflate(R.layout.book_detail_optimized_book_list_item, null);
        TextView optimizedBookTitleView = (TextView) optimizedBookView.findViewById(R.id.book_title);
        optimizedBookTitleView.setText(optimizedBook.getFilename());

        Button downloadButton = (Button) optimizedBookView.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.download(optimizedBook);
            }
        });
        return optimizedBookView;
    }
}
