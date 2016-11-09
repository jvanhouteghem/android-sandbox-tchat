package com.jonathan.vanhouteghem.android_tchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jonathan.vanhouteghem.android_tchat.assets.HttpResult;
import com.jonathan.vanhouteghem.android_tchat.assets.JsonParser;
import com.jonathan.vanhouteghem.android_tchat.assets.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    // Variables
    EditText login;
    EditText pass;
    Button signIn;
    ProgressDialog progressDialog;

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
                if (login.getText().toString().isEmpty()) {
                    login.setError("Merci de remplir le champs");
                    Toast.makeText(SignInActivity.this, "champs vide", Toast.LENGTH_SHORT).show();
                }else{
                    displayLoader(true);
                    Toast.makeText(SignInActivity.this, login.getText().toString() + " " + pass.getText().toString(), Toast.LENGTH_SHORT).show();
                    new SigninAsyncTask(v.getContext()).execute(login.getText().toString(), pass.getText().toString());
                }

            }

            ;
        });

    }

    private void displayLoader(boolean toDisplay){
        if(toDisplay){
            progressDialog = new ProgressDialog(SignInActivity.this);
            progressDialog.setTitle("Chargement");
            progressDialog.setMessage("Envoi du hello world");
            progressDialog.show();
        }else{
            if(progressDialog !=null && progressDialog.isShowing()){
                progressDialog.cancel();
            }else{
                Toast.makeText(this, "pg inexistante", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SigninAsyncTask extends AsyncTask<String, Void, String> {

        Context context;

        public SigninAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                return null;
            }

            // Un stream pour récevoir la réponse
            InputStream inputStream = null;

            try {

                Map<String, String> p = new HashMap<>();
                p.put("username", params[0]);
                p.put("pwd", params[1]);

                HttpResult result = NetworkHelper.doPost(context.getString(R.string.url_signin), p, null);

                if(result.code == 200) {
                    // Convert the InputStream into a string
                    return JsonParser.getToken(result.json);
                }
                return null;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage());
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("NetworkHelper", e.getMessage());
                    }
                }
            }
        }

        @Override
        public void onPostExecute(final String token){
            displayLoader(false);
            if(token != null){
                Intent i = new Intent(context, TchatActivity.class);
                i.putExtra(Constants.INTENT_TOKEN, token);
                startActivity(i);
                /*
                // Link
                /*Intent intent = new Intent(SignInActivity.this, TchatActivity.class);
                // Pass variables
                intent.putExtra("login", login.getText().toString());
                // Go to the new activity
                startActivity(intent);
                */
            } else {
                /*Snackbar.make(v, context.getString(R.string.error_login), Snackbar.LENGTH_LONG).setAction("btn", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    }
                }).show();*/
            }
        }
    }


}
