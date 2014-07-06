package org.hogel.android.bookscanmanager.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import com.google.inject.Inject;
import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.fragment.LoginDialogFragment;
import org.hogel.android.bookscanmanager.app.view.adapter.ActionBarTabAdapter;
import org.hogel.android.bookscanmanager.app.view.adapter.BookListTabPagerAdapter;
import org.hogel.android.bookscanmanager.app.view.adapter.ViewPagerPageChangeAdapter;
import roboguice.inject.InjectView;


public class BookListActivity extends RoboActionBarActivity {
    private static final int[] BOOK_LIST_TAB_NAMES = new int[] {
        R.string.book_list_tab_books,
        R.string.book_list_tab_optimized_books,
    };

    @Inject
    private FragmentManager fragmentManager;

    @Inject
    private LoginDialogFragment loginDialogFragment;

    @Inject
    private BookListTabPagerAdapter bookListTabPagerAdapter;

    @InjectView(R.id.pager)
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setSupportProgressBarIndeterminate(true);

        setContentView(R.layout.activity_book_list);

        setupViewPager();
    }

    private void setupViewPager() {
        viewPager.setAdapter(bookListTabPagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBarTabAdapter() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        };
        for (int tabName : BOOK_LIST_TAB_NAMES) {
            actionBar.addTab(actionBar.newTab().setText(tabName).setTabListener(tabListener));
        }

        viewPager.setOnPageChangeListener(new ViewPagerPageChangeAdapter() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_book_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                loginDialogFragment.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
