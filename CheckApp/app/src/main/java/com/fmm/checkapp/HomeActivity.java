package com.fmm.checkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.fmm.checkapp.Model.Event;
import com.fmm.checkapp.Model.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {

    List<Event> events;
    RecyclerView recyclerViewEvents;
    MyRecyclerViewAdapter eventsAdapter;
    LinearLayout msgNoEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        msgNoEvents = findViewById(R.id.msg_no_events);
        recyclerViewEvents = (RecyclerView) findViewById(R.id.home_recycler_view_events);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new MyRecyclerViewAdapter(getEvents());
        if(events.size()>0)recyclerViewEvents.setAdapter(eventsAdapter);


    }

    public List<Event> getEvents(){
        events = new ArrayList<Event>();
        events.add(new Event("L.Portuguesa", "Verbos", "7h", "8h", "", ""));
        events.add(new Event("L.Inglesa", "Mexeican War", "9h", "10h", "", ""));
        events.add(new Event("Matemática", "Ponto e Reta", "11h", "12h", "", ""));
        events.add(new Event("Física", "Reflexão em espelhos", "13h", "14h", "", ""));
        if(events.size()> 0){
            msgNoEvents.setVisibility(View.INVISIBLE);
        }
        else{
            msgNoEvents.setVisibility(View.VISIBLE);
        }

        return events;
    }


}
