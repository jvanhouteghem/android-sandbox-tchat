# android-tchat

Step 2 : Create TchatActivity
---

In this step we will update the first activity to login to a webservice. Then if the login success we will be send to TchatActivity.

A. Create Constant class
---
```
public class Constants {

    public static final String TAG = "TchatApp";

    public static final String INTENT_TOKEN = "TchatApp";
    
}
```

B. Create clas HttpResult
---
```
public class HttpResult {

    public int code;
    public String json;

    public HttpResult(int code, String s) {
        this.code = code;
        this.json = s;
    }
}
```

C. Create class JsonParser
```
public class JsonParser {

    public static String getToken(String response) throws JSONException {
        return new JSONObject(response).optString("token");
    }
}
```

D. Create class NetworkHelper
---
```
public class NetworkHelper {


    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("HelloWorld", "Error on checking internet:", e);

        }
        //default allowed to access internet
        return true;
    }

    /**
     * Doing get request
     * @param purl
     * @param params
     * @return
     */
    public static HttpResult doGet(String purl, Map<String, String> params, String token)  {

        // Un stream pour récevoir la réponse
        InputStream inputStream = null;
        if(purl == null){
            Log.e("CESI", "Error url to call empty");
            throw new RuntimeException("Error url to call empty");
        }

        try {
            StringBuilder sb = new StringBuilder(purl);
            sb.append("?");
            sb.append(concatParams(params));

            URL url = new URL(sb.toString());
            Log.d("Calling URL", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            if(token != null) {
                //set authorization header
                conn.setRequestProperty("token", token);
            }

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("NetworkHelper", "The response code is: " + response);

            if(response != 200){
                return new HttpResult(response, null);
            } else {
                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = NetworkHelper.readIt(inputStream);
                return new HttpResult(response, contentAsString);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            }
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


    public static HttpResult doPost(String purl, Map<String, String> params, String token)  {

        // Un stream pour récevoir la réponse
        InputStream inputStream = null;
        if(purl == null){
            Log.e("CESI", "Error url to call empty");
            throw new RuntimeException("Error url to call empty");
        }

        try {
            URL url = new URL(purl);
            Log.d("Calling URL", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if(token != null) {
                //set authorization header
                conn.setRequestProperty("token", token);
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(concatParams(params));
            writer.flush();
            writer.close();
            os.close();
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("NetworkHelper", "The response code is: " + response);

            if(response != 200){
                return new HttpResult(response, null);
            } else {

                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = NetworkHelper.readIt(inputStream);
                return new HttpResult(response, contentAsString);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            }
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

    /**
     * Concat params to be send
     * @param params
     * @return
     */
    private static String concatParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size()>0){
            for (Map.Entry<String, String> entry : params.entrySet()){
                try {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Cesi", "Error adding param", e);
                }
            }
        }
        return sb.toString();
    }


    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(stream));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}

```

E. Update SignInActivity
---
Add async task to login to a webservice (login is "jo" and pass is "pass") and receive a token. 
```
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

```

F. Add permission in android.manifest
---
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.jonathan.vanhouteghem.android_tchat">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".SignInActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".TchatActivity"></activity>
    </application>

    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />

</manifest>
```

G. Display token in TchatActivity
---
```
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
``` 