package com.fmm.checkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fmm.checkapp.Model.Event;
import com.fmm.checkapp.Model.MyRecyclerViewAdapter;
import com.fmm.checkapp.firebasemodel.Events;
import com.fmm.checkapp.firebasemodel.Keys;
import com.fmm.checkapp.firebasemodel.Professores;
import com.fmm.checkapp.firebasemodel.Students;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.fmm.checkapp.LoginActivity.user;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeActivity extends Activity {

    List<Event> events;
    RecyclerView recyclerViewEvents;
    String turma;
    MyRecyclerViewAdapter eventsAdapter;
    ImageButton btInfo;
    LinearLayout msgNoEvents;
    FirebaseUser firebaseUser;
    DatabaseReference dataBase;
    String userUid;
    ImageView imgNoEvents;
    ProgressBar progressBar;
    DatabaseReference teacherBase;
    Thread th;
    String minH;
    boolean stop;
    String TAG = "HomeActivity";
    boolean appHidden,firstTime;
    final static String CHANNEL_ID="simplified_coding";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btInfo = findViewById(R.id.activity_home_bt_about_us);
        imgNoEvents = findViewById(R.id.activity_home_img_no_events);
        msgNoEvents = findViewById(R.id.msg_no_events);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userUid = firebaseUser.getUid();
        dataBase = FirebaseDatabase.getInstance().getReference();
        teacherBase = dataBase.child("professores");
        progressBar = findViewById(R.id.activity_home_progressBar);
        appHidden =false;
        firstTime=true;
        Date hora = new Date();
        minH = Integer.toString(hora.getMinutes());
        minH = (hora.getMinutes()>=0&&hora.getMinutes()<=9 ? "0"+minH:minH);

        createNotificationChannel(getApplicationContext());

        dataBase.child("salas").orderByChild(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String turma = childSnapshot.getKey();
                            user.setTurma(turma);
                            if (turma != null) {
                                user.setTurma(turma);
                            }
                        }
                        getCurrentUserEvents(user.getTurma());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        dataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getCurrentUserEvents(user.getTurma());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
            }
        });

        btInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(HomeActivity.this, "Sobre nós", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }
    //Visiveis
    @Override
    protected void onStart() {
        super.onStart();

        appHidden = false;

    }

    @Override
    protected void onResume() {
        super.onResume();

        appHidden = false;

    }
    //Oculto - Segundo Plano
    @Override
    protected void onPause() {
        super.onPause();

        appHidden = true;

    }

    @Override
    protected void onStop() {
        super.onStop();

        appHidden = true;

    }

    private void getCurrentUserEvents(final String turma) {
        teacherBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Events> firebaseEvents = new ArrayList<Events>();
                    List<Event> eventList = new ArrayList<Event>();

                    for (final DataSnapshot dados : dataSnapshot.getChildren()) {
                        Professores profs = dados.getValue(Professores.class);
                        HashMap<String, Events> events = profs.getEvents().get(turma);
                        if (events != null) {
                            int j = 0;
                            Date time = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                            String fullDate = sdf.format(time);
                            for (final Map.Entry<String, Events> m : events.entrySet()) {
                                String checkin = "", checkout = "";
                                if (!m.getKey().equals("evento0")&&m.getValue().getDate().equals(fullDate)) {
                                    firebaseEvents.add(m.getValue());
                                    HashMap<String, Keys> keys = m.getValue().getKeys();
                                    final List<Keys> keysTemp = new ArrayList<Keys>();
                                    if (keys != null) {
                                        int i = 0;
                                        for (Map.Entry<String, Keys> k : keys.entrySet()) {
                                            Keys keyTemp = new Keys(k.getValue().getKey(), k.getValue().getTime());
                                            keysTemp.add(keyTemp);
                                            i++;
                                        }
                                    }
                                    HashMap<String, Students> students = m.getValue().getStudents();
                                    if (students != null) {
                                        for (Map.Entry<String, Students> s : students.entrySet()) {
                                            if (s.getKey().equals(userUid)) {
                                                checkin = (s.getValue().getCheckin());
                                                checkout = (s.getValue().getCheckout());
                                                if(!checkin.equals("")&&checkout.equals("")){
                                                    final Handler handle = new Handler();
                                                    final String checkinF =checkin;
                                                    final String checkoutF =checkout;
                                                    final Events  ev_th = m.getValue();
                                                    final String uidEv = m.getKey();
                                                    final String uidTeacher = dados.getKey();
                                                    Runnable runnable = new Runnable() {

                                                        @Override
                                                        public void run() {

                                                            while (!stop) {
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
                                                                                if (!minH.equals(min)||firstTime) {

                                                                                    Log.d("AQUI", "Mudou o Minuto, novo horário: "+fullHour);
                                                                                  //  try {
                                                                                      //  Thread.sleep(500);
                                                                                        Log.d("AQUI", "Verificando se lança a key......");

                                                                                        givePop(fullHour, new Event(ev_th, uidEv, uidTeacher, checkinF, checkoutF, keysTemp));
                                                                                    //    Thread.sleep(500);
                                                                                        minH = min;
                                                                                  //  } catch (InterruptedException ex) {
                                                                                  //      ex.printStackTrace();
                                                                                 //   }

                                                                                }
                                                                                /*
                                                                                try {
                                                                                    Thread.sleep(500);
                                                                                } catch (InterruptedException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                                 */

                                                                            }
                                                                        });
                                                                    } catch (InterruptedException ex) {
                                                                        ex.printStackTrace();
                                                                    }
                                                                }

                                                              //  try {
                                                             //       Thread.sleep(500);
                                                              //  } catch (InterruptedException e) {
                                                              //      e.printStackTrace();
                                                              //  }
                                                            }

                                                        }
                                                    };
                                                    th = new Thread(runnable);
                                                    th.start();

                                                }
                                            }
                                        }
                                    }
                                    eventList.add(new Event(m.getValue(), m.getKey(), dados.getKey(), checkin, checkout, keysTemp));
                                    j++;
                                }
                            }

                        }
                    }
                    getCheckedEvents(eventList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getCheckedEvents(List<Event> events) {

        if (events.size() > 0) {
            msgNoEvents.setVisibility(View.INVISIBLE);
        } else {
            msgNoEvents.setVisibility(View.VISIBLE);
            imgNoEvents.setVisibility(View.VISIBLE);

        }
        buildRecyclerView(events);
    }

    public void setCheckInTime(List<Event> events, int position) {
        if (events.get(position).getCheckInTime() == null || events.get(position).getCheckInTime().isEmpty()) {
            Date time = new Date();
            String hora = Integer.toString(time.getHours());
            String min = Integer.toString(time.getMinutes());

            teacherBase.child(events.get(position).getuIdTeacher()).child("events").child(user.getTurma())
                    .child(events.get(position).getUid()).child("students").child(userUid).child("checkin")
                    .setValue(hora + "h" + min);

            events.get(position).setCheckInTime(hora + "h" + min);
            stop=false;
            getKeyWordUpdates(true, position, events);
            eventsAdapter.notifyItemChanged(position);
        }
    }

    public void setCheckOutTime(List<Event> events, int position) {
        if (events.get(position).getCheckInTime() != null && !events.get(position).getCheckInTime().isEmpty()) {
            if (!events.get(position).isCheckOutDone()) {
                Date time = new Date();
                String hora = Integer.toString(time.getHours());
                String min = Integer.toString(time.getMinutes());

                teacherBase.child(events.get(position).getuIdTeacher()).child("events").child(user.getTurma())
                        .child(events.get(position).getUid()).child("students").child(userUid).child("checkout")
                        .setValue(hora + "h" + min);

                events.get(position).setCheckInTime(hora + "h" + min);
                events.get(position).setCheckOutTime(hora + "h" + min);
               stop=true;
                eventsAdapter.notifyItemChanged(position);
            }
        }
    }

    public void buildRecyclerView(final List<Event> eventsList) {

        recyclerViewEvents = findViewById(R.id.home_recycler_view_events);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new MyRecyclerViewAdapter(eventsList);

        if (eventsList != null && eventsList.size() > 0) {
            recyclerViewEvents.setAdapter(eventsAdapter);
            progressBar.setVisibility(View.GONE);
        }

        eventsAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onCheckInClick(int position) {
                setCheckInTime(eventsList, position);
            }

            @Override
            public void onCheckOutClick(int position) {
                setCheckOutTime(eventsList, position);

            }

            @Override
            public void onGoLiveClick(int position) {
                Uri uri = Uri.parse(eventsList.get(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public void getKeyWordUpdates(boolean listen, final int position, final List<Event> eventsHere) {
        if (!listen) {
            th.interrupt();
            return;// caso deu checkout, o aluno não pode saber quais palavras chaves
        } else {

            final Handler handle = new Handler();

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    while (!stop) {
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
                                        if (!minH.equals(min)||firstTime) {

                                            Log.d("AQUI", "Mudou o Minuto, novo horário: "+fullHour);
                                           // try {
                                            //    Thread.sleep(500);
                                                Log.d("AQUI", "Verificando se lança a key......");
                                                givePop(fullHour, position, eventsHere);
                                              //  Thread.sleep(500);
                                                minH = min;
                                         //   } catch (InterruptedException ex) {
                                           //     ex.printStackTrace();
                                           // }

                                        }
                                        /*
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        */


                                    }
                                });
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
/*
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

 */
                    }

                }
            };
            th = new Thread(runnable);
            th.start();
            
        }

    }

    private void givePop(String fullHour, int position, List<Event> events)  {
        /*
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         */

        Log.d("AQUI", "APPHIDDEN: "+appHidden);
            if (fullHour.equals(events.get(position).getKeys().get(0).getTime())) {
                String key = events.get(position).getKeys().get(0).getKey();
                //th.interrupt();
                Log.d("AQUI", "Vai soltar o POP-UP");

                popUp(position, events, 0);
                //th.start();
                // TODO CODE NOTIFICATION

            } else if (fullHour.equals(events.get(position).getKeys().get(1).getTime())) {
                String key = events.get(position).getKeys().get(1).getKey();
                Log.d("AQUI", "Vai soltar o POP-UP");

                popUp(position, events, 1);


            } else if (fullHour.equals(events.get(position).getKeys().get(2).getTime())) {
                String key = events.get(position).getKeys().get(2).getKey();
                Log.d("AQUI", "Vai soltar o POP-UP");

                popUp(position, events, 2);

            }
/*
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        firstTime=false;
    }
    private void givePop(String fullHour, Event events)  {
       /*
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        */

        Log.d("AQUI", "APPHIDDEN: "+appHidden);
        if (fullHour.equals(events.getKeys().get(0).getTime())) {

            //th.interrupt();
            Log.d("AQUI", "Vai soltar o POP-UP");

            popUp( events, 0);
            //th.start();
            // TODO CODE NOTIFICATION

        } else if (fullHour.equals(events.getKeys().get(1).getTime())) {

            Log.d("AQUI", "Vai soltar o POP-UP");

            popUp( events, 1);



        } else if (fullHour.equals(events.getKeys().get(2).getTime())) {

            Log.d("AQUI", "Vai soltar o POP-UP");

            popUp( events, 2);

        }
/*
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

 */
        firstTime=false;

    }

    private void popUp(final int position, final List<Event> events, final int keyPosition) {
        MediaPlayer popup = MediaPlayer.create(this, R.raw.popup);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.dialog_teacher_key_word, null);
        final EditText edtEmail = (EditText) mView.findViewById(R.id.dialog_key_word_edt_password);
        Button btnConfirma = (Button) mView.findViewById(R.id.dialog_key_word_bt_confirma);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtEmail.getText().toString().equals("")||!edtEmail.getText().toString().isEmpty()) {
                    if (edtEmail.getText().toString().equals(events.get(position).getKeys().get(keyPosition).getKey())) {
                        teacherBase.child(events.get(position).getuIdTeacher()).child("events").child(user.getTurma())
                                .child(events.get(position).getUid()).child("students").child(userUid).child("keys").child("key" + Integer.toString(keyPosition + 1))
                                .setValue("ok");
                        dialog.dismiss();

                        Toast.makeText(HomeActivity.this, "Você acertou a palavra chave", Toast.LENGTH_SHORT).show();
                    } else {
                        teacherBase.child(events.get(position).getuIdTeacher()).child("events").child(user.getTurma())
                                .child(events.get(position).getUid()).child("students").child(userUid).child("keys").child("key" + Integer.toString(keyPosition + 1))
                                .setValue("HUMMMMMMMMMMMMMMMMMM PARECE QUE VC ERROU");
                        dialog.dismiss();

                        Toast.makeText(HomeActivity.this, "HUMMMMMMMMMMMMMMMMMM PARECE QUE VC ERROU", Toast.LENGTH_SHORT).show();
                    }
                    NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(getApplicationContext());
                    mNotificationMgr.cancel(1);
                }else{
                    Toast.makeText(HomeActivity.this, "PREENCHA O CAMPO", Toast.LENGTH_SHORT).show();
                }

            }
        });
        if(!firstTime)displayNotification("Frequência FMM","Olá, como está a aula? Você deve inserir a palavra-passe para notificar o professor que você está acompanhando a aula!!!");
        dialog.show();
        Log.d("AQUI", "POP-UP Lançado!!!!");
        popup.start();

    }
    private void popUp(final Event events, final int keyPosition) {
        MediaPlayer popup = MediaPlayer.create(this, R.raw.popup);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.dialog_teacher_key_word, null);
        final EditText edtEmail = (EditText) mView.findViewById(R.id.dialog_key_word_edt_password);
        Button btnConfirma = (Button) mView.findViewById(R.id.dialog_key_word_bt_confirma);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtEmail.getText().toString().equals("")||!edtEmail.getText().toString().isEmpty()){
                    if (edtEmail.getText().toString().equals(events.getKeys().get(keyPosition).getKey())) {
                        teacherBase.child(events.getuIdTeacher()).child("events").child(user.getTurma())
                                .child(events.getUid()).child("students").child(userUid).child("keys").child("key" + Integer.toString(keyPosition + 1))
                                .setValue("ok");
                        dialog.dismiss();

                        Toast.makeText(HomeActivity.this, "Você acertou a palavra chave", Toast.LENGTH_SHORT).show();
                    } else {
                        teacherBase.child(events.getuIdTeacher()).child("events").child(user.getTurma())
                                .child(events.getUid()).child("students").child(userUid).child("keys").child("key" + Integer.toString(keyPosition + 1))
                                .setValue("HUMMMMMMMMMMMMMMMMMM PARECE QUE VC ERROU");
                        dialog.dismiss();

                        Toast.makeText(HomeActivity.this, "HUMMMMMMMMMMMMMMMMMM PARECE QUE VC ERROU", Toast.LENGTH_SHORT).show();
                    }
                    NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(getApplicationContext());
                    mNotificationMgr.cancel(1);
                }else{
                    Toast.makeText(HomeActivity.this, "PREENCHA O CAMPO", Toast.LENGTH_SHORT).show();
                }

            }
        });
        if(!firstTime)displayNotification("Frequência FMM","Olá, como está a aula? Você deve inserir a palavra-passe para notificar o professor que você está acompanhando a aula!!!");
        dialog.show();
        Log.d("AQUI", "POP-UP Lançado!!!!");
        popup.start();

    }
    public void displayNotification( String title, String body){


        Log.d("AQUI","Entrou pra lançar notificação......");
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(intent.getComponent());
        stackBuilder.addNextIntent(intent);

        PendingIntent p = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent p = PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "simplified_coding")
                        .setTicker("Frequência FMM")
                        .setSmallIcon(R.drawable.logo_main)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(p)
                        .setVibrate(new long[]{150,300,150,300,150})
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);;

        Log.d("AQUI","Criou o Builder......");

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(getApplicationContext());
        mNotificationMgr.notify(1, mBuilder.build());

        Log.d("AQUI","Lançou a notificação......");

    }
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Frequência FMM";
            String description = "Notificação APP";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
