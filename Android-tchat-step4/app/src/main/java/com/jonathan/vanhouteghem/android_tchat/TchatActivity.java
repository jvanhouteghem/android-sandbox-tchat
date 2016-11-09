package com.jonathan.vanhouteghem.android_tchat;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jonathan.vanhouteghem.android_tchat.assets.HttpResult;
import com.jonathan.vanhouteghem.android_tchat.assets.JsonParser;
import com.jonathan.vanhouteghem.android_tchat.assets.Message;
import com.jonathan.vanhouteghem.android_tchat.assets.MessagesAdapter;
import com.jonathan.vanhouteghem.android_tchat.assets.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TchatActivity extends AppCompatActivity {

    private String token;
    ListView listView;
    MessagesAdapter adapter;
    private GetMessagesAsyncTask messagesAsyncTask;

    Timer timer;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchat);

        // Récupération de la variable de test Login
        token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        Toast.makeText(this, "Token récupéré : " + token, Toast.LENGTH_SHORT).show();

        // Affichage des messages
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MessagesAdapter(this);
        listView.setAdapter(adapter);

    }

    public void refresh() {
        // optimisation pour ne pas lancer si est déjà lancé (utile si bcp de messages)
        if (messagesAsyncTask == null || messagesAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            new GetMessagesAsyncTask(this).execute();
            //  swipeRefreshLayout.setRefreshing(true);
        }
    }

    // Obligatoire, provoque le refresh()
    @Override
    public void onResume() {
        super.onResume();
        //start polling
        timer = new Timer();
        // first start in 500 ms, then update every TIME_POLLING
        try {
            timer.schedule(task, 500, 5000);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Tchat timertask error", e);
        }
    }

    // Obligatoire
    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        //messagesAsyncTask.cancel(true); // Coupe la requête quand pause (propre)
    }

    /**
     * AsyncTask for sign-in
     */
    protected class GetMessagesAsyncTask extends AsyncTask<String, Void, List<Message>> {

        Context context;

        public GetMessagesAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected List<Message> doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return null;
            }

            InputStream inputStream = null;

            try {

                HttpResult result = NetworkHelper.doGet("http://cesi.cleverapps.io/messages", null, token);

                if (result.code == 200) {
                    // Convert the InputStream into a string
                    return JsonParser.getMessages(result.json);
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
        public void onPostExecute(final List<Message> msgs) {
            int nb = 0;
            if (msgs != null) {
                nb = msgs.size();
            }
            Toast.makeText(TchatActivity.this, "loaded nb messages: " + nb, Toast.LENGTH_LONG).show();
            adapter.addMessage(msgs);
            //swipe.setRefreshing(false);
        }

    }

    /**
     * AsyncTask for sign-in
     */
    // Nb INTERDICTION de mettre un toast dedans sous peine de faire buger l'affichage de la liste
    /*public class SendMessageAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;

            try {
                // Envoit des paramètres stockés dans une map
                Map<String, String> p = new HashMap<>();
                p.put("message", params[0]);
                HttpResult result = NetworkHelper.doPost(TchatActivity.this.getString(R.string.url_msg), p, token);

                return result.code;

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
        public void onPostExecute(Integer status) {
            if (status != 200) {
                Toast.makeText(TchatActivity.this, "erreur", Toast.LENGTH_SHORT).show();
            } else {
                //DO nothing
            }
        }
    }*/
}
