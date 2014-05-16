package org.hogel.android.bookscan_manager.app.activity;

import android.os.Bundle;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectFragment;

public class LoginActivity extends RoboActivity {
    @InjectFragment
    LoginDialogFragment loginDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        getFragmentManager()
//            .beginTransaction()
//            .replace(android.R.id.content, bookscanPreferenceFragment)
//            .commit();
    }
}
