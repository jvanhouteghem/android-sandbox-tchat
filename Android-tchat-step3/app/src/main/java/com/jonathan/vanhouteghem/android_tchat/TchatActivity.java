package com.jonathan.vanhouteghem.android_tchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class TchatActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchat);

        // Récupération de la variable de test Login
        token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        Toast.makeText(this, "Token récupéré : " + token, Toast.LENGTH_SHORT).show();

    }
}
