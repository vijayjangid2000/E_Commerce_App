package com.e.e_commerce_app.SimBuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.e.e_commerce_app.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/* THIS IS NOT USED ANYWHERE */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    final String ADD_TO_CART = "Add to cart", REMOVE_FROM_CART = "Remove from cart",
            ITEM_REMOVED = "Item Removed", ADDED_TO_CART = "Added to cart";
    private final Context context;
    private final List<ProductSimCardModel> cartItemList;
    private final StoredData storedData;

    public CartAdapter(Context context, List<ProductSimCardModel> cartItemsList) {
        this.context = context;
        this.cartItemList = cartItemsList;
        storedData = StoredData.getInstance(context);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(context)
                .inflate(R.layout.list_cart_sim, parent, false);
        return new ViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {

        ProductSimCardModel product = cartItemList.get(position);

        holder.tvCustomerPrice.setText(product.getCustomer_priceStr());
        holder.tvRetailPrice.setText(product.getRetail_priceStr());
        holder.tvSimNumber.setText(product.getProduct_name());

        if (product.isInCart()) {
            holder.btnAddToCart.setText(REMOVE_FROM_CART);
        }

        holder.tvPriority.setText(product.getPriority() + "");

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRetailPrice, tvCustomerPrice, tvSimNumber, tvPriority;
        ImageButton iBtnIncreasePriority, iBtnDecreasePriority;
        Button btnAddToCart;
        View bigView;

        public ViewHolder(View itemView) {
            super(itemView);
            bigView = itemView.findViewById(R.id.llView);
            tvCustomerPrice = itemView.findViewById(R.id.tv_customePrice);
            tvRetailPrice = itemView.findViewById(R.id.tv_retailPrice);
            tvSimNumber = itemView.findViewById(R.id.tv_number);
            btnAddToCart = itemView.findViewById(R.id.btn_gridAddToCart);
            iBtnIncreasePriority = itemView.findViewById(R.id.imgbtn_increasePriority);
            iBtnDecreasePriority = itemView.findViewById(R.id.imgbtn_decreasePriority);
            tvPriority = itemView.findViewById(R.id.tv_priority);

            iBtnDecreasePriority.setOnClickListener(view -> {
                int tempPriority = cartItemList.get(getAdapterPosition()).getPriority();
                if (tempPriority == 1) toast("1 is Minimum Priority");
                else tempPriority--;
                cartItemList.get(getAdapterPosition()).setPriority(tempPriority);
                tvPriority.setText(String.valueOf(tempPriority));
                storedData.applyUpdate(context); // v. imp
            });

            iBtnIncreasePriority.setOnClickListener(view -> {
                int tempPriority = cartItemList.get(getAdapterPosition()).getPriority();
                if (tempPriority == 10) toast("10 is Maximum Priority");
                else tempPriority++;
                cartItemList.get(getAdapterPosition()).setPriority(tempPriority);
                tvPriority.setText(String.valueOf(tempPriority));
                storedData.applyUpdate(context); // v. imp
            });

            btnAddToCart.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (cartItemList.get(position).isInCart()) {
                    // means removing from cart
                    showAlertRemoveFromCart(position);
                }

                // this is required on each change in stored data
                storedData.applyUpdate(context);
            });
        }
    }

    void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    void showAlertRemoveFromCart(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Remove Item", (dialog, which) -> {
            notifyItemRemoved(position);
            cartItemList.get(position).setInCart(false);
            storedData.removeFromOrderList(cartItemList.get(position));
            storedData.applyUpdate(context);
            toast("Item Removed");
        });

        builder.setTitle("Remove from Cart?");

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
}
