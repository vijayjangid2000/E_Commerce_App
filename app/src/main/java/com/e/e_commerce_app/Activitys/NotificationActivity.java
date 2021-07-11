package com.e.e_commerce_app.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.Adapters.NotificationAdapter;
import com.e.e_commerce_app.Model.Notification_Model;
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

public class NotificationActivity extends AppCompatActivity {

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    RecyclerView rvNotificationItem;
    NotificationAdapter adapterNotification;
    ArrayList<Notification_Model> listNotification;
    TextView tv;
    int temp_badge = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionManager = new SessionManager(NotificationActivity.this);
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        rvNotificationItem = findViewById(R.id.notirecycle);
        requestGetNotification();
    }

    public void requestGetNotification() {
        final ProgressDialog progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.e("notification_url", "" + APIs.GET_NOTIFICATIONS);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_NOTIFICATIONS,
                response -> {
                    Log.e(" my_notification", "=" + response);
                    listNotification = new ArrayList<>();
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                Notification_Model notification_model = new Notification_Model();
                                if (jsonObject1.getString("subject").equals("") || jsonObject1.getString("subject").equals(null)) {
                                    notification_model.setNotification_Tittle("N/A");
                                } else {
                                    notification_model.setNotification_Tittle(jsonObject1.getString("subject"));
                                }
                                if (jsonObject1.getString("notification_message").equals("") || jsonObject1.getString("notification_message").equals(null)) {
                                    notification_model.setNotification_Message("N/A");
                                } else {
                                    notification_model.setNotification_Message(jsonObject1.getString("notification_message"));
                                }
                                listNotification.add(notification_model);

                            }
                            adapterNotification = new NotificationAdapter(NotificationActivity.this, listNotification);
                            final LinearLayoutManager linearLayoutManager;
                            linearLayoutManager = new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false);
                            rvNotificationItem.setHasFixedSize(false);
                            rvNotificationItem.setLayoutManager(linearLayoutManager);
                            rvNotificationItem.setItemAnimator(new DefaultItemAnimator());
                            rvNotificationItem.setAdapter(adapterNotification);

                        } else {


                            Toast.makeText(NotificationActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Log.e("resp my notification", "=1");
                    }
                },
                error -> progressDialog.dismiss()) {
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
                Intent intent2 = new Intent(NotificationActivity.this, MyCartActivity.class);
                startActivity(intent2);
                return true;

        }

        return true;
    }
}

