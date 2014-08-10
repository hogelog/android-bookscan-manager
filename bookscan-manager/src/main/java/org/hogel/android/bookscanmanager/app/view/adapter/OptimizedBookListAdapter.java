package org.hogel.android.bookscanmanager.app.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.bookscan.BookscanDownloadManager;
import org.hogel.bookscan.model.OptimizedBook;

import java.util.List;

public class OptimizedBookListAdapter extends ArrayAdapter<OptimizedBook> {

    BookscanDownloadManager downloadManager;

    public OptimizedBookListAdapter(Context context, List<OptimizedBook> books, BookscanDownloadManager downloadManager) {
        super(context, R.layout.optimized_book_list_item, R.id.book_title, books);
        this.downloadManager = downloadManager;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OptimizedBook book = getItem(position);
        View view = super.getView(position, convertView, parent);

        TextView bookTitleView = (TextView) view.findViewById(R.id.book_title);
        bookTitleView.setText(book.getFilename());

        Button downloadButton = (Button) view.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.download(book);
            }
        });

        return view;
    }
}
