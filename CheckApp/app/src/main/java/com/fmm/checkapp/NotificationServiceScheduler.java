package com.fmm.checkapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fmm.checkapp.Model.Event;

import java.util.Date;

import static com.fmm.checkapp.HomeActivity.CURRENT_EVENT;

public class NotificationServiceScheduler extends JobService {

    public static boolean jobCancelled =false;
    private  String minH;


    @Override
    public boolean onStartJob(JobParameters params) {

        keyWordsUpdate(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        jobCancelled=true;
        return true;
    }

    private void keyWordsUpdate(final JobParameters params){

        final Event eventsHere=CURRENT_EVENT;
        new Thread(new Runnable() {

            @Override
            public void run() {
                Date hora = new Date();
                minH = Integer.toString(hora.getMinutes());
                minH = (hora.getMinutes()>=0&&hora.getMinutes()<=9 ? "0"+minH:minH);

                final Handler handle = new Handler();
                while (!jobCancelled) {

                    Log.d("AQUI", "Na Thread.....");
                    synchronized (this) {
                        try {
                            wait(500);
                            handle.post(new Runnable() {
                                @Override
                                public void run() {
                                    Date time = new Date();
                                    String hora = Integer.toString(time.getHours());
                                    String min = Integer.toString(time.getMinutes());
                                    min = (time.getMinutes()>=0&&time.getMinutes()<=9 ? "0"+min:min);
                                    hora = (time.getHours()>=0&&time.getHours()<=9 ? "0"+hora:hora);
                                    String fullHour = hora + "h" + min + "min";
                                    Log.d("AQUI", "Hora atual: "+fullHour);
                                    if (!minH.equals(min)) {

                                        Log.d("AQUI", "Mudou o Minuto, novo horário: "+fullHour);

                                        Log.d("AQUI", "Verificando se lança a key......");
                                        givePop(fullHour,  eventsHere);

                                        minH = min;

                                    }


                                }
                            });
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
                jobFinished(params,false);
            }
        }).start();
    }

    private void givePop(String fullHour, Event events)  {


        if (fullHour.equals(events.getKeys().get(0).getTime())||fullHour.equals(events.getKeys().get(1).getTime())||fullHour.equals(events.getKeys().get(2).getTime())) {

            displayNotification("Frequência FMM","Olá, como está a aula? Você deve inserir a palavra-passe para notificar o professor que você está acompanhando a aula!!! ABRA O APLICATIVO!!!");
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
                new NotificationCompat.Builder(getApplicationContext(), "simplified_coding")
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
}
