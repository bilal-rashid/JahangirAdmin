package com.jahangir.deviceadmin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    CardView card_buzzer;
    CardView card_vibrate;
    CardView card_flash;
    CardView card_profile;
    Button setting_button;
    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    private static final String SMS_SENT = "SMS_SENT";
    sendreceiver sendReceiver = new sendreceiver();
    deliveredreceiver deliveredReciever = new deliveredreceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        card_buzzer = (CardView) findViewById(R.id.card_buzzer);
        card_vibrate = (CardView) findViewById(R.id.card_vibrate);
        card_flash = (CardView) findViewById(R.id.card_flash);
        card_profile = (CardView) findViewById(R.id.card_profile);
        setting_button = (Button) findViewById(R.id.settings);

        card_buzzer.setOnClickListener(this);
        card_vibrate.setOnClickListener(this);
        card_flash.setOnClickListener(this);
        card_profile.setOnClickListener(this);
        setting_button.setOnClickListener(this);

        card_buzzer.setOnTouchListener(this);
        card_vibrate.setOnTouchListener(this);
        card_flash.setOnTouchListener(this);
        card_profile.setOnTouchListener(this);
    }
    public  String getNumber(){

        return PreferenceManager.getDefaultSharedPreferences(this).getString("example_text", "03345505421");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.card_buzzer:
                sendSignal("buzzer");
                break;
            case R.id.card_profile:
                sendSignal("profile");
                break;
            case R.id.card_flash:
                sendSignal("flash");
                break;
            case R.id.card_vibrate:
                sendSignal("tracking");
                break;
            case R.id.settings:
                Intent i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                break;
        }

    }

    private void animate(View view, MotionEvent motionEvent, int color, int colorPressed) {
        CardView cardView = (CardView) view;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                cardView.setCardBackgroundColor(color);
                break;
            case MotionEvent.ACTION_CANCEL:
                cardView.setCardBackgroundColor(color);
                break;
            case MotionEvent.ACTION_DOWN:
                cardView.setCardBackgroundColor(colorPressed);
                break;
            case MotionEvent.ACTION_MOVE:
                cardView.setCardBackgroundColor(colorPressed);
                break;

        }
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.card_buzzer:
                animate(view, motionEvent, ContextCompat.getColor(this, R.color.card_income_color),
                        ContextCompat.getColor(this, R.color.card_income_color_pressed));
                break;
            case R.id.card_vibrate:
                animate(view, motionEvent, ContextCompat.getColor(this, R.color.card_expense_color),
                        ContextCompat.getColor(this, R.color.card_expense_color_pressed));
                break;
            case R.id.card_profile:
                animate(view, motionEvent, ContextCompat.getColor(this, R.color.card_saving_color),
                        ContextCompat.getColor(this, R.color.card_saving_color_pressed));
                break;
            case R.id.card_flash:
                animate(view, motionEvent, ContextCompat.getColor(this, R.color.card_purchase_color),
                        ContextCompat.getColor(this, R.color.card_purchase_color_pressed));
                break;
        }
        return false;
    }


    private class sendreceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String info = "Send information: ";

            switch(getResultCode())
            {
                case Activity.RESULT_OK: info += "Send Successful"; break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: info += "send failed, generic failure"; break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: info += "send failed, no service"; break;
                case SmsManager.RESULT_ERROR_NULL_PDU: info += "send failed, null pdu"; break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: info += "send failed, radio is off"; break;
            }

            Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();

        }
    }

    private class deliveredreceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String info = "Delivery information: ";

            switch(getResultCode())
            {
                case Activity.RESULT_OK: info += "delivered"; break;
                case Activity.RESULT_CANCELED: info += "not delivered"; break;
            }

            Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(sendReceiver, new IntentFilter(SMS_SENT));

        registerReceiver(deliveredReciever, new IntentFilter(SMS_DELIVERED));
    }
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(sendReceiver);
        unregisterReceiver(deliveredReciever);
    }

    public void sendSignal(String message){
        SmsManager smsManager = SmsManager.getDefault();
        registerReceiver(sendReceiver, new IntentFilter(SMS_SENT));
        short port = 6635;

        registerReceiver(deliveredReciever, new IntentFilter(SMS_DELIVERED));
        PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        smsManager.sendDataMessage(getNumber(), null, port, message.getBytes(), piSend, piDelivered);
//        Log.d("TAAAG",""+getNumber());
    }
}
