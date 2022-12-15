package com.example.finaltext;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class Music extends Service {
    public Music() {
    }

    public int i = 0;

    public MediaPlayer myplayer;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return new binder();
    }

    public class binder extends Binder {
        public void Pause(){
            Music.this.Pause();
        }
        public void star(){
            myplayer.start();
        }
        public void stop(){
            myplayer.stop();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myplayer = MediaPlayer.create(this,R.raw.wddmx);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i= intent.getIntExtra("status",0);
        myplayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        myplayer.stop();
        super.onDestroy();

    }

    public void Pause(){
        //int i = intent.getIntExtra("status",0);
        switch (i){
            case 0:
                myplayer.pause();
                i=1;
                break;
            case 1:
                myplayer.start();
                i=0;
                break;

        }

        Intent intent1 = new Intent("music");
        intent1.putExtra("status",i);
        sendBroadcast(intent1);
    }
}
