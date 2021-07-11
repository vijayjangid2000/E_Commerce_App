package com.e.e_commerce_app.Activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.Adapters.CancelOrderProAdapter;
import com.e.e_commerce_app.Model.CancelOrderProductModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CancelOrderActivity extends AppCompatActivity {

    RecyclerView rvCoPro;
    ArrayList<CancelOrderProductModel> listCoPro;
    CancelOrderProAdapter adapterCancelOrderPro;
    Spinner spinner;
    Button btnSubmit;
    Dialog dialogDelete;
    String order_id, order_status;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvCoPro = findViewById(R.id.cancel_orderRecycle);
        if (getIntent().getExtras() != null)
            order_id = (getIntent().getExtras().getString("orderid"));
        spinner = (Spinner) findViewById(R.id.myspinner);
        btnSubmit = findViewById(R.id.btn_submit_request);
        getCancelOrder();
        String[] reasons = new String[]{
                "select cancelation reason...",
                "I have change my mind",
                "Price for the product has decreased",
                "Not intrested",
                "Order By Mistake",
                "other"
        };

        final List<String> reasonList = new ArrayList<>(Arrays.asList(reasons));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, reasonList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint


                if (position > 0) {
                    // Notify the selected item text
                    ((TextView) view).setTextColor(Color.BLACK); //Change selected text color

                    Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (spinner.getSelectedItemPosition() > 0) {
        }

        btnSubmit.setOnClickListener(view -> {
            if (spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(CancelOrderActivity.this, "Select cancellation reason", Toast.LENGTH_SHORT).show();
                return;
            }
            order_status = "cancelled";
            AlertDialog.Builder builder = new AlertDialog.Builder(CancelOrderActivity.this);
            builder.setTitle("Cancel Order");
            builder.setMessage("Are you sure want to delete this order?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Cancel_Order();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();


        });
    }

    public void getCancelOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(CancelOrderActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("cancelorderurl", "" + APIs.GET_CANCEL_ORDER);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_CANCEL_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listCoPro = new ArrayList<>();
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(" getcorder", "=" + jsonObject.toString());
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("order_products");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    CancelOrderProductModel cancelOrderProductModel = new CancelOrderProductModel();
                                    cancelOrderProductModel.setCoPRoImg(jsonObject1.getString("image"));
                                    cancelOrderProductModel.setCoProName(jsonObject1.getString("product_name"));
                                    cancelOrderProductModel.setCoProQyn(jsonObject1.getString("quantity"));
                                    cancelOrderProductModel.setCoProTotal(jsonObject1.getString("price"));
                                    listCoPro.add(cancelOrderProductModel);

                                }
                                adapterCancelOrderPro = new CancelOrderProAdapter(CancelOrderActivity.this, listCoPro);

                                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CancelOrderActivity.this, LinearLayoutManager.VERTICAL, false);
                                rvCoPro.setHasFixedSize(false);
                                rvCoPro.setLayoutManager(linearLayoutManager);
                                rvCoPro.setItemAnimator(new DefaultItemAnimator());
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvCoPro.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                rvCoPro.addItemDecoration(dividerItemDecoration);
                                rvCoPro.setAdapter(adapterCancelOrderPro);
                            } else {
                                Toast.makeText(CancelOrderActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my getcancel order", "=1");
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
                params.put("order_id", "" + order_id);
                Log.e("param cancelOrder", params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void Cancel_Order() {
        final ProgressDialog progressDialog = new ProgressDialog(CancelOrderActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("cancelorder", "" + APIs.ORDER_STATUS_CHANGE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.ORDER_STATUS_CHANGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(" login", "=" + jsonObject.toString());
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                CancelOrderPopUP();
                            } else {
                                Toast.makeText(CancelOrderActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my login", "=1");
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
                params.put("order_id", "" + order_id);
                params.put("order_status", "" + order_status);
                Log.e("param login", params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void CancelOrderPopUP() {
        dialogDelete = new Dialog(CancelOrderActivity.this);
        dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDelete.setCancelable(false);
        dialogDelete.setContentView(R.layout.cnfrm_order_popup);
        TextView cancelmsg = dialogDelete.findViewById(R.id.popup_message);
        Button cbtn = dialogDelete.findViewById(R.id.c_order);


        Window window = dialogDelete.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cancelmsg.setText("Order Cancelled Sucsessfully");
        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CancelOrderActivity.this, MyOrdersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                dialogDelete.dismiss();
            }
        });

        dialogDelete.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();
        return true;

    }
}
