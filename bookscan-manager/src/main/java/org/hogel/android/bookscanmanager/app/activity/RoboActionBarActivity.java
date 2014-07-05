package org.hogel.android.bookscanmanager.app.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.google.inject.Key;
import roboguice.RoboGuice;
import roboguice.util.RoboContext;

import java.util.HashMap;
import java.util.Map;

public abstract class RoboActionBarActivity extends ActionBarActivity implements RoboContext {
    private Map<Key<?>,Object> scopedObjects = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoboGuice.getInjector(this).injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSupportContentChanged() {
        super.onSupportContentChanged();
        RoboGuice.getInjector(this).injectViewMembers(this);
    }

    @Override
    protected void onDestroy() {
        try {
            RoboGuice.destroyInjector(this);
        } finally {
            super.onDestroy();
        }
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}
