package com.example.findfriends;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MySmsReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String messageBody,phoneNumber;
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {
            Bundle bundle =intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();
                    Toast.makeText(context,
                                    "Message : "+messageBody+"Reçu de la part de;"+ phoneNumber,
                                    Toast.LENGTH_LONG )
                            .show();
                    if(messageBody.contains("Send me your position"))
                    {
                        // send the position
                        Intent i = new Intent(context, MyLocationService.class);
                        i.putExtra("phone",phoneNumber);
                        context.startService(i);

                    }
                    if(messageBody.contains("My position is : #")){
                        // extract the position
                        String[] parts = messageBody.split("#");
                        String latitude = parts[1];
                        String longitude = parts[2];
                        NotificationCompat.Builder myNotification =  new NotificationCompat.Builder(context,
                                "channel");
                        myNotification.setContentTitle("Position reçe");
                        myNotification.setContentText("Latitude : "+latitude+"Longitude : "+longitude);
                        myNotification.setSmallIcon(R.drawable.ic_launcher_background);
                        myNotification.setAutoCancel(true);
                        Intent i2 = new Intent(context,MapsActivity.class);
                        i2.putExtra("latitude",latitude);
                        i2.putExtra("longitude",longitude);
                        PendingIntent pi = PendingIntent.getActivity(context,1,i2,PendingIntent.FLAG_MUTABLE);
                        myNotification.setContentIntent(pi);

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);


                        NotificationChannel channel = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            channel = new NotificationChannel("channel","channel", NotificationManager.IMPORTANCE_DEFAULT);
                            managerCompat.createNotificationChannel(channel);
                        }
                        managerCompat.notify(1,myNotification.build());

                        Toast.makeText(context,
                                "Latitude : "+latitude+"Longitude : "+longitude,
                                Toast.LENGTH_LONG )
                                .show();

                    }
                }
            }
        }
    }
}