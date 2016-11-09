# android-tchat

Step 4 : Load message list
---

In this step we will load a list of messages

A. Create class DateHelper
---
```
public class DateHelper {

    private static String format = "HH:mm:ss";
    private static SimpleDateFormat formatter = null;

    /**
     * create formatted date from timestamp.
     * @param timestamp
     * @return
     */
    public static String getFormattedDate(long timestamp) throws ParseException {
        if(formatter == null){
            formatter = new SimpleDateFormat(format);
        }
        return formatter.format(new Date(timestamp)).toString();
    }
}
```

B. Create class Message 
---
```
public class Message {

    public Message(String username, String message, long date) {
        this.username = username;
        this.msg = message;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getMsg() {
        return msg;
    }

    String username;
    String msg;

    public long getDate() {
        return date;
    }

    long date;
}

```

C. Create MessagesAdapter
---
```
public class MessagesAdapter extends BaseAdapter {

    private final Context context;

    public MessagesAdapter(Context ctx){
        this.context = ctx;
    }

    List<Message> messages = new LinkedList<>();

    public void addMessage(List<Message> messages){
        this.messages = messages;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(messages == null){
            return 0;
        }
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // On utilise le ViewHolder pour améliorer les performances et ne pas recharger les mêmes messages quand scroll en bas ou en haut
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity)context).getLayoutInflater(); // layout inflater est un objet qui permet d'instancier une vue
            convertView = inflater.inflate(R.layout.item_message, parent, false); // R c'est le résultat quand compile.
            vh = new ViewHolder();
            vh.username = (TextView) convertView.findViewById(R.id.msg_user);
            vh.message = (TextView) convertView.findViewById(R.id.msg_message);
            vh.date = (TextView) convertView.findViewById(R.id.msg_date);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.username.setText(messages.get(position).getUsername());
        vh.message.setText(messages.get(position).getMsg());
        try {
            vh.date.setText(DateHelper.getFormattedDate(messages.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder{
        TextView username;
        TextView message;
        TextView date;
    }
}

```

D. Create item_message.xml
---
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_margin="5dip">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceMedium"
        android:id="@+id/msg_user" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceLarge"
        android:id="@+id/msg_message" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceSmall"
        android:id="@+id/msg_date" />
</LinearLayout>
```

E. Update TchatActivity
---
```
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

```