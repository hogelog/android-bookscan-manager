package org.hogel.android.bookscanmanager.app.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.google.inject.Inject;
import org.hogel.android.bookscanmanager.app.fragment.BookListFragment;
import org.hogel.android.bookscanmanager.app.fragment.OptimizedBookListFragment;

public class BookListTabPagerAdapter extends FragmentPagerAdapter {
    private static final int POSITION_BOOKS = 0;
    private static final int POSITION_OPTIMIZED_BOOKS = 1;

    private static final Class[] FRAGMENT_CLASSES = new Class[]{
        BookListFragment.class,
        OptimizedBookListFragment.class,
    };

    @Inject
    public BookListTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case POSITION_BOOKS:
                return new BookListFragment();
            case POSITION_OPTIMIZED_BOOKS:
                return new OptimizedBookListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_CLASSES.length;
    }
}
