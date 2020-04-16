package com.fmm.checkapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            createNotificationChannelFIREBASE(getApplicationContext());
            displayNotification( title, body);

        }
    }
    public void displayNotification( String title, String body){


        Log.d("AQUI","Entrou pra lançar notificação......");

        // Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        // Intent intent = getIntent();
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        //stackBuilder.addParentStack(intent.getComponent());
        // stackBuilder.addNextIntent(intent);

        //  PendingIntent p = PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_NO_CREATE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "simplified_coding_2")
                        .setTicker("Frequência FMM")
                        .setSmallIcon(R.drawable.logo_main)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setVibrate(new long[]{150,300,150,300,150})
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("AQUI","Criou o Builder......");

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(getApplicationContext());
        mNotificationMgr.notify(1, mBuilder.build());

        Log.d("AQUI","Lançou a notificação......");


    }
    public static void createNotificationChannelFIREBASE(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Serviços - Frequência FMM";
            String description = "Serviços - Notificação APP";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("simplified_coding_2", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
