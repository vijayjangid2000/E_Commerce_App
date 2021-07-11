package com.e.e_commerce_app.SimBuy;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProductAdapterRv extends
        RecyclerView.Adapter<ProductAdapterRv.ViewHolder> {

    final String ADD_TO_CART = "Add to cart", REMOVE_FROM_CART = "Remove",
            ITEM_REMOVED = "Item Removed", ADDED_TO_CART = "Added to cart";
    private final Context context;
    private final List<ProductSimCardModel> productsSimList;
    private final StoredData storedData;
    InterfaceForCartItems interfaceForCartItems;
    AlertDialog dialogView;

    String TAG = "API -> ";

    public ProductAdapterRv(Context context,
                            List<ProductSimCardModel> listRequestedSim, InterfaceForCartItems interfaceForCartItems) {
        this.context = context;
        this.productsSimList = listRequestedSim;
        storedData = StoredData.getInstance(context);
        this.interfaceForCartItems = interfaceForCartItems;
    }


    void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ProductAdapterRv.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sim_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapterRv.ViewHolder holder, int position) {
        holder.tvCustomerPrice.setText(productsSimList.get(position).getCustomer_priceStr());
        holder.tvRetailPrice.setText(productsSimList.get(position).getRetail_priceStr());

        if (productsSimList.get(position).isInCart()) {
            holder.btnAddToCart.setColorFilter(context.getColor(R.color.red));
            holder.btnAddToCart.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_remove_from_cart));
            holder.tvAddRemove.setText("Remove");
            holder.tvAddRemove.setTextColor(context.getColor(R.color.red));
        } else {
            holder.btnAddToCart.setColorFilter(context.getColor(R.color.green_add_to_cart));
            holder.btnAddToCart.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_to_cart_plus));
            holder.tvAddRemove.setText("Add Item");
            holder.tvAddRemove.setTextColor(context.getColor(R.color.green_add_to_cart));
        }

        if (productsSimList.get(position).isSearchResult()) {
            holder.tvSimNumber.setText(productsSimList.get(position).getSpannable(), TextView.BufferType.SPANNABLE);
        } else {
            holder.tvSimNumber.setText(productsSimList.get(position).getProduct_name());
        }
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

                if (productsSimList.get(position).isInCart()) {
                    // means removing from cart

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(context.getResources().getColor(R.color.transparent));
                    AlertDialog alertView = builder.create();
                    View myView = LayoutInflater.from(context).
                            inflate(R.layout.alert_add_remove_item, null);
                    alertView.setView(myView);

                    Button btnYes = myView.findViewById(R.id.btnOkay);
                    Button btnNo = myView.findViewById(R.id.btnCancel);
                    TextView tvMessage = myView.findViewById(R.id.tvMessage);

                    btnYes.setOnClickListener(v -> {
                        productsSimList.get(position).setInCart(false);
                        storedData.removeFromOrderList(productsSimList.get(position));
                        btnAddToCart.setColorFilter(context.getColor(R.color.green_add_to_cart));
                        btnAddToCart.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_to_cart_plus));
                        tvAddRemove.setText("Add Item");
                        tvAddRemove.setTextColor(context.getColor(R.color.green_add_to_cart));
                        // this is required on each change in stored data
                        storedData.applyUpdate(context);
                        // this should be after update
                        interfaceForCartItems.updateNumberOfItemsInCart();

                        alertView.cancel();

                    });

                    btnNo.setOnClickListener(v -> alertView.cancel());

                    btnYes.setText("Remove");
                    tvMessage.setText("Remove this item from cart?");
                    alertView.setCancelable(true);
                    alertView.show();


                    // no api for removing of item from cart list

                } else {
                    //means adding to cart

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(context.getResources().getColor(R.color.transparent));
                    AlertDialog alertView = builder.create();
                    View myView = LayoutInflater.from(context).
                            inflate(R.layout.alert_add_remove_item, null);
                    alertView.setView(myView);

                    Button btnYes = myView.findViewById(R.id.btnOkay);
                    Button btnNo = myView.findViewById(R.id.btnCancel);
                    TextView tvMessage = myView.findViewById(R.id.tvMessage);

                    btnYes.setOnClickListener(v -> {

                        productsSimList.get(position).setInCart(true);
                        storedData.addToOrderList(productsSimList.get(position));
                        btnAddToCart.setColorFilter(context.getColor(R.color.red));
                        btnAddToCart.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_remove_from_cart));
                        tvAddRemove.setText("Remove");
                        tvAddRemove.setTextColor(context.getColor(R.color.red));
                        alertView.cancel();

                        // this is required on each change in stored data
                        storedData.applyUpdate(context);
                        // this should be after update
                        interfaceForCartItems.updateNumberOfItemsInCart();
                        sendRequestForAddToCart(productsSimList.get(getAdapterPosition()).getMobile_number());
                    });

                    btnNo.setOnClickListener(v -> alertView.cancel());

                    btnYes.setText("Add");
                    tvMessage.setText("Add this item to cart?");
                    alertView.setCancelable(true);
                    alertView.show();

                }

            };

            btnAddToCart.setOnClickListener(addToCart);
            tvAddRemove.setOnClickListener(addToCart);
        }
    }

    private void sendRequestForAddToCart(String mobileNumber) {

        showProgressBar(true, "loading...");
        String url = "https://www.waytopay.in/api_vip/request_for_number";

        StringRequest requestForProducts = new StringRequest(Request.Method.POST,
                url, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                dialogView.cancel();
                String status = jsonObject.getString("status");

                if (status.equalsIgnoreCase("true")) {
                    toast("Successfully requested");
                } else {
                    // toast("Error: Some problem");
                    toast("Error: Status False");
                    Log.e("Error: ", "Status: False");
                }

            } catch (Exception e) {
                dialogView.cancel();
                toast("Error: " + e.getMessage());
                e.printStackTrace();
            }
            Log.e(TAG, "URL: " + url + "\nResponse: " + response);

        }, error -> {
            dialogView.cancel();
            toast("Error: " + error.getMessage());
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("txtUserID", new SessionManager(context).getUserId());
                param.put("txtMobileNo", mobileNumber);
                Log.e("param", "=" + param.toString());
                return param;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(requestForProducts);
        requestForProducts.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(
                        ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = LayoutInflater.from(context).inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alertBldr_loading = new AlertDialog.Builder(context)
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        Window window = dialogView.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.transparent);
        dialogView.show();
    }


    // this can be used any where, it has text and yes no buttons
    // we have not called this method anywhere, instead used the code inside
    void showAlertForAddRemoveItems(String message, String yes, String no) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(context.getResources().getColor(R.color.transparent));
        AlertDialog alertView = builder.create();
        View myView = LayoutInflater.from(context).
                inflate(R.layout.alert_add_remove_item, null);
        alertView.setView(myView);

        Button btnYes = myView.findViewById(R.id.btnOkay);
        Button btnNo = myView.findViewById(R.id.btnCancel);
        TextView tvMessage = myView.findViewById(R.id.tvMessage);

        btnYes.setOnClickListener(v -> {

            alertView.cancel(); // at last cancel the view
        });

        btnNo.setOnClickListener(v -> alertView.cancel());

        tvMessage.setText("Do you want to Remove this item from cart?");
        alertView.setCancelable(true);
        alertView.show();

    }
}
