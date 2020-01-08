package com.example.remote_surveillance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        ipAddress = findViewById(R.id.ipAddress);
        portAddress = findViewById(R.id.portNumber);


        btnUp.setOnClickListener(new View.OnClickListener(){
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

        btnDown.setOnClickListener(new View.OnClickListener(){
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

    public void getIpAndPort(){
        wifiModuleIp = ipAddress.getText().toString();
        Log.d(TAG, "getIpAndPort: " + wifiModuleIp);
        wifiModulePort = Integer.valueOf(String.valueOf(portAddress.getText()));
        Log.d(TAG, "getIpAndPort: " + wifiModulePort);
    }


    public class SendServoCommand extends AsyncTask<Void, Void, Void>{

        Socket socket;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket = new Socket(MainActivity.wifiModuleIp, MainActivity.wifiModulePort);
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(CMD.trim().replaceAll("\n",""));
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
}
