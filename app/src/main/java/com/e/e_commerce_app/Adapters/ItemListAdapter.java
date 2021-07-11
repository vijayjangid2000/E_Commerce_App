package com.e.e_commerce_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.e_commerce_app.Activitys.ItemDescriptionActivity;
import com.e.e_commerce_app.Model.ItemListModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> implements View.OnClickListener {

    ArrayList<ItemListModel> listItems;
    Context context;

    public ItemListAdapter(Context context, ArrayList<ItemListModel> itemlist, SelectProductListener productListener) {
        this.context = context;
        this.listItems = itemlist;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Picasso.with(context).load(APIs.URL_IMAGE + listItems.get(position).getListimg())
                .error(R.drawable.no_image).into(holder.listimg);
        // holder.listimg.setImageResource(Integer.parseInt(""+ itemlist.get(position).getListimg()));
        holder.pro_Name.setText(listItems.get(position).getPro_Name());
        holder.pro_Price.setText("₹" + listItems.get(position).getPro_Price());
        holder.ID.setText("" + listItems.get(position).getID());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ItemDescriptionActivity.class);
            intent.putExtra("Id", "" + listItems.get(position).getID());
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View view) {

    }

    public void filterList(ArrayList<ItemListModel> filterdNames) {
        this.listItems = filterdNames;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView listimg;
        TextView pro_Name, pro_Price, ID;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listimg = itemView.findViewById(R.id.list_img);
            pro_Name = itemView.findViewById(R.id.pro_name);
            pro_Price = itemView.findViewById(R.id.pro_price);
            ID = itemView.findViewById(R.id.ID);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface SelectProductListener {
        void productListener(String product);
    }


}
