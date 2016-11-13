package com.jonathan.vanhouteghem.testwebview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_CODE_REQUEST = 0x12;
    //WebView webView;
    ImageView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final WebView webView = (WebView) findViewById(R.id.webview);
        webView = (ImageView) findViewById(R.id.webview);
        
        /*findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("javascript:hello('Jonathan')");
            }
        });*/



        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, PHOTO_CODE_REQUEST);
            }
        });

        //enable chrome://inspect for remote debug
        // Only in dev, not in prod
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        /*webView.getSettings().setJavaScriptEnabled(true);
        // Affichage de l'alert
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("file:///android_asset/www/page.html");//webview.loadUrl("http://www.google.fr");


        // Pont dans l'autre sens
        webView.addJavascriptInterface(new JSInterface(this), "Android");*/


        //webview.setWebViewClient(new MyWebClient());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PHOTO_CODE_REQUEST){
            if (resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap image = (Bitmap) extras.get("data");
                webView.setImageBitmap(image);
            } else {
                Toast.makeText(this, "Erreur sur la prise de photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class JSInterface {
        private final Context context;

        public JSInterface(Context context){
            this.context = context;
        }

        @JavascriptInterface
        public void helloJava(){
            // Seulement si pas besoin d'accès BDD, c'est un thread qui n'est pas dans le thread de l'ui
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "I've been called from JS", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /*private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebViewClient view, String url){
            if (url.startsWith("mailto:")){
                // démarrer appMail
            } else if (url.startsWith("tel:")){

            }
            return false;
        }
    }*/
}
