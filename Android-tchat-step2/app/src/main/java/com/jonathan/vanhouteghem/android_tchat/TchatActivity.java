package com.jonathan.vanhouteghem.android_tchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class TchatActivity extends AppCompatActivity {

    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchat);

        // Récupération de la variable de test Login
        login = this.getIntent().getExtras().getString("login"); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        Toast.makeText(this, "Login récupéré : " + login, Toast.LENGTH_SHORT).show();

    }
}
