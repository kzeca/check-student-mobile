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
import java.util.List;
import java.util.Objects;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btInfo = findViewById(R.id.activity_home_bt_about_us);
        imgNoEvents = findViewById(R.id.activity_home_img_no_events);
        msgNoEvents = findViewById(R.id.msg_no_events);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        System.out.println("alo:" + user.getTurma());
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
                        Log.d("ADOLETA", "" + user.getTurma());
                        serie = turma.substring(0, 0);
                        curso = turma.substring(2, 2);
                    }
                }
                onCreateContinue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                atualizaEventos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void atualizaEventos(){
        getEvents();
    }

    private void onCreateContinue() {
        getEvents();

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


    public void getEvents() {
        events = new ArrayList<Event>();
        teachersUID = new ArrayList<>();
        //pegar uid professor

        teacherBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Professor teacher = new Professor();
                        teacher.setuId(dados.getKey());
                        teachersUID.add(teacher.getuId());
                        Log.d("ARMANDO", String.valueOf(teachersUID.size()));
                    }
                }

                events = getEventsContinue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("ARMANDO", String.valueOf(teachersUID.size()));

    }


    private List<Event> getEventsContinue() {

        if (teachersUID.size() > 0) {
            int i;
            for (i = 0; i < teachersUID.size(); i++) {
                Log.d("ARMANDO", "existe");
                uidTeacherCurrent = teachersUID.get(i);
                aux = teacherBase.child(teachersUID.get(i)).child("events").child(user.getTurma());//para verificar se existe evento para sala  única
                if (teacherBase.child(teachersUID.get(i)).child("events").child((aux != null ? user.getTurma() : serie + "ano")) != null) {//verifica se há evento para sala única ou para ano

                    teacherBase.child(teachersUID.get(i)).child("events").child((aux != null ? user.getTurma() : serie + "ano")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                                    if(!dados.getKey().equals("evento0")){
                                        Event evento = new Event(dados, uidTeacherCurrent, (aux != null ? user.getTurma() : serie + "ano"));
                                        events.add(evento);
                                    }
                                    else{

                                    }


                                }
                                events = getCheckedEvents();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {//Por curso
                    teacherBase.child(teachersUID.get(i)).child("events").child((serie + curso + "ano")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dados : dataSnapshot.getChildren()) {
                                Event evento = new Event(dados, uidTeacherCurrent, (serie + curso + "ano"));
                                events.add(evento);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
        return events;
    }

    private List<Event> getCheckedEvents() {

        //AO ADICIONAR OS EVENTOS NO ATRIBUTO URL CONCATENAR À URL A STRING : "https://"
        if (events.size() > 0) {
            msgNoEvents.setVisibility(View.INVISIBLE);
        } else {
            msgNoEvents.setVisibility(View.VISIBLE);
            imgNoEvents.setVisibility(View.VISIBLE);

        }
        buildRecyclerView(events);
        return events;
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

    public void buildRecyclerView(List<Event> eventsList) {

        recyclerViewEvents = findViewById(R.id.home_recycler_view_events);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new MyRecyclerViewAdapter(eventsList);

        if (events != null && events.size() > 0) {
            recyclerViewEvents.setAdapter(eventsAdapter);
        }

        eventsAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {

            String checkOutTime;
            String checkInTime;


            @Override
            public void onCheckInClick(int position) {
                if (!events.get(position).isCheckInDone()) {
                    events.get(position).setCheckInDone(true);
                    Date time = new Date();
                    String hora = Integer.toString(time.getHours());
                    String min = Integer.toString(time.getMinutes());
                    events.get(position).setCheckInTime(hora + "h" + min);
                    eventsAdapter.notifyItemChanged(position);
                    checkInTime = hora + ":" + min;
                    //Listener para verificar palavras chaves

                    //getKeyWordUpdates(events.get(position).isCheckInDone(), position);

                }
            }

            @Override
            public void onCheckOutClick(int position) {
                if (events.get(position).isCheckInDone()) {
                    if (!events.get(position).isCheckOutDone()) {
                        events.get(position).setCheckOutDone(true);
                        Date time = new Date();
                        String hora = Integer.toString(time.getHours());
                        String min = Integer.toString(time.getMinutes());
                        events.get(position).setCheckInTime(hora + "h" + min);
                        events.get(position).setCheckOutTime(hora + "h" + min);
                        eventsAdapter.notifyItemChanged(position);
                        checkOutTime = hora + ":" + min;
                        //getKeyWordUpdates(!events.get(position).isCheckOutDone(), position);
                    }


                }

            }

            @Override
            public void onGoLiveClick(int position) {
                Uri uri = Uri.parse(events.get(position).getUrl());
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
