package com.meterstoinches.servicesdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class MyService extends Service {
    private int mRandomNumber;
    private boolean mIsRandomGeneratorOn;

    private final int min = 0;
    private final int max = 100;

    //OR implements IBinder
    class MyServiceBinder extends Binder {
        public MyService getService(){
            return MyService.this; //returns Myservice instance
        }
    }

    private IBinder iBinder = new MyServiceBinder();//created instance of IBinder

    //implementing onBind method so that it returns IBinder interface for that
    //we created a class that implements IBinder or extends Binder
    public IBinder onBind(Intent intent) {
        Log.d("ServiceDemo","In OnBind");
        return iBinder;//first it was null
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRandomNumberGenerator();
        Log.d("Service Demo ","Service destroyed" );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service Demo ","In onStartCommand, thread id: "+
                Thread.currentThread().getId());
        mIsRandomGeneratorOn = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRandomNumberGenerator();//By default service run on a main thread invisibly
                //in background
            }//we dont want to block UI to get app error that is why created a seperate thread
        }).start();
        //stopSelf();
        return START_STICKY;
    }
    private void startRandomNumberGenerator(){
        while (mIsRandomGeneratorOn){
            try {
                Thread.sleep(2000);
                if (mIsRandomGeneratorOn){
                    mRandomNumber = new Random().nextInt(max)+min;
                    Log.d("Service demo ","Thread ID: "+
                            Thread.currentThread().getId()+", Random Number: "+mRandomNumber);
                }
            }catch (InterruptedException e){
                Log.d("Service demo","Thread Interrupted");
            }
        }
    }
    private void stopRandomNumberGenerator(){
        mIsRandomGeneratorOn = false;
    }
    public int getRandomNumber(){
        return mRandomNumber;
    }

}

/*
                      AUTO-RESTART   INTENT   (AT KILL TIME)                                        EG             START/STOP
START_STICKY             YES         NULL (NO NEED TO REMEMBER THE STATE)                           MUSIC SERVICE (EXPLICITLY)
STOP_STICKY              NO          WITH INTENT WHEN STARTED(PERIODICALLY RUNNING AND SELF STOPING)ALARM SERVICE
START_REDELIVER_INTENT   YES         INTENT (EXPLICITLY AND RESTART FROM PREVIOUS STATE)            FILE DOWNLOAD



a service that is providing data to an activity or another service is called bound service
or An activity that binds to service to get status update

bound service is of two types: local binding & remote binding

if a service is providing the info to an activity or another service that is part of the app ,
is local binding

if a service or a component that binds to a service is not part of the same app is called remote
binding

LOCAL BINDING IS IMPLEMENTED USING IBinder
REMOTE BINDING IS IMPLEMENTED USING MESSENGER & AIDL(Android interface definition language)

messenger is a queued concept(suited for non multi thread scenario)
AIDL Complicated and suited for multi threaded environment
*/
