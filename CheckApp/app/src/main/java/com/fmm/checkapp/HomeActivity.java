package com.fmm.checkapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fmm.checkapp.Model.Event;
import com.fmm.checkapp.Model.Keyword;
import com.fmm.checkapp.Model.MyRecyclerViewAdapter;
import com.fmm.checkapp.Model.Professor;
import com.fmm.checkapp.firebasemodel.Events;
import com.fmm.checkapp.firebasemodel.Professores;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.fmm.checkapp.LoginActivity.user;

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
    List<String> teachersUID;
    String uid, uidTeacherCurrent;
    ImageView imgNoEvents;
    String serie;
    String curso;
    DatabaseReference teacherBase;
    static boolean firstTime = true;

    String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btInfo = findViewById(R.id.activity_home_bt_about_us);
        imgNoEvents = findViewById(R.id.activity_home_img_no_events);
        msgNoEvents = findViewById(R.id.msg_no_events);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dataBase = FirebaseDatabase.getInstance().getReference();
        teacherBase = dataBase.child("professores");

        dataBase.child("salas").orderByChild(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String turma = childSnapshot.getKey();
                    user.setTurma(turma);
                    if (turma != null) {
                        user.setTurma(turma);
                        Log.d(TAG, "" + user.getTurma());
                        serie = turma.substring(0, 0);
                        curso = turma.substring(2, 2);
                    }
                }
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

    private void getCurrentUserEvents(final String turma){
        Log.d(TAG, " getCurrentUserEvents turma :: " + turma);

        teacherBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Events> firebaseEvents = new ArrayList<Events>();
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Professores profs = dados.getValue(Professores.class);
                        HashMap <String, Events> events = profs.getEvents().get(turma);

                        if(events != null ){
                            for (Map.Entry<String, Events> m : events.entrySet()){
                                firebaseEvents.add(m.getValue());
                            }
                        }
                    }
                    List<Event> eventList = new ArrayList<>();
                    for (Events e: firebaseEvents){
                        eventList.add(new Event(e));
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

        //AO ADICIONAR OS EVENTOS NO ATRIBUTO URL CONCATENAR À URL A STRING : "https://"
        if (events.size() > 0) {
            msgNoEvents.setVisibility(View.INVISIBLE);
        } else {
            msgNoEvents.setVisibility(View.VISIBLE);
            imgNoEvents.setVisibility(View.VISIBLE);

        }
        buildRecyclerView(events);
    }


    public List<Keyword> getKeyWords(String uIdTeacher) {//Recupera as Keywords que já estavam registradas
        final List<Keyword> keywords = new ArrayList<>();

        //Acessa parte dos professores
        DatabaseReference teachersBase = dataBase.child("professores");

        //Pegar palavras do banco


        if (teacherBase.child(uIdTeacher).child("events").child((aux != null ? turma : serie + "ano")) != null) {//verifica se há evento para sala única ou para ano

            teacherBase.child(uIdTeacher).child("events").child((aux != null ? turma : serie + "ano")).child("keys").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Keyword key = dados.getValue(Keyword.class);
                        keywords.add(key);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {//Por curso
            teacherBase.child(uIdTeacher).child("events").child((serie + curso + "ano")).child("keys").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Keyword key = dados.getValue(Keyword.class);
                        keywords.add(key);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return keywords;
    }

    public void buildRecyclerView(final List<Event> eventsList) {

        recyclerViewEvents = findViewById(R.id.home_recycler_view_events);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new MyRecyclerViewAdapter(eventsList);

        if (eventsList != null && eventsList.size() > 0) {
            recyclerViewEvents.setAdapter(eventsAdapter);
        }

        eventsAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {

            String checkOutTime;
            String checkInTime;

            @Override
            public void onCheckInClick(int position) {
                if (!eventsList.get(position).isCheckInDone()) {
                    eventsList.get(position).setCheckInDone(true);
                    Date time = new Date();
                    String hora = Integer.toString(time.getHours());
                    String min = Integer.toString(time.getMinutes());
                    eventsList.get(position).setCheckInTime(hora + "h" + min);
                    eventsAdapter.notifyItemChanged(position);
                    checkInTime = hora + ":" + min;
                }
            }

            @Override
            public void onCheckOutClick(int position) {
                if (eventsList.get(position).isCheckInDone()) {
                    if (!eventsList.get(position).isCheckOutDone()) {
                        eventsList.get(position).setCheckOutDone(true);
                        Date time = new Date();
                        String hora = Integer.toString(time.getHours());
                        String min = Integer.toString(time.getMinutes());
                        eventsList.get(position).setCheckInTime(hora + "h" + min);
                        eventsList.get(position).setCheckOutTime(hora + "h" + min);
                        eventsAdapter.notifyItemChanged(position);
                        checkOutTime = hora + ":" + min;
                    }
                }

            }

            @Override
            public void onGoLiveClick(int position) {
                Uri uri = Uri.parse(eventsList.get(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    public void getKeyWordUpdates(boolean listen, final int position) {//Veririfica as Keywords que foram adicionadas
        //Acessa parte dos professores
        DatabaseReference teachersBase = dataBase.child("professores");

        //Acessa conta do professor que mandou o evento
        //Precisa saber a turma do aluno e do 'nome' do evento
        if (!listen) return;//caso deu checkout, o aluno não pode saber quais palavras chaves
        teachersBase.child(events.get(position).getuIdTeacher()).child("events").child(events.get(position).getClassEvent()).child("keys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Keyword> keysAux = new ArrayList<Keyword>();
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    keysAux.add((Keyword) dados.getValue());//atualiza as palavras chaves lançadas

                }
                events.get(position).setKeys(keysAux);
                System.out.println("TEM PALAVRA CHAVE NOVA");
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(position).getKeys().get(i) != events.get(position).getKeysLatest().get(i)) {
                        //TODO CODE OF POP UP


                        //Depois que responder
                        events.get(position).setKeysLatest(events.get(position).getKeys());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
