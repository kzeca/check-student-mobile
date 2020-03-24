package com.fmm.checkapp.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fmm.checkapp.R;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {



    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvSubject, tvTitle, tvStartTime, tvEndTime, tvCheckInTime, tvCheckOutTime;
        private Button btnCheckIn, btnCheckOut;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.event_tv_subject);
            tvTitle = itemView.findViewById(R.id.event_tv_class_title);
            tvStartTime =itemView.findViewById(R.id.event_tv_class_starts_at);
            tvEndTime = itemView.findViewById(R.id.event_tv_class_ends_at);
            tvCheckInTime = itemView.findViewById(R.id.event_tv_check_in_at);
            tvCheckOutTime = itemView.findViewById(R.id.event_tv_check_out_at);
            btnCheckIn = itemView.findViewById(R.id.event_bt_check_in);
            btnCheckOut = itemView.findViewById(R.id.event_bt_check_out);


        }
    }

    public List<Event> eventList;

    public MyRecyclerViewAdapter(List<Event> eventList){
        this.eventList = eventList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvSubject.setText(eventList.get(position).getSubject());
        holder.tvTitle.setText(eventList.get(position).getTitle());
        holder.tvStartTime.setText("Início: "+eventList.get(position).getStartTime());
        holder.tvEndTime.setText("Fim: "+ eventList.get(position).getEndTime());
        if(!eventList.get(position).getCheckInTime().isEmpty() && eventList.get(position).getCheckInTime() != null){
            holder.tvCheckInTime.setText("Check in realizado às "+eventList.get(position).getCheckInTime());
        }
        else{
            holder.tvCheckInTime.setText("");
        }

        if(!eventList.get(position).getCheckOutTime().isEmpty() && eventList.get(position).getCheckOutTime() != null){
            holder.tvCheckOutTime.setText("Check out realizado às "+eventList.get(position).getCheckOutTime());
        }
        else{
            holder.tvCheckOutTime.setText("");
        }
        System.out.println("DAQUELE JEITAO: "+ position);
    }

    @Override
    public int getItemCount() {
        System.out.println("AQUI:"+eventList.size());
        return eventList.size();

    }


}
