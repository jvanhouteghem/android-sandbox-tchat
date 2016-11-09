package com.jonathan.vanhouteghem.android_tchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    // Variables
    EditText login;
    EditText pass;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialisation des variables
        login =(EditText)findViewById(R.id.signInLoginTextEditor);
        pass =(EditText)findViewById(R.id.signInPassTextEditor);
        signIn = (Button)findViewById(R.id.signInButtonSignIn);

        // Add event when click on signIn button
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SignInActivity.this, "Login : " + login.getText() + " Pass : " + pass.getText(), Toast.LENGTH_SHORT).show();
                // Link
                Intent intent = new Intent(SignInActivity.this, TchatActivity.class);
                // Pass variables
                intent.putExtra("login", login.getText().toString());
                // Go to the new activity
                startActivity(intent);

            }

            ;
        });

    }
}
