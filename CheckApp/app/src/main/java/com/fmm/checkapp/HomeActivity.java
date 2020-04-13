package com.fmm.checkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
    DatabaseReference aux;
    String userUid;
    ImageView imgNoEvents;
    ProgressBar progressBar;
    String serie;
    String curso;
    DatabaseReference teacherBase;
    Thread th;
    String minH;
    boolean stop;
    String TAG = "HomeActivity";

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

        Date hora = new Date();
        minH = Integer.toString(hora.getMinutes());
        minH = (hora.getMinutes()>=0&&hora.getMinutes()<=9 ? "0"+minH:minH);

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

    private void getCurrentUserEvents(final String turma) {
        teacherBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Events> firebaseEvents = new ArrayList<Events>();
                    List<Event> eventList = new ArrayList<Event>();

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Professores profs = dados.getValue(Professores.class);
                        HashMap<String, Events> events = profs.getEvents().get(turma);
                        if (events != null) {
                            int j = 0;
                            Date time = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                            String fullDate = sdf.format(time);
                            for (Map.Entry<String, Events> m : events.entrySet()) {
                                String checkin = "", checkout = "";
                                if (!m.getKey().equals("evento0")&&m.getValue().getDate().equals(fullDate)) {
                                    firebaseEvents.add(m.getValue());
                                    HashMap<String, Keys> keys = m.getValue().getKeys();
                                    List<Keys> keysTemp = new ArrayList<Keys>();
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
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        handle.post(new Runnable() {
                            @Override
                            public void run() {
                                Date time = new Date();
                                String hora = Integer.toString(time.getHours());
                                String min = Integer.toString(time.getMinutes());
                                min = (time.getMinutes()>=0&&time.getMinutes()<=9 ? "0"+min:min);
                                String fullHour = hora + "h" + min + "min";
                                Log.d("AQUI", "Hora atual"+fullHour);
                                if (!minH.equals(min)) {
                                    Log.d("AQUI", "Mudou o Minuto: "+fullHour);
                                    try {
                                        Thread.sleep(500);
                                        Log.d("AQUI", "Verificando se lança a key......");
                                        givePop(fullHour, position, eventsHere);
                                        Thread.sleep(500);
                                        minH = min;
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                    
                                }
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            th = new Thread(runnable);
            th.start();
            
        }

    }

    private void givePop(String fullHour, int position, List<Event> events)  {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


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

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void popUp(final int position, final List<Event> events, final int keyPosition) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
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

            }
        });

        dialog.show();
        Log.d("AQUI", "POP-UP Lançado!!!!");
    }

}
