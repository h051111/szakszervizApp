package com.example.szakszervizapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;

public class IdopontAdapter extends RecyclerView.Adapter<IdopontAdapter.ViewHolder> implements Filterable {
    private ArrayList<Idopont> mIdopontData;
    private ArrayList<Idopont> mIdopontDataAll;
    private Context mContext;
    private int lastPosition = -1;

    IdopontAdapter(Context context, ArrayList<Idopont> itemsData){
        this.mIdopontData = itemsData;
        this.mIdopontDataAll = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.idopont, parent, false));
    }

    @Override
    public void onBindViewHolder(IdopontAdapter.ViewHolder holder, int position) {
        Idopont currentIdopont = mIdopontData.get(position);
        holder.bindTo(currentIdopont);

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mIdopontData.size();
    }

    private Filter idopontFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Idopont> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mIdopontDataAll.size();
                results.values = mIdopontDataAll;
            }
            else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Idopont idopont : mIdopontDataAll){
                    if(idopont.getEmail().toLowerCase().contains(filterPattern)){
                        filteredList.add(idopont);
                    }
                }

                results.count = mIdopontDataAll.size();
                results.values = mIdopontDataAll;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mIdopontData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return idopontFilter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView email;
        private TextView cim;
        private TextView idopont;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.idoponEmail);
            cim = itemView.findViewById(R.id.idopontCim);
            idopont = itemView.findViewById(R.id.idopontIdo);

            itemView.findViewById(R.id.foglalButton).setOnClickListener(view -> ((FoglalasActivity)mContext).foglal(
                    new Idopont(email.getText().toString(), cim.getText().toString(), Timestamp.valueOf(idopont.getText().toString()))));
        }
        public void bindTo(Idopont currentIdopont) {
            email.setText(currentIdopont.getEmail());
            cim.setText(currentIdopont.getCim());
            idopont.setText(currentIdopont.getIdo().toString());
        }
    }
}
