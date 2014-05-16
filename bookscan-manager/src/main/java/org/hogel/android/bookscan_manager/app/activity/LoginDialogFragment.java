package org.hogel.android.bookscan_manager.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.hogel.android.bookscan_manager.app.R;
import org.hogel.android.bookscan_manager.app.bookscan.BookscanClient;
import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import javax.inject.Inject;

public class LoginDialogFragment extends RoboDialogFragment implements View.OnClickListener {
    @Inject
    private SharedPreferences preferences;
    @InjectResource(R.string.prefs_login_mail)
    private String prefLoginMail;
    @InjectResource(R.string.prefs_login_pass)
    private String prefLoginPass;

    @InjectView(R.id.loginButton)
    private Button loginButton;
    @InjectView(R.id.loginMailEdit)
    private TextView loginMailEdit;
    @InjectView(R.id.loginPassEdit)
    private TextView loginPassEdit;

    @Inject
    BookscanClient bookscanClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.login_title);
        View view = inflater.inflate(R.layout.fragment_login_dialog, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton.setOnClickListener(this);
        loginMailEdit.setText(preferences.getString(prefLoginMail, ""));
        loginPassEdit.setText(preferences.getString(prefLoginPass, ""));
    }

    @Override
    public void onClick(View v) {
        String loginMail = loginMailEdit.getText().toString();
        String loginPass = loginPassEdit.getText().toString();
        bookscanClient.login(loginMail, loginPass);
        dismiss();
    }
}
