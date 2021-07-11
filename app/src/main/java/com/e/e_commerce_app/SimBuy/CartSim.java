package com.e.e_commerce_app.SimBuy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CartSim extends AppCompatActivity implements InterfaceForCartItems {

    RecyclerView rvCart;
    StoredData storedData;
    Button btnCheckOut;
    TextView tvNothingFound;
    AlertDialog dialogView;
    List<ProductSimCardModel> listProductsInCart;

    String TAG = "Requests";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_sim);

        storedData = StoredData.getInstance();
        tvNothingFound = findViewById(R.id.tv_nothingFound);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sendForProductsInCartOrRequested();

    }

    private void showRecyclerView() {
        rvCart = findViewById(R.id.rv_cartSim);

        if (listProductsInCart.size() == 0) {
            toast("No Items found In cart!");
            tvNothingFound.setVisibility(View.GONE);
        } else rvCart.setVisibility(View.VISIBLE);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setHasFixedSize(true);
        rvCart.setAdapter(new CartAdapter2(this, listProductsInCart,
                this));
    }

    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateNumberOfItemsInCart() {
        tvNothingFound.setVisibility(View.VISIBLE);
        rvCart.setVisibility(View.GONE);
    }

    private void sendForProductsInCartOrRequested() {
        showProgressBar(true, "loading...");

        String url = "https://www.waytopay.in/api_vip/vipnumberlist";

        StringRequest requestForProducts = new StringRequest(Request.Method.POST,
                url, response -> {

            try {
                listProductsInCart = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(response);
                dialogView.cancel();
                String status = jsonObject.getString("status");

                if (status.equalsIgnoreCase("true")) {
                    JSONObject jsonResult = jsonObject.getJSONObject("result");
                    String token = jsonResult.getString("token");

                    JSONArray jsonArray = jsonResult.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject productObj = jsonArray.getJSONObject(i);

                        ProductSimCardModel simProduct = new ProductSimCardModel(
                                productObj.getString("id"),
                                productObj.getString("product_name"),
                                productObj.getDouble("customer_price"),
                                productObj.getDouble("retail_price"),
                                productObj.getDouble("dealer_commission"),
                                productObj.getString("status"),
                                productObj.getString("description")
                        );

                        listProductsInCart.add(simProduct);
                    }

                    showRecyclerView();

                } else {
                    toast("Error: Try again later");
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

                param.put("txtUserID", new SessionManager(CartSim.this).getUserId());
                param.put("status", "requested");

                /* status can be like this
                 * requested, active, sale, approved, rejected/reject */

                Log.e("param", "=" + param.toString());
                return param;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(requestForProducts);
        requestForProducts.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alertBldr_loading = new AlertDialog.Builder(this)
                .setCancelable(false);
        dialogView = alertBldr_loading.create();
        dialogView.setView(view);
        Window window = dialogView.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.transparent);
        dialogView.show();
    }

   /* private void notUseful() {
        showProgressBar(true, "loading...");

        String url = "https://www.waytopay.in/api_vip/request_for_number";

        StringRequest requestForProducts = new StringRequest(Request.Method.POST,
                url, response -> {

            try {
                listProductsInCart = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(response);
                dialogView.cancel();
                String status = jsonObject.getString("status");


                if (status.equalsIgnoreCase("true")) {
                    JSONObject jsonResult = jsonObject.getJSONObject("result");
                    String token = jsonResult.getString("token");

                    JSONArray jsonArray = jsonResult.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject productObj = jsonArray.getJSONObject(i);

                        ProductSimCardModel simProduct = new ProductSimCardModel(
                                productObj.getString("id"),
                                productObj.getString("product_name"),
                                productObj.getDouble("customer_price"),
                                productObj.getDouble("retail_price"),
                                productObj.getDouble("dealer_commission"),
                                productObj.getString("status"),
                                productObj.getString("description")
                        );

                        listProductsInCart.add(simProduct);
                    }

                    showRecyclerView();

                } else {
                    toast("Error: Try again later");
                    Log.e("Error: ", "Status: False");
                }

            } catch (Exception e) {
                dialogView.cancel();
                toast("Error: " + e.getMessage());
                e.printStackTrace();
            }


        }, error -> {
            dialogView.cancel();
            toast("Error: " + error.getMessage());
            error.printStackTrace();
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> param = new HashMap<>();

                param.put("txtUserID", "7"); // where is userId
                param.put("txtFilter", "approved");
                Log.e("param", "=" + param.toString());
                return param;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(requestForProducts);
        requestForProducts.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }*/
}