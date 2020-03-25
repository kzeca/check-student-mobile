package com.fmm.checkapp.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.fmm.checkapp.R;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    public List<Event> eventList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onCheckInClick(int position);
        void onCheckOutClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public MyRecyclerViewAdapter(List<Event> eventList){
        this.eventList = eventList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvSubject, tvTitle, tvStartTime, tvEndTime, tvCheckInTime, tvCheckOutTime;
        private Button btnCheckIn, btnCheckOut;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.event_tv_subject);
            tvTitle = itemView.findViewById(R.id.event_tv_class_title);
            tvStartTime =itemView.findViewById(R.id.event_tv_class_starts_at);
            tvEndTime = itemView.findViewById(R.id.event_tv_class_ends_at);
            tvCheckInTime = itemView.findViewById(R.id.event_tv_check_in_at);
            tvCheckOutTime = itemView.findViewById(R.id.event_tv_check_out_at);
            btnCheckIn = itemView.findViewById(R.id.event_bt_check_in);
            btnCheckOut = itemView.findViewById(R.id.event_bt_check_out);

            btnCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCheckInClick(position);
                        }
                    }
                }
            });

            btnCheckOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCheckOutClick(position);
                        }
                    }
                }
            });


        }
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);
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
