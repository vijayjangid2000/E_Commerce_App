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

import com.e.e_commerce_app.Activitys.ProductsActivity;
import com.e.e_commerce_app.Model.CategoryModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    ArrayList<CategoryModel> listCategory;
    Context context;
    SelectCategoryListener listener;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> catlist, SelectCategoryListener listener) {
        this.context = context;
        this.listCategory = catlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCartItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new MyViewHolder(viewCartItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Picasso.with(context).load(APIs.URL_CATEGORY_IMG + listCategory.get(position)
                .getCatImg()).error(R.drawable.no_image).into(holder.caItemImage);
        // holder.caItemImage.setImageResource(Integer.parseInt(""+catlist.get(position).getCatImg()));
        holder.caItemName.setText(listCategory.get(position).getCatName());
        holder.catId.setText("" + listCategory.get(position).getCatId());


        holder.itemView.setOnClickListener(view -> {

            Intent intent = new Intent(context, ProductsActivity.class);
            intent.putExtra("parsecatId", "" + listCategory.get(position).getCatId());
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView caItemImage;
        TextView caItemName, catId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            caItemImage = itemView.findViewById(R.id.cat_img);
            caItemName = itemView.findViewById(R.id.cat_pro_name);
            catId = itemView.findViewById(R.id.cat_id);


        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface SelectCategoryListener {
        void categorySelected(String category);
    }

}
