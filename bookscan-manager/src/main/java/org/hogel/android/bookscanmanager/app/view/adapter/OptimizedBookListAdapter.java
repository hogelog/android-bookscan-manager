package org.hogel.android.bookscanmanager.app.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.bookscan.model.OptimizedBook;

import java.util.List;

public class OptimizedBookListAdapter extends ArrayAdapter<OptimizedBook> {
    public OptimizedBookListAdapter(Context context, List<OptimizedBook> books) {
        super(context, R.layout.optimized_book_list_item, R.id.book_title, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OptimizedBook book = getItem(position);
        View view = super.getView(position, convertView, parent);

        TextView bookTitleView = (TextView) view.findViewById(R.id.book_title);
        bookTitleView.setText(book.getFilename());

        return view;
    }
}
