package com.example.soundsensev1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginTabFragment extends Fragment {

    EditText emailET;
    EditText passwordET;
    TextView forgotPasswordTV;
    Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.login_tab_fragment, container, false);

        emailET = root.findViewById(R.id.emailSignInEditText);
        passwordET = root.findViewById(R.id.passwordSignInEditText);
        forgotPasswordTV = root.findViewById(R.id.forgotPasswordSignInTextView);
        loginButton = root.findViewById(R.id.loginSignInButton);

        emailET.setTranslationX(800);
        passwordET.setTranslationX(800);
        forgotPasswordTV.setTranslationX(800);
        loginButton.setTranslationX(800);

        emailET.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        passwordET.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgotPasswordTV.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        loginButton.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        return root;
    }

}
