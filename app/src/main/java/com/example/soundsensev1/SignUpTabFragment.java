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

public class SignUpTabFragment extends Fragment {

    EditText emailET;
    EditText passwordET;
    EditText nameET;
    Button signUpButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.signup_tab_fragment, container, false);

        nameET = root.findViewById(R.id.nameSignUpEditText);
        emailET = root.findViewById(R.id.emailSignUpEditText);
        passwordET = root.findViewById(R.id.passwordSignUpEditText);
        signUpButton = root.findViewById(R.id.signUpButton);

        nameET.setTranslationX(800);
        emailET.setTranslationX(800);
        passwordET.setTranslationX(800);
        signUpButton.setTranslationX(800);

        nameET.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        emailET.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        passwordET.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        signUpButton.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();

        return root;
    }
}
