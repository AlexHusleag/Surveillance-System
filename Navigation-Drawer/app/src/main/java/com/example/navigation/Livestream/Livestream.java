package com.example.navigation.Livestream;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.navigation.R;

public class Livestream extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestream);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Livestream");
        }

        webView = (WebView) findViewById(R.id.webview);
//        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://192.168.192.235:8000");
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onBackPressed() {
        webView.destroy();
        super.onBackPressed();
    }

}