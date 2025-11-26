package com.example.gittins_mark_s2429709.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gittins_mark_s2429709.R;
import com.example.gittins_mark_s2429709.model.CurrencyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the RecyclerView in MainActivity
 */
public class RateAdapter  extends RecyclerView.Adapter<RateAdapter.ViewHolder> {
    private Context context;
    // The master list of all currency items provided to the adapter.
    private List<CurrencyItem> items;
    // The filtered list changes based on search queries.
    private List<CurrencyItem> filteredItems;

    public RateAdapter(Context context,List<CurrencyItem> items){
        this.context = context;
        this.items = items;
        // Initialize the filtered list with all items at the beginning.
        this.filteredItems = new ArrayList<>(items);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardRoot;
        TextView currencyTitle;
        TextView currencyRate;
        ImageView flagImage;

        public ViewHolder (View itemView){
            super(itemView);
            // Link layout elements to the ViewHolder properties.
            cardRoot = (CardView) itemView;
            currencyTitle = cardRoot.findViewById(R.id.currencyTitle);
            currencyRate = cardRoot.findViewById(R.id.currencyRate);
            flagImage = cardRoot.findViewById(R.id.flagImage);
        }

    }

    /**
     * Called when the RecyclerView needs a new ViewHolder to represent an item.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rate,parent,false);
        return new ViewHolder(v);
    }

    /**
     * Called by the RecyclerView to display the data at the specified position.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        // Get the specific currency item to display from the filtered list.
        CurrencyItem item = filteredItems.get(position);
        holder.currencyTitle.setText(item.title);
        holder.currencyRate.setText(String.format("%.4f",item.rate));
        holder.flagImage.setImageResource(item.flagId);


        double rate = item.rate;
        int color;

        // set the background color of the card based on the exchange rate value.
        if (rate < 1.0){
            color = ContextCompat.getColor(context,R.color.rate_very_weak);
        }else if (rate < 5.0){
            color = ContextCompat.getColor(context,R.color.rate_weak);
        }else if (rate < 10.0){
            color = ContextCompat.getColor(context,R.color.rate_strong);
        }else{
            color = ContextCompat.getColor(context,R.color.rate_very_strong);
        }
        holder.cardRoot.setCardBackgroundColor(color);

        // Set a click listener on the item view to launch the ConverterActivity.
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConverterActivity.class);
            // Pass the selected currency's rate and code to the converter activity.
            intent.putExtra("rate", item.rate);
            intent.putExtra("code", item.code);
            context.startActivity(intent);
        });

    }

    /**
     * Returns the total number of items in the data set held by the adapter
     */
    @Override
    public int getItemCount(){
        return filteredItems.size();
    }

    /**
     * Updates the adapter's data set with a new list of currency items
     */
    public void setCurrencyList(List<CurrencyItem> newList) {
        this.items.clear();
        this.items.addAll(newList);

        // Also update the filtered list
        this.filteredItems.clear();
        this.filteredItems.addAll(newList);

        // Triggers a redraw of the entire list.
        notifyDataSetChanged();
    }

}
