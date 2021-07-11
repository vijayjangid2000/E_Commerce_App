package com.e.e_commerce_app.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.Adapters.WishListAdapter;
import com.e.e_commerce_app.Model.WishListModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WishList_Activity extends AppCompatActivity {
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    TextView wishlisterror;
    LinearLayout wishlistErrorLayout;
    RecyclerView wishlistRecycle;
    WishListAdapter wishListAdapter;
    ArrayList<WishListModel> wishList;
    TextView tv;
    int temp_badge = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list_);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("");

        sessionManager = new SessionManager(WishList_Activity.this);
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        wishlistErrorLayout = findViewById(R.id.wishlisterror);
        wishlisterror = findViewById(R.id.wishlisterrormsg);
        wishlistRecycle = findViewById(R.id.wishlistrecycle);
        getMyWishList();
    }

    public void getMyWishList() {
        final ProgressDialog progressDialog = new ProgressDialog(WishList_Activity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        wishlistRecycle.setVisibility(View.GONE);
        Log.e("mywl_url", "" + APIs.GET_WISH_LIST);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_WISH_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" my_wishlist", "=" + response);
                        wishList = new ArrayList<>();
                        progressDialog.dismiss();
                        wishlistRecycle.setVisibility(View.VISIBLE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {

                                JSONArray productsArray = jsonObject.getJSONArray("data_product");

                                for (int i = 0; i < productsArray.length(); i++) {
                                    JSONObject jsonObject1 = productsArray.getJSONObject(i);
                                    WishListModel wishListModel = new WishListModel();
                                    wishListModel.setWishlist_id(jsonObject1.getString("wishlist_id"));

                                    JSONObject product_details = jsonObject1.getJSONObject("product_details");


                                    wishListModel.setProID(product_details.getString("id"));
                                    wishListModel.setWishListProImage(product_details.getString("image"));
                                    wishListModel.setWishListProName(product_details.getString("p_name"));
                                    wishListModel.setWishListProPrice(product_details.getString("price"));
                                    wishListModel.setWishListProDate("20 November"); //
                                    // wishListModel.setWishListProDate(product_details.getString("updated_at"));

                                    wishList.add(wishListModel);

                                }


                                wishListAdapter = new WishListAdapter(WishList_Activity.this, wishList);
                                final LinearLayoutManager manager;
                                manager = new LinearLayoutManager(WishList_Activity.this, LinearLayoutManager.VERTICAL, false);
                                wishlistRecycle.setHasFixedSize(false);
                                wishlistRecycle.setLayoutManager(manager);
                                wishlistRecycle.setItemAnimator(new DefaultItemAnimator());
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(wishlistRecycle.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                wishlistRecycle.addItemDecoration(dividerItemDecoration);
                                wishlistRecycle.setAdapter(wishListAdapter);


                            } else {
                                wishlisterror.setText("" + message);
                                wishlistErrorLayout.setVisibility(View.VISIBLE);
                                wishlistRecycle.setVisibility(View.GONE);
                                Toast.makeText(WishList_Activity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my wishdata", "=1");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                    }
                }) {
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", "" + sessionManager.getString("userid"));
                Log.e("params", "=" + params.toString());

                return params;
            }

        };
        RequestHandler.getInstance(this).

                addToRequestQueue(request);
        request.setRetryPolicy(new

                DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflater.inflate(R.menu.cart_menu, menu);
        final MenuItem item = menu.findItem(R.id.cart_icon);
        MenuItemCompat.setActionView(item, R.layout.cart_count_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText("" + sharedPreferences.getString("carttotal", null));
        setupBadge();
        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setupBadge() {
        temp_badge = Integer.parseInt(tv.getText().toString());
        if (tv != null) {
            if (tv.getText().equals("0") || tv.getText().equals("null") || temp_badge < 0) {
                if (tv.getVisibility() != View.GONE) {
                    tv.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.cart_icon:
                Intent intent2 = new Intent(WishList_Activity.this, MyCartActivity.class);
                startActivity(intent2);
                return true;
        }
        return true;
    }
}
