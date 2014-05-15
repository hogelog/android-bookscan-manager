package org.hogel.android.bookscan_manager.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import org.hogel.android.bookscan_manager.app.R;
import roboguice.inject.InjectResource;

public class BookscanPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @InjectResource(R.string.prefs_login_mail) private String prefLoginMail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setSummaries();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSummaries();
    }

    private void setSummaries() {
        EditTextPreference mailPref = (EditTextPreference) findPreference(prefLoginMail);
        mailPref.setSummary(mailPref.getText());
    }
}
