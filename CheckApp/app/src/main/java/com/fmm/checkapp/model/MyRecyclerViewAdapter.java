package com.fmm.checkapp.model;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.fmm.checkapp.R;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    public List<Event> eventList;
    private OnItemClickListener mListener;
    private OnLongClickListener longClickListener;


    public interface OnItemClickListener{
        void onCheckInClick(int position);
        void onCheckOutClick(int position);
        void onGoLiveClick(int position);
        void onKeyButtonClick(int position);

    }
    public interface OnLongClickListener{
        void onLongCheckInClick(int position);
        void onLongCheckOutClick(int position);
        void onLongGoLiveClick(int position);
        void onLongKeyButtonClick(int position);

    }
    public void setOnLongClickListener(OnLongClickListener listener){
        longClickListener =  listener;
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
        private ImageButton btnGoLive, btnKey;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, final OnLongClickListener listenerLong) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.event_tv_subject);
            tvTitle = itemView.findViewById(R.id.event_tv_class_title);
            tvStartTime =itemView.findViewById(R.id.event_tv_class_starts_at);
            tvEndTime = itemView.findViewById(R.id.event_tv_class_ends_at);
            tvCheckInTime = itemView.findViewById(R.id.event_tv_check_in_at);
            tvCheckOutTime = itemView.findViewById(R.id.event_tv_check_out_at);
            btnCheckIn = itemView.findViewById(R.id.event_bt_check_in);
            btnCheckOut = itemView.findViewById(R.id.event_bt_check_out);
            btnGoLive = itemView.findViewById(R.id.event_bt_go_live);
            btnKey = itemView.findViewById(R.id.event_bt_keys);


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

            btnGoLive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onGoLiveClick(position);
                        }
                    }
                }
            });

            btnKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onKeyButtonClick(position);
                        }
                    }
                }
            });


            btnCheckIn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listenerLong != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listenerLong.onLongCheckInClick(position);
                        }
                    }
                    return true;
                }
            });


            btnCheckOut.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listenerLong != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listenerLong.onLongCheckOutClick(position);
                        }
                    }
                    return true;
                }
            });

            btnGoLive.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listenerLong != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listenerLong.onLongGoLiveClick(position);
                        }
                    }
                    return  true;
                }
            });
            btnKey.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listenerLong != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listenerLong.onLongKeyButtonClick(position);
                        }
                    }

                    return true;
                }
            });


        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener, (OnLongClickListener) longClickListener);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvSubject.setText(eventList.get(position).getSubject());
        holder.tvTitle.setText(eventList.get(position).getTitle());
        holder.tvStartTime.setText("Início: "+eventList.get(position).getStartTime());
        holder.tvEndTime.setText("Fim: "+ eventList.get(position).getEndTime());
        if(eventList.get(position).getCheckInTime() != null && !eventList.get(position).getCheckInTime().isEmpty()){
            holder.tvCheckInTime.setText("Check in realizado às "+eventList.get(position).getCheckInTime());
            holder.tvCheckInTime.setVisibility(View.VISIBLE);
            holder.btnCheckIn.setBackgroundTintList(holder.btnCheckIn.getResources().getColorStateList(R.color.cinza_botao));
            holder.btnCheckIn.setTextColor(holder.btnCheckIn.getContext().getResources().getColor(R.color.black));
            holder.btnCheckOut.setBackgroundTintList(holder.btnCheckIn.getResources().getColorStateList(R.color.azul_botao));
            holder.btnCheckOut.setTextColor(holder.btnCheckOut.getContext().getResources().getColor(R.color.white));
        }
        else{
            holder.tvCheckInTime.setVisibility(View.GONE);
            holder.tvCheckInTime.setText("");
        }
        if(eventList.get(position).getCheckOutTime() != null && !eventList.get(position).getCheckOutTime().isEmpty()){
            holder.tvCheckOutTime.setText("Check out realizado às "+eventList.get(position).getCheckOutTime());
            holder.tvCheckOutTime.setVisibility(View.VISIBLE);
            holder.btnCheckOut.setBackgroundTintList(holder.btnCheckIn.getResources().getColorStateList(R.color.cinza_botao));
            holder.btnCheckOut.setTextColor(holder.btnCheckOut.getContext().getResources().getColor(R.color.black));
        }
        else{
            holder.tvCheckOutTime.setVisibility(View.GONE);
            holder.tvCheckOutTime.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


}
