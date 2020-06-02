package com.example.navigation.Livestream;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.navigation.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Livestream extends AppCompatActivity {

    private WebView webView;
    private LinearLayout moveServoLayout;

    // UI Elements
    Button btnUp;
    Button btnDown;
    EditText ipAddress;
    EditText portAddress;

    Socket myAppSocket = null;
    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;
    public static String CMD = "0";
    private static final String TAG = "MainActivity";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestream);

        initializeVariables();
        initializeLayout();
        moveServo();
    }


    public void initializeVariables() {
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        ipAddress = findViewById(R.id.ipAddress);
        portAddress = findViewById(R.id.portNumber);
        moveServoLayout = findViewById(R.id.moveServo);
    }

    public void initializeLayout() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Livestream");
        }

        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("http://192.168.192.235:8000");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
//                moveServoLayout.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        });
    }


    public void getIpAndPort() {
        wifiModuleIp = ipAddress.getText().toString();
        Log.d(TAG, "getIpAndPort: " + wifiModuleIp);
        wifiModulePort = Integer.valueOf(String.valueOf(portAddress.getText()));
        Log.d(TAG, "getIpAndPort: " + wifiModulePort);
    }

    public void moveServo() {
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Up button");
                getIpAndPort();
                CMD = "UP";
                System.out.println("Button UP pressed, CMD is " + CMD);
                SendServoCommand sendServoCommand = new SendServoCommand();
                sendServoCommand.execute();
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Down button");
                getIpAndPort();
                CMD = "DOWN";
                System.out.println("Button DOWN pressed, CMD is " + CMD);
                SendServoCommand sendServoCommand = new SendServoCommand();
                sendServoCommand.execute();
            }
        });
    }

    public class SendServoCommand extends AsyncTask<Void, Void, Void> {

        Socket socket;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket = new Socket(Livestream.wifiModuleIp, Livestream.wifiModulePort);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(CMD.trim().replaceAll("\n", ""));
                pw.flush();
                pw.close();
                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        webView.destroy();
        super.onBackPressed();
    }

}