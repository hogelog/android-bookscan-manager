package org.hogel.android.bookscanmanager.app.fragment;

import com.google.inject.Inject;

import com.squareup.otto.Subscribe;

import org.hogel.android.bookscanmanager.app.R;
import org.hogel.android.bookscanmanager.app.event.LoginEvent;
import org.hogel.android.bookscanmanager.app.util.BusProvider;
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

    @Inject
    private AsyncBookscanClient client;

    @Inject
    private FragmentManager fragmentManager;

    @InjectView(R.id.loginButton)
    private Button loginButton;

    @InjectView(R.id.loginMailEdit)
    private TextView loginMailEdit;

    @InjectView(R.id.loginPassEdit)
    private TextView loginPassEdit;

    @InjectView(R.id.progress_layout)
    private View progressLayout;

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.register(this);
    }

    @Override
    public void onPause() {
        BusProvider.unregister(this);
        super.onPause();
    }

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
        progressLayout.setVisibility(View.VISIBLE);
        client.login(loginMail, loginPass, new LoginListener() {
            @Override
            public void onSuccess() {
                BusProvider.post(LoginEvent.success(loginMail, loginPass));
            }

            @Override
            public void onError(Exception e) {
                BusProvider.post(LoginEvent.failure());
            }
        });
    }

    @Subscribe
    public void loginSuccess(LoginEvent.Success success) {
        progressLayout.setVisibility(View.GONE);
        preferences.putLoginPreference(success.getLoginMail(), success.getLoginPass());
        preferences.putCookies(client.getCookies());
        Toasts.show(getActivity(), R.string.action_login_success);
        dismiss();
    }

    @Subscribe
    public void loginFailure(LoginEvent.Failure failure) {
        progressLayout.setVisibility(View.GONE);
        Toasts.show(getActivity(), R.string.action_login_fail);
    }

    public void show() {
        show(fragmentManager, "login");
    }
}
