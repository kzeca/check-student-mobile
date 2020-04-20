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
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fmm.checkapp.model.Event;
import com.fmm.checkapp.model.MyRecyclerViewAdapter;
import com.fmm.checkapp.firebasemodel.Events;
import com.fmm.checkapp.firebasemodel.Keys;
import com.fmm.checkapp.firebasemodel.Professores;
import com.fmm.checkapp.firebasemodel.Students;
import com.fmm.checkapp.task.TimeAsyncTask;
import com.fmm.checkapp.util.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.fmm.checkapp.LoginActivity.user;

public class HomeActivity extends Activity{

    RecyclerView recyclerViewEvents;
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
    boolean appHidden, firstTime,checkinChecked;
    boolean runningThread;
    public static Event CURRENT_EVENT;
    final static String CHANNEL_ID = "simplified_coding";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        btInfo = findViewById(R.id.activity_home_bt_about_us);
        imgNoEvents = findViewById(R.id.activity_home_img_no_events);
        msgNoEvents = findViewById(R.id.msg_no_events);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        th = null;
        userUid = firebaseUser.getUid();
        dataBase = FirebaseDatabase.getInstance().getReference();
        teacherBase = dataBase.child("professores");
        progressBar = findViewById(R.id.activity_home_progressBar);
        appHidden = false;
        firstTime = true;
        runningThread = false;
        URL url = NetworkUtil.buildUrl("America", "Manaus");
        TimeAsyncTask asyncTask = new TimeAsyncTask(new TimeAsyncTask.OnFinishTask() {
            @Override
            public void onFinish(String hora, String min) {
                minH = min;
            }
        });
        asyncTask.execute(url);


        CURRENT_EVENT=null;
        createNotificationChannel(getApplicationContext());
        createNotificationChannelFIREBASE(getApplicationContext());

        dataBase.child("salas").orderByChild(userUid)
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

        teacherBase.addValueEventListener(new ValueEventListener() {
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
                popUpMoreOption();
            }
        });

        btInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(HomeActivity.this, "Mais opções", Toast.LENGTH_SHORT).show();
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
                                if (!m.getKey().equals("evento0") && m.getValue().getDate().equals(fullDate)) {
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
                                                if (!checkin.equals("") && checkout.equals("")) {
                                                    final Handler handle = new Handler();
                                                    final String checkinF = checkin;
                                                    final String checkoutF = checkout;
                                                    final Events ev_th = m.getValue();
                                                    final String uidEv = m.getKey();
                                                    final String uidTeacher = dados.getKey();
                                                    CURRENT_EVENT = new Event(ev_th, uidEv, uidTeacher, checkinF, checkoutF, keysTemp);

                                                    Runnable runnable = new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            while (!stop) {
                                                                Log.d("AQUI", "Na Thread Current Events.....");
                                                                synchronized (this) {
                                                                    try {
                                                                        wait(500);
                                                                        handle.post(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                runningThread = true;
                                                                                Date time = new Date();
                                                                                String hora = Integer.toString(time.getHours());
                                                                                String min = Integer.toString(time.getMinutes());
                                                                                min = (time.getMinutes() >= 0 && time.getMinutes() <= 9 ? "0" + min : min);
                                                                                hora = (time.getHours() >= 0 && time.getHours() <= 9 ? "0" + hora : hora);
                                                                                String fullHour = hora + "h" + min + "min";
                                                                                Log.d("AQUI", "Hora atual, no celular: " + fullHour);
                                                                                if (!minH.equals(min) || firstTime) {

                                                                                    Log.d("AQUI", "Mudou o Minuto, novo horário, no celular: " + fullHour);

                                                                                    Log.d("AQUI", "Verificando se lança a key......");

                                                                                    givePop(CURRENT_EVENT);

                                                                                    minH = min;

                                                                                }


                                                                            }
                                                                        });

                                                                    } catch (InterruptedException ex) {
                                                                        ex.printStackTrace();
                                                                    }
                                                                }
                                                                try {
                                                                    Thread.sleep(2500);
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                            runningThread = false;

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

    public void setCheckInTime(final List<Event> events, final int position) {

        if (!runningThread) {

            URL url = NetworkUtil.buildUrl("America", "Manaus");
            TimeAsyncTask asyncTask = new TimeAsyncTask(new TimeAsyncTask.OnFinishTask() {
                @Override
                public void onFinish(String hora, String min) {
                    //Start Time of Event
                    final int horaEvent = Integer.parseInt(events.get(position).getStartTime().substring(0, 2));//Pegar a hora do Evento --> HHhMMmin
                    final int minEvent = Integer.parseInt(events.get(position).getStartTime().substring(3, 5));//Pegar o minuto do Evento --> HHhMMmin
                    final Date dInicio = new Date();
                    dInicio.setHours(horaEvent);
                    dInicio.setMinutes(minEvent);
                    Log.d("AQUI", "Hora de inicio do Evento: " + horaEvent + "h" + minEvent);
                    //Finish Time of Event
                    final int horaEventFinal = Integer.parseInt(events.get(position).getEndTime().substring(0, 2));//Pegar a hora do Evento --> HHhMMmin
                    final int minEventFinal = Integer.parseInt(events.get(position).getEndTime().substring(3, 5));//Pegar o minuto do Evento --> HHhMMmin
                    final Date dFinal = new Date();
                    dFinal.setHours(horaEventFinal);
                    dFinal.setMinutes(minEventFinal);
                    Log.d("AQUI", "Hora de fim do Evento: " + dFinal.getHours() + "h" + dFinal.getMinutes());
                    int horaNow = Integer.parseInt(hora);
                    int minNow = Integer.parseInt(min);
                    Date dCell = new Date();
                    dCell.setHours(horaNow);
                    dCell.setMinutes(minNow);
                    int horaEmMinutosEvent = horaEvent * 60 + minEvent;
                    int horaEmMinutosNow = horaNow * 60 + minNow;
                    //10 minutos antes ou entre o período do evento
                    Log.d("AQUI", "Hora do Celular está depois do inicio: " + dCell.after(dInicio) + "   Hora do Celular está antes do Final: " + dCell.before(dFinal));
                    if (((minEvent - minNow) <= 10 && (minEvent - minNow) >= 0 && horaNow == horaEvent) || (horaNow != horaEvent && (horaEmMinutosEvent - horaEmMinutosNow) <= 10 && (horaEmMinutosEvent - horaEmMinutosNow) >= 0) || (dCell.after(dInicio) && dCell.before(dFinal))) {
                        checkinChecked=true;
                    }else{
                        checkinChecked=false;
                    }
                    if (checkinChecked) {
                        if (events.get(position).getCheckInTime() == null || events.get(position).getCheckInTime().isEmpty()) {

                            teacherBase.child(events.get(position).getuIdTeacher()).child("events").child(user.getTurma())
                                    .child(events.get(position).getUid()).child("students").child(userUid).child("checkin")
                                    .setValue(hora + "h" + min);

                            events.get(position).setCheckInTime(hora + "h" + min);





                            stop = false;
                            CURRENT_EVENT = events.get(position);
                            ComponentName componentName = new ComponentName(HomeActivity.this, NotificationServiceScheduler.class);
                            JobInfo info = new JobInfo.Builder(123, componentName)
                                    .setRequiresCharging(false)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                                    .setPersisted(true)
                                    .setPeriodic(15 * 60 * 100)
                                    .build();
                            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                            int resultCode = scheduler.schedule(info);
                            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                                Log.d("AQUI", "Job scheduled");
                            } else {
                                Log.d("AQUI", "Job scheduled failed");
                            }
                            eventsAdapter.notifyItemChanged(position);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Você pode entrar com 10 minutos antes de inciar a aula ou durante o evento", Toast.LENGTH_LONG).show();
                    }
                }
            });
            asyncTask.execute(url);


        } else if (runningThread && events.get(position).getCheckInTime().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Aperte em checkout no último evento que você entrou", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCheckOutTime(final List<Event> events, final int position) {
        if (events.get(position).getCheckInTime() != null && !events.get(position).getCheckInTime().isEmpty()) {
            if (!events.get(position).isCheckOutDone()) {


                URL url = NetworkUtil.buildUrl("America", "Manaus");
                TimeAsyncTask asyncTask = new TimeAsyncTask(new TimeAsyncTask.OnFinishTask() {
                    @Override
                    public void onFinish(String hora, String min) {
                        teacherBase.child(events.get(position).getuIdTeacher()).child("events").child(user.getTurma())
                                .child(events.get(position).getUid()).child("students").child(userUid).child("checkout")
                                .setValue(hora + "h" + min);

                        events.get(position).setCheckInTime(hora + "h" + min);
                        events.get(position).setCheckOutTime(hora + "h" + min);
                    }
                });
                asyncTask.execute(url);


                stop = true;
                CURRENT_EVENT = null;
                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.cancel(123);
                Log.d("AQUI", "Job Schedular Cancelled");
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
        eventsAdapter.setOnLongClickListener(new MyRecyclerViewAdapter.OnLongClickListener() {
            @Override
            public void onLongCheckInClick(int position) {

            }

            @Override
            public void onLongCheckOutClick(int position) {

            }

            @Override
            public void onLongGoLiveClick(int position) {
                copyLinkMeet(eventsList.get(position).getUrl());
            }
        });

    }

    private void givePop( final Event events) {
        URL url = NetworkUtil.buildUrl("America", "Manaus");
        TimeAsyncTask asyncTask = new TimeAsyncTask(new TimeAsyncTask.OnFinishTask() {
            @Override
            public void onFinish(String hora, String min) {
                String fullHour = hora + "h" + min + "min";
                if (fullHour.equals(events.getKeys().get(0).getTime())) {
                    Log.d("AQUI", "Vai soltar o POP-UP");
                    popUp(events, 0);
                } else if (fullHour.equals(events.getKeys().get(1).getTime())) {
                    Log.d("AQUI", "Vai soltar o POP-UP");
                    popUp(events, 1);
                } else if (fullHour.equals(events.getKeys().get(2).getTime())) {

                    Log.d("AQUI", "Vai soltar o POP-UP");

                    popUp(events, 2);

                } else {
                    CURRENT_EVENT = events;
                }
                firstTime = false;
            }
        });
        asyncTask.execute(url);


    }

    private void popUp(final Event events, final int keyPosition) {

        MediaPlayer popup = MediaPlayer.create(this, R.raw.popup);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.dialog_teacher_key_word, null);
        final EditText edtEmail = (EditText) mView.findViewById(R.id.dialog_key_word_edt_password);
        TextView messageKey = mView.findViewById(R.id.number_key_word);
        Button btnConfirma = (Button) mView.findViewById(R.id.dialog_key_word_bt_confirma);
        messageKey.setText("Insira a " + (keyPosition + 1) + "ª" + " palavra-passe fornecida pelo(a) professor(a).");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtEmail.getText().toString().equals("") || !edtEmail.getText().toString().isEmpty()) {
                    if (edtEmail.getText().toString().equals(events.getKeys().get(keyPosition).getKey())) {
                        teacherBase.child(events.getuIdTeacher()).child("events").child(user.getTurma())
                                .child(events.getUid()).child("students").child(userUid).child("keys").child("key" + Integer.toString(keyPosition + 1))
                                .setValue("ok");
                        dialog.dismiss();

                        Toast.makeText(HomeActivity.this, "Palavra-passe inserida com sucesso", Toast.LENGTH_SHORT).show();

                    } else {
                        teacherBase.child(events.getuIdTeacher()).child("events").child(user.getTurma())
                                .child(events.getUid()).child("students").child(userUid).child("keys").child("key" + Integer.toString(keyPosition + 1))
                                .setValue("");
                        dialog.dismiss();

                        Toast.makeText(HomeActivity.this, "Palavra-passe inserida incorretamente, preste mais atenção na aula", Toast.LENGTH_SHORT).show();
                    }
                    NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(getApplicationContext());
                    mNotificationMgr.cancel(1);
                } else {
                    Toast.makeText(HomeActivity.this, "PREENCHA O CAMPO", Toast.LENGTH_SHORT).show();
                }

            }
        });
        if (!firstTime || appHidden)
            displayNotification("Frequência FMM", "Olá, como está a aula? Você deve inserir a palavra-passe para notificar o professor que você está acompanhando a aula!!!");
        popup.start();
        dialog.show();
        Log.d("AQUI", "POP-UP Lançado!!!!");
        CURRENT_EVENT = events;
    }

    public void displayNotification(String title, String body) {


        Log.d("AQUI", "Entrou pra lançar notificação......");

        Intent it = new Intent(getApplicationContext(), HomeActivity.class);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "simplified_coding")
                        .setTicker("Frequência FMM")
                        .setSmallIcon(R.drawable.logo_main)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setVibrate(new long[]{150, 300, 150, 300, 150})
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("AQUI", "Criou o Builder......");

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(getApplicationContext());
        mNotificationMgr.notify(1, mBuilder.build());

        Log.d("AQUI", "Lançou a notificação......");


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
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void logOut() {
        stop = true;
        CURRENT_EVENT = null;
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d("AQUI", "Job Schedular Cancelled");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void screenAbout() {
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
    }

    private void popUpMoreOption() {

        MediaPlayer popup = MediaPlayer.create(this, R.raw.popup);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_options, null);
        Button btnAboutUs = mView.findViewById(R.id.dialog_options_bt_about_us);
        Button btnLogOut = mView.findViewById(R.id.dialog_options_bt_logout);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                screenAbout();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                logOut();

            }
        });

        dialog.show();

        popup.start();

    }

    private void copyLinkMeet(String link) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText(link, link);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getApplicationContext(), "Link do Google Meet copiado com sucesso", Toast.LENGTH_SHORT).show();
        }

    }


    interface OnDateTimeReceived{
        void dateTimeReceivedListener(String hora, String min);
    }
}
