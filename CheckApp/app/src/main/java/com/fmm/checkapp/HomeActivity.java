package com.fmm.checkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fmm.checkapp.Model.Event;
import com.fmm.checkapp.Model.MyRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    List<Event> events;
    RecyclerView recyclerViewEvents;
    MyRecyclerViewAdapter eventsAdapter;
    ImageButton btInfo;
    LinearLayout msgNoEvents;
    FirebaseUser firebaseUser;
    DatabaseReference dataBase;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btInfo = findViewById(R.id.activity_home_bt_about_us);
        msgNoEvents = findViewById(R.id.msg_no_events);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        dataBase = FirebaseDatabase.getInstance().getReference();
        buildRecyclerView();

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



    public List<Event> getEvents(){
        events = new ArrayList<Event>();
        events.add(new Event("L.Portuguesa", "Verbos", "7h", "8h", "",  "","https://meet.google.com/jyi-fkvd-gih"));
        events.add(new Event("L.Inglesa", "Mexican War", "9h", "10h", "",  "","https://www.google.com.br/"));
        events.add(new Event("Matemática", "Ponto e Reta", "11h", "12h", "", "", "https://www.google.com.br/"));
        events.add(new Event("Física", "Refração", "13h", "14h", "", "", "https://www.google.com.br/"));

        //AO ADICIONAR OS EVENTOS NO ATRIBUTO URL CONCATENAR À URL A STRING : "https://"
        if(events.size()> 0){
            msgNoEvents.setVisibility(View.INVISIBLE);
        }
        else{
            msgNoEvents.setVisibility(View.VISIBLE);
        }

        return events;
    }

    public void buildRecyclerView(){
        recyclerViewEvents = findViewById(R.id.home_recycler_view_events);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new MyRecyclerViewAdapter(getEvents());
        if(events != null && events.size()>0){
            recyclerViewEvents.setAdapter(eventsAdapter);
        }

        eventsAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {

            String checkOutTime;
            String checkInTime;


            @Override
            public void onCheckInClick(int position) {
                if(!events.get(position).isCheckInDone()){
                    events.get(position).setCheckInDone(true);
                    Date time = new Date();
                    String hora = Integer.toString(time.getHours());
                    String min = Integer.toString(time.getMinutes());
                    events.get(position).setCheckInTime(hora+"h" + min);
                    eventsAdapter.notifyItemChanged(position);
                    checkInTime = hora+":"+min;
                }
            }

            @Override
            public void onCheckOutClick(int position) {
                if(events.get(position).isCheckInDone()){
                    if(!events.get(position).isCheckOutDone()){
                        events.get(position).setCheckOutDone(true);
                        Date time = new Date();
                        String hora = Integer.toString(time.getHours());
                        String min = Integer.toString(time.getMinutes());
                        events.get(position).setCheckInTime(hora+"h" + min);
                        events.get(position).setCheckOutTime(hora+"h" + min);
                        eventsAdapter.notifyItemChanged(position);
                        checkOutTime = hora+":"+min;
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


}
