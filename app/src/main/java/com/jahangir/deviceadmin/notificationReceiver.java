package com.jahangir.deviceadmin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class notificationReceiver extends BroadcastReceiver{
    Context context;

    private String TAG = notificationReceiver.class.getSimpleName();

    public notificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.context = context;

            Bundle bundle = intent.getExtras();

            SmsMessage[] msgs = null;

            String message = "";


            if (bundle != null) {
                // Retrieve the Binary SMS data
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
//                Log.d("hello", pdus.length + "");

                // For every SMS message received (although multipart is not supported with binary)
                msgs[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);


                // Log.d("hello", msgs[0].getUserData() + "");
                String str = "";
                byte[] data = null;

                data = msgs[0].getUserData();


                for (int index = 0; index < data.length; ++index) {
                    str += Character.toString((char) data[index]);


                }
//                Log.d("hello",str+"");


                // Return the User Data section minus the
                // User Data Header (UDH) (if there is any UDH at all)
                // Return the User Data section minus the
                // User Data Header (UDH) (if there is any UDH at all)
                // message = msgs[0].getMessageBody();
                // Log.d("helloi", message);
                Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
                String[] attributes = str.split("-");
                double lat = Double.parseDouble(attributes[0]);
                double longi = Double.parseDouble(attributes[1]);
                double speed = Double.parseDouble(attributes[2]);
                String uri = "";
                uri = uri + "https://maps.google.com/maps?q=loc:" + lat + "," + longi + "(" + "Unknown Location" + ", Speed:" + speed + ")";
                Intent intentimp = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intentimp.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentimp);
//                Log.d("locattt", "" + str);


            }
        } catch (Exception e) {
            Toast.makeText(context, "Couldn't Track", Toast.LENGTH_SHORT).show();

        }
    }


}
