package com.e.e_commerce_app.SimBuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.e.e_commerce_app.R;

import java.util.List;

public class CartAdapter2 extends
        RecyclerView.Adapter<CartAdapter2.ViewHolder> {

    final String ADD_TO_CART = "Add to cart", REMOVE_FROM_CART = "Remove",
            ITEM_REMOVED = "Item Removed", ADDED_TO_CART = "Added to cart";
    private final Context context;
    private final List<ProductSimCardModel> productsSimList;
    private final StoredData storedData;
    InterfaceForCartItems myInterface;

    public CartAdapter2(Context context,
                        List<ProductSimCardModel> listRequestedSim, InterfaceForCartItems myInterface) {
        this.context = context;
        this.productsSimList = listRequestedSim;
        storedData = StoredData.getInstance(context);
        this.myInterface = myInterface;
    }


    void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public CartAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sim_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCustomerPrice.setText(productsSimList.get(position).getCustomer_priceStr());
        holder.tvRetailPrice.setText(productsSimList.get(position).getRetail_priceStr());
        holder.tvSimNumber.setText(productsSimList.get(position).getProduct_name());

        holder.btnAddToCart.setColorFilter(context.getColor(R.color.red));
        holder.btnAddToCart.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_remove_from_cart));
        holder.tvAddRemove.setText("Remove");
        holder.tvAddRemove.setTextColor(context.getColor(R.color.red));

    }

    @Override
    public int getItemCount() {
        return productsSimList.size();
    }

    /* VIEW HOLDER CLASS */

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRetailPrice, tvCustomerPrice, tvSimNumber, tvAddRemove;
        ImageButton btnAddToCart;
        View bigView;
        LinearLayout llDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            bigView = itemView.findViewById(R.id.llView);
            tvCustomerPrice = itemView.findViewById(R.id.tv_customePrice);
            tvRetailPrice = itemView.findViewById(R.id.tv_retailPrice);
            tvSimNumber = itemView.findViewById(R.id.tv_number);
            btnAddToCart = itemView.findViewById(R.id.btn_gridAddToCart);
            tvAddRemove = itemView.findViewById(R.id.tv_addRemove);

            View.OnClickListener addToCart = view -> {
                int position = getAdapterPosition();
                toast("No api for removing item from cart");
            };

            btnAddToCart.setOnClickListener(addToCart);
            tvAddRemove.setOnClickListener(addToCart);
        }


    }
}
