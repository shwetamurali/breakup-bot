package com.example.shwet.breakupbot;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    SmsManager SMSmanager;
    String otherReply = "";
    TextView state;
    String phoneNum;
    Object[] received;
    SmsMessage[] texts;
    int stage = 0;
    String reply = "";
    int howManyReplies = 0;
    int curr;
    boolean done=false;
    String[] unknow = {"What?","What are you trying to say?","What do you mean?","I don't comprehend."};
    String[] possibleState = {"Beginning","Start Heartbreak","Finish Heartbreak","Why","Finish","Unknown Response"};
    String[] beginning = {"Sup!","What's up?","Hi!","Hey!"};
    String [] start = {"We need to talk...","I assure you, this is something I have been pondering for a while","I've been feeling different recently.","I have something I need to get off my chest."};
    String[] finish = {"I think we should stop seeing each other.","You and I are done.","We need to break up.","This relationship is over."};
    String[] why = {"I don't have strong feelings for you anymore.","I have feelings for someone else.","It's not you, it's me.","Things just aren't as magical as they were in the beginning."};
    String[] end = {"Delete my number right now.","Poof! You're single.","I'm sorry it had to end this way.","Bye. See you never"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        state = findViewById(R.id.textView2);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_SMS},123);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        BroadcastReceiver broadcastReceiver = new GetMessage();
        registerReceiver(broadcastReceiver,intentFilter);
        SMSmanager = SmsManager.getDefault();
    }
    public class GetMessage extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            otherReply="";
            Bundle bundle = intent.getExtras();
            received = (Object[]) bundle.get("pdus");
            texts = new SmsMessage[received.length];
            for(int i = 0;i<texts.length;i++) {
                texts[i] = SmsMessage.createFromPdu((byte[])received[i],bundle.getString("format"));
                otherReply+=texts[i].getMessageBody();
            }
            phoneNum = texts[0].getOriginatingAddress();
            otherReply = otherReply.toLowerCase();
            if(stage==0) {
                if (otherReply.contains("hey") || otherReply.contains("hi") || otherReply.contains("hello")||otherReply.contains("what's up")) {
                    stage = 1;
                } else {
                    curr=stage;
                    stage = 6;
                }
            }
            else if(stage==1) {
                if(otherReply.contains("nothing")||otherReply.contains("how are you")||otherReply.contains("what's going on")||otherReply.contains("sup")||otherReply.contains("what's up")) {
                    stage=2;
                }
                else {
                    curr=stage;
                    stage=6;
                }
            }
            else if(stage==2) {
                if(otherReply.contains("what")||otherReply.contains("what do you mean")||otherReply.contains("what are you talking about")||otherReply.contains("meaning")) {
                    stage =3;
                }
                else {
                    curr=stage;
                    stage=6;
                }
            }
            else if(stage==3) {
                if(otherReply.contains("why")||otherReply.contains("omg")||otherReply.contains("where did this come from")) {

                    stage =4;
                }
                else {
                    curr=stage;
                    stage=6;
                }
            }
            else if(stage==4) {
                if(otherReply.contains("who")||otherReply.contains("don't do this")||otherReply.contains("i still love you")||otherReply.contains("omg")) {
                    stage =5;
                }
                else {
                    curr=stage;
                    stage=6;
                }
            }
            else if (done) {
                reply="This is over, please stop texting me.";
            }
            state.setText("Stage "+stage + ": "+possibleState[stage-1]);
            int rand = (int)(Math.random()*beginning.length);
            if(stage==1)
                reply = beginning[rand];
            if(stage==2)
                reply=start[rand];
            if(stage==3)
                reply = finish[rand];
            if(stage==4)
                reply = why[rand];
            if(stage==5 &&!done) {
                reply = end[rand];
                done=true;
            }
            if(stage==6) {
                reply = unknow[rand];
                stage=curr;
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SMSmanager.sendTextMessage(phoneNum, null, reply, null, null);
                }
            },4000);


        }
    }
}
