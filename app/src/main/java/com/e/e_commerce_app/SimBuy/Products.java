package com.e.e_commerce_app.SimBuy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Products extends AppCompatActivity implements InterfaceForCartItems {

    List<ProductSimCardModel> listProductModel;
    RecyclerView recyclerViewForProducts;
    TextView tvNumberOfItems, tvNothingFound;
    ImageButton iBtnGoToCart;
    StoredData storedData;
    LinearLayout llCartIcon;
    // To cancel any type of dialog like loading etc.
    AlertDialog dialogView;
    private final String TAG = "API -> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvNothingFound = findViewById(R.id.tv_nothingFound);
        storedData = StoredData.getInstance(this);
        sendRequestForGetAllProducts();

        tvNumberOfItems = findViewById(R.id.tv_noOfItemInCart);
        iBtnGoToCart = findViewById(R.id.ibtn_cartIcon);

        View.OnClickListener goToCart = v -> {
            if (storedData.getCartItemsList().size() == 0) {
                toast("Add Some items First");
            } else {
                startActivity(new Intent(Products.this, CartSim.class));
            }
        };

        iBtnGoToCart.setOnClickListener(goToCart);

        llCartIcon = findViewById(R.id.ll_cartIcon);
        llCartIcon.setOnClickListener(goToCart);

        // sendSearchRequestForProducts(); // this is not working

        EditText et_search = findViewById(R.id.et_search_number);
        et_search.clearFocus();
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    afterSearchResults(s.toString());
                } catch (Exception e) {
                    Log.e(TAG, "Error: because of empty string in search, no problem!");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void showRecyclerView() {
        recyclerViewForProducts = findViewById(R.id.rv_product);
        ProductAdapterRv adapterRv = new ProductAdapterRv(this, listProductModel, this);
        recyclerViewForProducts.setHasFixedSize(true);
        recyclerViewForProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewForProducts.setAdapter(adapterRv);
    }

    private void afterSearchResults(String number) {

        if (number.equals("")) {
            for (ProductSimCardModel obj : listProductModel) {
                obj.setSearchResult(false);
            }
            showRecyclerView(); // means not searching number
            return;
        }

        if (listProductModel == null) {
            toast("List is null, please check");
            return;
        }

        List<ProductSimCardModel> listToShowInResults = new ArrayList<>();

        for (int i = 0; i < listProductModel.size(); i++) {

            String tempMobile = listProductModel.get(i).getMobile_number();
            String tempProductName = listProductModel.get(i).getProduct_name();

            if (tempMobile.contains(number)) {
                Spannable spannable = new SpannableString(tempProductName);
                int startIndex = tempProductName.indexOf(number);
                int endIndex = startIndex + number.length();
                spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)),
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // to prevent reference
                /*ProductSimCardModel temp = listProductModel.get(i);
                ProductSimCardModel psm = new ProductSimCardModel(
                        temp.getId(),
                        temp.getProduct_name(),
                        temp.getCustomer_price(),
                        temp.getRetail_price(),
                        temp.getDealer_commission(),
                        temp.getStatusStr(),
                        temp.getDescription()
                );*/

                ProductSimCardModel psm = listProductModel.get(i);
                psm.setSpannable(spannable);
                psm.setSearchResult(true);

                listToShowInResults.add(psm);
            }
        }

        if (listProductModel.size() == 0) {
            tvNothingFound.setVisibility(View.VISIBLE);
            recyclerViewForProducts.setVisibility(View.GONE);
        } else {
            tvNothingFound.setVisibility(View.GONE);
            recyclerViewForProducts.setVisibility(View.VISIBLE);
            recyclerViewForProducts.setAdapter(new ProductAdapterRv(
                    this, listToShowInResults, this));
        }


    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // this is called from adapter, when item added to cart or removed
    @Override
    public void updateNumberOfItemsInCart() {
        tvNumberOfItems.setText(StoredData.getInstance(this).getCartItemsList().size() + "");
    }

    private void sendRequestForGetAllProducts() {
        showProgressBar(true, "loading...");

        String url = "https://www.waytopay.in/api_vip/vipnumberlist";

        StringRequest requestForProducts = new StringRequest(Request.Method.POST,
                url, response -> {

            try {
                listProductModel = new ArrayList<>();

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

                        listProductModel.add(simProduct);
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
           /* @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> param = new HashMap<>();

                param.put("id", String.valueOf(userData.getId()));
                param.put("token", String.valueOf(userData.getToken()));
                param.put("number", mobileNumber);
                param.put("provider", operatorModel.getId());
                param.put("amount", amount);

                Log.e("param", "=" + param.toString());
                return param;
            }*/
        };

        RequestHandler.getInstance(this).addToRequestQueue(requestForProducts);
        requestForProducts.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void showProgressBar(boolean showText, String text) {

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

    private void sendSearchRequestForProducts() {
        showProgressBar(true, "loading...");

        String url = "https://www.waytopay.in/api_vip/search_number";

        StringRequest requestForProducts = new StringRequest(Request.Method.POST,
                url, response -> {

            try {
                listProductModel = new ArrayList<>();

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

                        listProductModel.add(simProduct);
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
                param.put("txtVipNumber", "77 000");
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

    private void createFakeProductsForTesting() {

        int sizeOfList = 50;
        listProductModel = new ArrayList<>();

        for (int i = 0; i < sizeOfList; i++) {

            StringBuilder randomPhoneNumber = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                int random = (int) (Math.random() * 9 + 1);
                if (j == 3 || j == 6) randomPhoneNumber.append(" ");
                randomPhoneNumber.append(random);
            }

            double customerPrice = Double.parseDouble(new DecimalFormat("##.##").format((Math.random() * 100 + 1)));
            double retailPrice = Double.parseDouble(new DecimalFormat("##.##").format((Math.random() * 20 + 1)));
            double dealerCommission = Double.parseDouble(new DecimalFormat("##.##").format((Math.random() * 20 + 1)));

            ProductSimCardModel psm = new ProductSimCardModel
                    (String.valueOf(i), randomPhoneNumber.toString(),
                            customerPrice, retailPrice, dealerCommission, "active", "This is description");

            listProductModel.add(psm);
        }

        toast(listProductModel.size() + " Products Generated!");
    }
}