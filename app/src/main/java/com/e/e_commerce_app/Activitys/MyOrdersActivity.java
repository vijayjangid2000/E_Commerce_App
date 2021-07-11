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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.Adapters.MyOrderAdapter;
import com.e.e_commerce_app.Model.MyOrderModel;
import com.e.e_commerce_app.Model.Product_Model;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyOrdersActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = OrderProcessActivity.class.getSimpleName();
    private LinearLayout urOrderlayout, orderErrorLyout;
    private TextView tvOrderError, tv;
    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;
    private RecyclerView rvMyOrder;
    private MyOrderAdapter myOrderAdapter;
    private MyOrderAdapter.onClickInterface onclickInterface;
    private ArrayList<MyOrderModel> listMyOrderModel;
    private ListView listViewOrderPro;
    private ArrayList<Product_Model> listProductModel;
    private String send_Temp_status = "", chk = "", temp_order_id = "";
    private int temp_num = 0, temp_badge = 0, status_temp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        sessionManager = new SessionManager(MyOrdersActivity.this);
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        urOrderlayout = findViewById(R.id.urorderlayout);
        orderErrorLyout = findViewById(R.id.ordererror);
        tvOrderError = findViewById(R.id.ordererrormsg);
        rvMyOrder = findViewById(R.id.myorderRecyclelist);
        listViewOrderPro = findViewById(R.id.orderlistview);

        requestGetOrderList();

        Checkout.preload(getApplicationContext());
        onclickInterface = abc -> {
            temp_order_id = listMyOrderModel.get(abc).getOrderid();
            temp_num = abc;
        };
    }

    public void requestGetOrderList() {
        final ProgressDialog progressDialog = new ProgressDialog(MyOrdersActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.e("orderlist_url", "" + APIs.GET_ORDER_LIST);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_ORDER_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" orderlist", "=" + response);

                        progressDialog.dismiss();
                        listMyOrderModel = new ArrayList<MyOrderModel>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("order_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    MyOrderModel myOrderModel = new MyOrderModel();
                                    myOrderModel.setOrderid(jsonObject1.getString("order_id"));
                                    myOrderModel.setOrderstatus(jsonObject1.getString("order_status"));
                                    myOrderModel.setOrdertoal(jsonObject1.getString("grand_total"));
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("order_date");
                                    myOrderModel.setOrderDate("" + jsonObject2.getString("date"));

                                    listProductModel = new ArrayList<Product_Model>();
                                    JSONArray jsonArrayproduct = jsonObject1.getJSONArray("order_products");
                                    for (int j = 0; j < jsonArrayproduct.length(); j++) {

                                        JSONObject jsonObject01 = jsonArrayproduct.getJSONObject(j);
                                        Product_Model product_model = new Product_Model();
                                        product_model.setPro_img(jsonObject01.getString("image"));
                                        product_model.setPro_name(jsonObject01.getString("product_name"));
                                        product_model.setPro_price(jsonObject01.getString("price"));
                                        listProductModel.add(product_model);
                                    }
                                    myOrderModel.setProduct_list(listProductModel);
                                    listMyOrderModel.add(myOrderModel);
                                }
                                myOrderAdapter = new MyOrderAdapter(MyOrdersActivity.this, listMyOrderModel, onclickInterface);
                                temp_num = 1;
                                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyOrdersActivity.this, LinearLayoutManager.VERTICAL, false);
                                rvMyOrder.setHasFixedSize(false);
                                rvMyOrder.setLayoutManager(linearLayoutManager);
                                rvMyOrder.setItemAnimator(new DefaultItemAnimator());
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMyOrder.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                rvMyOrder.addItemDecoration(dividerItemDecoration);
                                rvMyOrder.setAdapter(myOrderAdapter);
                            } else {
                                orderErrorLyout.setVisibility(View.VISIBLE);
                                tvOrderError.setText("" + message);
                                urOrderlayout.setVisibility(View.GONE);
                                Toast.makeText(MyOrdersActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my orderlist", "=1");
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
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void startPayment() {
        final Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.a2zmart);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Food n Joy");
            options.put("description", "Order Id:" + temp_order_id);
            // options.put("order_id", ""+order_id);//from response of step 3.
            options.put("image", "" + R.drawable.a2zmart);
            options.put("theme.color", "#0496AA");
            options.put("currency", "INR");
            options.put("amount", "100");//pass amount in currency subunits
            JSONObject preFill = new JSONObject();
            preFill.put("email", "" + sessionManager.getString("useremail"));
            preFill.put("contact", "" + sessionManager.getString("usermobile"));
            options.put("prefill", preFill);
            checkout.open(MyOrdersActivity.this, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
            Toast.makeText(MyOrdersActivity.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            Toast.makeText(MyOrdersActivity.this, "this", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
        chk = "yes";
        send_Temp_status = "pending";
        Chng_Order_Status();
    }

    @Override
    public void onPaymentError(int i, String s) {
        chk = "no";
        send_Temp_status = "payment pending";
        Chng_Order_Status();

    }

    public void Chng_Order_Status() {
        final ProgressDialog progressDialog = new ProgressDialog(MyOrdersActivity.this);
        progressDialog.setMessage("Loading...");

        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("chng_order_status", "" + APIs.ORDER_STATUS_CHANGE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.ORDER_STATUS_CHANGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(" status_chng", "=" + jsonObject.toString());
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                if (chk.equals("yes")) {
                                    status_temp = 1;
                                    Payment_Status();

                                } else if (chk.equals("no")) {
                                    try {
                                        status_temp = 2;
                                        Payment_Status();
                                    } catch (Exception e) {
                                        Log.e("OnPaymentError", "Exception in onPaymentError", e);
                                        Toast.makeText(MyOrdersActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                                        Log.e("Onpaymenterror", "" + e);
                                    }
                                }
                            } else {
                                Toast.makeText(MyOrdersActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my status", "=1");
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
                params.put("order_id", "" + temp_order_id);
                params.put("order_status", "" + send_Temp_status);
                Log.e("param status", params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void Payment_Status() {
        final BottomSheetDialog ofr_popup = new BottomSheetDialog(MyOrdersActivity.this);
        ofr_popup.setContentView(R.layout.payment_status_popup);
        ofr_popup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ofr_popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ofr_popup.show();
        final TextView payment_status = ofr_popup.findViewById(R.id.paymemt_status);
        Button payment_status_btn = ofr_popup.findViewById(R.id.payment_status_btn);
        TextView payment_status_close = ofr_popup.findViewById(R.id.payment_status_close);

        if (status_temp == 1) {
            payment_status.setText("Payment Complted");
            payment_status_btn.setText("Done");
            listMyOrderModel.get(temp_num).setOrderstatus("pending");
            myOrderAdapter.notifyDataSetChanged();
        } else if (status_temp == 2) {
            payment_status.setText("Transaction Failed");
            payment_status_btn.setText("Retry");
            listMyOrderModel.get(temp_num).setOrderstatus("payment pending");
            myOrderAdapter.notifyDataSetChanged();
        }
        payment_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status_temp == 1) {
                    Intent intent = new Intent(MyOrdersActivity.this, MyOrdersActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (status_temp == 2) {
                    ofr_popup.dismiss();
                    startPayment();
                }
            }
        });
        payment_status_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ofr_popup.dismiss();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (sessionManager.getString("email").equalsIgnoreCase("") || sessionManager.getString("email").isEmpty() && sessionManager.getString("password").equalsIgnoreCase("") || sessionManager.getString("password").isEmpty()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.dashboard_menu2, menu);
            return true;
        } else {
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
                Intent intent = new Intent(MyOrdersActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.cart_icon:
                Intent intent2 = new Intent(MyOrdersActivity.this, MyCartActivity.class);
                startActivity(intent2);
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyOrdersActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }


}
