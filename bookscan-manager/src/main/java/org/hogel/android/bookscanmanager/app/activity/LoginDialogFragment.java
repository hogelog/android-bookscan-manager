package org.hogel.android.bookscanmanager.app.activity;

import com.google.inject.Inject;

import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.util.Preferences;
import org.hogel.android.bookscanmanager.app.util.Toasts;
import org.hogel.bookscan.AsyncBookscanClient;
import org.hogel.bookscan.listener.LoginListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;

public class LoginDialogFragment extends RoboDialogFragment implements View.OnClickListener {
    private static final Logger LOG = LoggerFactory.getLogger(LoginDialogFragment.class);

    @Inject
    private Preferences preferences;

    @InjectView(R.id.loginButton)
    private Button loginButton;
    @InjectView(R.id.loginMailEdit)
    private TextView loginMailEdit;
    @InjectView(R.id.loginPassEdit)
    private TextView loginPassEdit;

    @Inject
    AsyncBookscanClient client;

    @Inject
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.login_title);
        return inflater.inflate(R.layout.fragment_login_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton.setOnClickListener(this);
        loginMailEdit.setText(preferences.getLoginMail());
        loginPassEdit.setText(preferences.getLoginPass());
    }

    @Override
    public void onClick(View v) {
        final String loginMail = loginMailEdit.getText().toString();
        final String loginPass = loginPassEdit.getText().toString();
        client.login(loginMail, loginPass, new LoginListener() {
            @Override
            public void onSuccess() {
                Toasts.show(getActivity(), R.string.action_login_success);
                preferences.putLoginPreference(loginMail, loginPass);
                preferences.putCookies(client.getCookies());
                dismiss();
            }

            @Override
            public void onError(Exception e) {
                Toasts.show(getActivity(), R.string.action_login_fail);
            }
        });
    }

    public void show() {
        show(fragmentManager, "login");
    }
}
