# android-tchat

Step 2 : Create TchatActivity
---

In this step we will create a new activity called TchatActivity, update the first activity to login to a webservice and if the login success 
we want to go to TchatActivity.

A. Create TchatActivity and activity_tchat.xml
---

B. Update activity_tchat.xml
---
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="com.jonathan.vanhouteghem.android_tchat.TchatActivity">

    <EditText
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/tchaTextEditorMessageToSend"
        android:layout_alignParentBottom="true"
        android:layout_alignParentLeft="true"
        android:layout_alignParentStart="true"
        android:layout_toLeftOf="@+id/tchatButtonSend"
        android:layout_toStartOf="@+id/tchatButtonSend" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Send"
        android:id="@+id/tchatButtonSend"
        android:layout_alignParentBottom="true"
        android:layout_alignParentRight="true"
        android:layout_alignParentEnd="true" />

    <ListView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/listView"
        android:layout_alignParentTop="true"
        android:layout_alignParentLeft="true"
        android:layout_alignParentStart="true"
        android:layout_above="@+id/tchaTextEditorMessageToSend" />
</RelativeLayout>
```
C. Link SignInActivity with TchatActivity
---
a. Create variables 
```
// Variables
EditText login;
EditText pass;
Button signIn;
```

b. Initialize variables in onCreate() after setContentView(R.layout.activity_sign_in);
```
// Initialisation des variables
login =(EditText)findViewById(R.id.signInLoginTextEditor);
pass =(EditText)findViewById(R.id.signInPassTextEditor);
signIn = (Button)findViewById(R.id.signInButtonSignIn);
``` 

c. Use setOnClickListener to add event when click on signIn button
```
signIn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(SignInActivity.this, "Login : " + login.getText() + " Pass : " + pass.getText(), Toast.LENGTH_SHORT).show();
    };
});
```
d. Use intent to go to TchatActivity and pass login
```
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
            };
        });
```

D. Pass input from Signin to TchatActivity
---
a. use getExtra to get the login in TchatActivity
```
public class TchatActivity extends AppCompatActivity {

    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchat);

        // Récupération de la variable de test Login
        login = this.getIntent().getExtras().getString("login"); 
        Toast.makeText(this, "Login récupéré : " + login, Toast.LENGTH_SHORT).show();

    }
}
```