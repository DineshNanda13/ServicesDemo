package com.meterstoinches.servicesdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Intent serviceIntent;
    //background means it doesn't have a UI, it does not mean it(service)runs on a seperate thread
    //if doing long running task in service and doesn't make sure that it runs on a seperate thread
    //then you will get application error
    //activity is a visible part and service is an invisible part in the background
    private MyService myService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText_ID);

        Log.d("Service Demo ","MainActivity thread id: "+
                Thread.currentThread().getId());
    }

    public void startServiveClicked(View view) {
        serviceIntent = new Intent(getApplicationContext(),MyService.class);//declare in manifest
        startService(serviceIntent);
    }
    public void stopServiceClicked(View view){
        stopService(serviceIntent);//rarely used to explicitly stop the service otherwise stopSelf()
    }
    public void bindServiceClicked(View view){
        if(serviceConnection == null){
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    MyService.MyServiceBinder myServiceBinder = (MyService.MyServiceBinder) service;
                    myService = myServiceBinder.getService();
                    isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isServiceBound = false;
                }
            };
        }
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public void unbindServiceClicked(View view){
        if(isServiceBound){
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
    public void getRandomNumberClicked(View view){
        if(isServiceBound){
            editText.setText("Random Number: "+myService.getRandomNumber());
        }else {
            editText.setText("Service not bound");
        }
    }
}
