package org.hogel.android.bookscanmanager.app.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.bookscan.model.OptimizingBook;

import java.util.List;

public class OptimizingBookListAdapter extends ArrayAdapter<OptimizingBook> {
    public OptimizingBookListAdapter(Context context, List<OptimizingBook> books) {
        super(context, R.layout.optimizing_book_list_item, R.id.book_title, books);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        OptimizingBook book = getItem(position);
        View view = super.getView(position, convertView, parent);

        TextView bookTitleView = (TextView) view.findViewById(R.id.book_title);
        bookTitleView.setText(book.getFilename());

        TextView optimizeType = (TextView) view.findViewById(R.id.optimize_type);
        optimizeType.setText(book.getType());

        TextView optimizeStatus = (TextView) view.findViewById(R.id.optimize_status);
        optimizeStatus.setText(book.getStatus());

        TextView optimizeTime = (TextView) view.findViewById(R.id.optimize_time);
        optimizeTime.setText(book.getRequestedAt());

        return view;
    }
}
