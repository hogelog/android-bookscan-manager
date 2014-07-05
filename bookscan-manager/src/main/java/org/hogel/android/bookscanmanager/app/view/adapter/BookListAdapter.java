package org.hogel.android.bookscanmanager.app.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.bookscan.model.Book;

import java.util.List;

public class BookListAdapter extends ArrayAdapter<Book> {
    public BookListAdapter(Context context, List<Book> books) {
        super(context, R.layout.book_list_item, R.id.book_title, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book book = getItem(position);
        View view = super.getView(position, convertView, parent);

        TextView bookTitleView = (TextView) view.findViewById(R.id.book_title);
        bookTitleView.setText(book.getFilename());

        String imageUrl = book.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageView bookThumbnailView = (ImageView) view.findViewById(R.id.book_thumbnail);
            Picasso.with(getContext()).load(imageUrl).into(bookThumbnailView);
        }

        return view;
    }
}
