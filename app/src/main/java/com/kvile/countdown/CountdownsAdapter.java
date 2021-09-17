package com.kvile.countdown;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CountdownsAdapter extends RecyclerView.Adapter<CountdownsAdapter.ViewHolder> {

    private static ClickListener clickListener;
    private final Context context;
    private final List<Countdown> mCountdowns;

    public CountdownsAdapter(Context mContext, List<Countdown> countdowns) {
        context = mContext;
        mCountdowns = countdowns;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Countdown countdown = mCountdowns.get(position);

        TextView textView = holder.nameTextView;
        textView.setText(countdown.getName());

        TextView dateView = holder.dateTextView;
        dateView.setText(String.valueOf(countdown.getDaysRemaining()));

        if (countdown.getDaysRemaining() <= 0) {
            textView.setTextColor(Color.GREEN);
            dateView.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return mCountdowns.size();
    }

    public List<Countdown> getCountdowns() {
        return mCountdowns;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        CountdownsAdapter.clickListener = clickListener;
    }

    public void addCountdown(Countdown mCountdown) {
        this.mCountdowns.add(mCountdown);
    }

    public void editCountdown(Countdown mCountdown, int position) {
        Countdown countdown = this.mCountdowns.get(position);
        countdown.setName(mCountdown.getName());
        countdown.setCalendar(mCountdown.getCalendar());
    }

    public void deleteCountdown(int position) {
        this.mCountdowns.remove(position);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;
        public TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.countdown_name);
            dateTextView = itemView.findViewById(R.id.countdown_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getBindingAdapterPosition(), v);
        }
    }
}
