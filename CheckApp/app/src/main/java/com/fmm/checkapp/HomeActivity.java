package com.fmm.checkapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.fmm.checkapp.Model.MyRecyclerViewAdapter;
import com.fmm.checkapp.Model.Professor;
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
import java.text.SimpleDateFormat;

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
    String serie;
    String curso;
    DatabaseReference teacherBase;
    boolean correcao = true;
    int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btInfo = findViewById(R.id.activity_home_bt_about_us);
        msgNoEvents = findViewById(R.id.msg_no_events);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dataBase = FirebaseDatabase.getInstance().getReference();
        teacherBase = dataBase.child("professores");
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
                        onCreateContinue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        dataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        // pegar uid professor

        teacherBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Professor teacher = new Professor();
                        teacher.setuId(dados.getKey());
                        teachersUID.add(teacher.getuId());
                    }
                }
                events = getEventsContinue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<Event> getEventsContinue() {
        if (teachersUID.size() > 0) {
                events = ourWhileCheckin(contador);
            }
        return events;
    }

    private List<Event> ourWhileCheckin(int i) {
        if(i<teachersUID.size()) {
            SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yy");
            Date data = new Date();
            final String dataHoje = formataData.format(data);
            uidTeacherCurrent = teachersUID.get(contador);
            aux = teacherBase.child(uidTeacherCurrent).child("events").child(user.getTurma());
            if(aux!=null){
                teacherBase.child(uidTeacherCurrent).child("events").child(user.getTurma())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                                        if (!dados.getKey().equals("evento0")) {
                                            if (dados.child("date").getValue().toString().equals(dataHoje)) {
                                                Event evento = new Event(dados, uidTeacherCurrent, user.getTurma());
                                                events.add(evento);
                                            }
                                        }
                                        ourWhileCheckin(++contador);
                                    }
                                    events = getCheckedEvents();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
        return events;
    }


    private List<Event> getCheckedEvents() {
        ImageView imgNoEvents = findViewById(R.id.activity_home_img_no_events);
        // AO ADICIONAR OS EVENTOS NO ATRIBUTO URL CONCATENAR À URL A STRING :
        // "https://"
        if (events.size() > 0) {
            msgNoEvents.setVisibility(View.INVISIBLE);
        } else {
            msgNoEvents.setVisibility(View.VISIBLE);
            imgNoEvents.setVisibility(View.VISIBLE);

        }
        buildRecyclerView(events);
        return events;
    }

    /*
     * 
     * public List<Keyword> getKeyWords(String uIdTeacher) {//Recupera as Keywords
     * que já estavam registradas final List<Keyword> keywords = new ArrayList<>();
     * 
     * //Acessa parte dos professores
     * 
     * 
     * //Pegar palavras do banco
     * 
     * 
     * if (teacherBase.child(uIdTeacher).child("events").child((aux != null ? turma
     * : serie + "ano")) != null) {//verifica se há evento para sala única ou para
     * ano
     * 
     * teacherBase.child(uIdTeacher).child("events").child((aux != null ? turma :
     * serie + "ano")).child("keys").addListenerForSingleValueEvent(new
     * ValueEventListener() {
     * 
     * @Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
     * if(dataSnapshot.exists()){ for (DataSnapshot dados :
     * dataSnapshot.getChildren()) { Keyword key = dados.getValue(Keyword.class);
     * keywords.add(key); } } }
     * 
     * @Override public void onCancelled(@NonNull DatabaseError databaseError) {
     * 
     * } }); } else {//Por curso
     * teacherBase.child(uIdTeacher).child("events").child((serie + curso +
     * "ano")).child("keys").addListenerForSingleValueEvent(new ValueEventListener()
     * {
     * 
     * @Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
     * if(dataSnapshot.exists()){ for (DataSnapshot dados :
     * dataSnapshot.getChildren()) { Keyword key = dados.getValue(Keyword.class);
     * keywords.add(key); } } }
     * 
     * @Override public void onCancelled(@NonNull DatabaseError databaseError) {
     * 
     * } }); }
     * 
     * return keywords; }
     */
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
                    String hora;
                    String min;

                    if(time.getHours()<10) hora = "0"+Integer.toString(time.getHours());
                    else hora = "0"+Integer.toString(time.getHours());
                    if(time.getMinutes()<10) min = "0"+Integer.toString(time.getMinutes());
                    else min = Integer.toString(time.getMinutes());
                    events.get(position).setCheckInTime(hora + "h" + min);
                    eventsAdapter.notifyItemChanged(position);
                    checkInTime = hora + "h" + min;
                    events.get(position).setCheckInTime(checkInTime);


                    // Listener para verificar palavras chaves

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
                        getKeyWordUpdates(!events.get(position).isCheckOutDone(), position);
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

    public void getKeyWordUpdates(boolean listen, final int position) {// Veririfica as Keywords que foram adicionadas
        // Acessa parte dos professores

        // Acessa conta do professor que mandou o evento
        // Precisa saber a turma do aluno e do 'nome' do evento
        if (!listen)
            return;// caso deu checkout, o aluno não pode saber quais palavras chaves
        else {/*
            int alarmeHora, alarmeMinuto;
            Date time = new Date();
            String hora = Integer.toString(time.getHours());
            String min = Integer.toString(time.getMinutes());
            String fullHour = hora + "h" + min + "min";
            if(Integer.parseInt(hora)<10){
                alarmeHora = Integer.parseInt(events.get(position).getKeys().get(0).getTime().substring(0,1));
                alarmeMinuto = Integer.parseInt(events.get(position).getKeys().get(0).getTime().substring(3,5));
            }

            Log.d("Arley", events.get(position).getKeys().get(0).getTime().substring(0,2));
            Log.d("Arley", events.get(position).getKeys().get(0).getTime().substring(3,5));

            /*
            for(int i = 0;i>3;i++) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, events.get(position).getKeys().get(0).getTime(), pendingIntent );
            }*/


            // KeyPopUpThread(position).start();
            /*int para = 0;
              new Thread({
            while (para == 0) {
                Date time = new Date();
                String hora = Integer.toString(time.getHours());
                String min = Integer.toString(time.getMinutes());
                String fullHour = hora + ":" + min + "min";
                Log.d("ARLEY", fullHour);

                if (fullHour.equals(events.get(position).getKeys().get(0).getTime())) {
                    String key = events.get(position).getKeys().get(0).getKey();
                    // TODO CODE OF POP UP

                    Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                            Toast.LENGTH_LONG).show();
                    para = 1;

                } else if (fullHour.equals(events.get(position).getKeys().get(1).getTime())) {
                    String key = events.get(position).getKeys().get(1).getKey();
                    // TODO CODE OF POP UP

                    Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                            Toast.LENGTH_LONG).show();

                } else if (fullHour.equals(events.get(position).getKeys().get(2).getTime())) {
                    String key = events.get(position).getKeys().get(2).getKey();
                    // TODO CODE OF POP UP

                    Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                            Toast.LENGTH_LONG).show();

                }

            }

            }).start();
*/


            /*
            while (para == 0) {
                Date time = new Date();
                String hora = Integer.toString(time.getHours());
                String min = Integer.toString(time.getMinutes());
                String fullHour = hora + "h" + min + "min";
                Log.d("ARLEY",fullHour);

                  if(fullHour.equals(events.get(position).getKeys().get(0).getTime())) {
                      String key = events.get(position).getKeys().get(0).getKey();
                      // TODO CODE OF POP UP

                      Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                              Toast.LENGTH_LONG).show();
                      para = 1;

                  }else if(fullHour.equals(events.get(position).getKeys().get(1).getTime())) {
                    String key = events.get(position).getKeys().get(1).getKey();
                    // TODO CODE OF POP UP

                    Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                            Toast.LENGTH_LONG).show();

                    }else if(fullHour.equals(events.get(position).getKeys().get(2).getTime())) {
                      String key = events.get(position).getKeys().get(2).getKey();
                      // TODO CODE OF POP UP

                      Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                            Toast.LENGTH_LONG).show();

                }

                }*/

            }

        }

        void popUp(){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_teacher_key_word, null);
            final EditText edtEmail = (EditText) mView.findViewById(R.id.editText);
            Button btnConfirma = (Button) mView.findViewById(R.id.button);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                            /*btnConfirma.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!edtEmail.getText().toString().isEmpty() && edtEmail.getText().toString().contains("@")){
                                        auth.sendPasswordResetEmail(edtEmail.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        dialog.dismiss();
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(), "Email para redefinição enviado!(Confira a caixa de spam)", Toast.LENGTH_LONG).show();
                                                        } else
                                                            Toast.makeText(getApplicationContext(), "Informe um email válido", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }
                            });*/
            dialog.show();

        }

        /*
        public class KeyPopUpThread extends Thread{
            int position;

            public KeyPopUpThread(int position) {
                super();
                this.position = position;
            }

            @Override
            public void run() {
                super.run();
                while (true) {
                    Date time = new Date();
                    String hora = Integer.toString(time.getHours());
                    String min = Integer.toString(time.getMinutes());
                    String fullHour = hora + "h" + min + "min";
                    Log.d("ARLEY", fullHour);

                    if (fullHour.equals(events.get(position).getKeys().get(0).getTime())) {
                        String key = events.get(position).getKeys().get(0).getKey();
                        // TODO CODE OF POP UP

                        Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                                Toast.LENGTH_LONG).show();


                    } else if (fullHour.equals(events.get(position).getKeys().get(1).getTime())) {
                        String key = events.get(position).getKeys().get(1).getKey();
                        // TODO CODE OF POP UP

                        Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                                Toast.LENGTH_LONG).show();

                    } else if (fullHour.equals(events.get(position).getKeys().get(2).getTime())) {
                        String key = events.get(position).getKeys().get(2).getKey();
                        // TODO CODE OF POP UP

                        Toast.makeText(HomeActivity.this, "Hora de adicionar uma nova Key: " + key + "",
                                Toast.LENGTH_LONG).show();

                    }
                }
            }

        }
        */
}

