package com.example.soundsensev1;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private User lUser = new User();
    private FirebaseUser fUser;
    private DatabaseReference dbReference;
    private FirebaseAuth mAuth;

    private Context context;

    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public boolean createFirebaseUser (String name, String email, String password){

        final boolean[] result = new boolean[1];

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(name,email);

                            //obtain id of newly registered user
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context,"User has been registered successfully",Toast.LENGTH_LONG).show();
                                        result[0] = true;

                                    }else{
                                        FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                        Toast.makeText(context,"Failed to register! Try again. "+e.getMessage(),Toast.LENGTH_LONG).show();
                                        result[0] = false;
                                    }

                                }
                            });
                        }
                        else {
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(context,"Failed to register! "+e.getMessage(),Toast.LENGTH_LONG).show();
                            result[0] = false;
                        }
                    }
                });

        if (result[0]) { return true; } else { return false; }
    }

}
