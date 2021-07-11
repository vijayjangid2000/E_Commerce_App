package com.e.e_commerce_app.Activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.SimBuy.Products;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.NetworkChecking;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView tvb_goToRegister, tvbGotoForget;
    EditText etLoginEmail, etLoginPassword;
    AlertDialog progressDialog;

    SessionManager sessionManager;

    TextView buySimFeature;
    Boolean isNewNum = false;
    String regexStr = "^[0-9]*$", device_token;
    int skip_number = 0, item_to_login, itemid;
    float fadeValue = 0.4f, brightenValue = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.et_login_email);
        etLoginPassword = findViewById(R.id.et_login_pswrd);
        btnLogin = findViewById(R.id.loginbtn);
        tvb_goToRegister = findViewById(R.id.goregister);
        tvbGotoForget = findViewById(R.id.fogetpasword);

        sessionManager = new SessionManager(LoginActivity.this);
        device_token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();

        if (getIntent().getExtras() != null) {
            skip_number = (getIntent().getExtras().getInt("skip_id"));
            isNewNum = true;
            itemid = (getIntent().getExtras().getInt("Item_to_login"));
            item_to_login = (getIntent().getExtras().getInt("ItemtologNmbr"));
        }

        tvbGotoForget.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> {
            view.setAlpha(fadeValue);

            if (!NetworkChecking.isConnectedNetwork(LoginActivity.this)) {
                //internet is connected do something
                Toast.makeText(LoginActivity.this, "Internet Not Available", Toast.LENGTH_SHORT).show();
                return;
            }

            if (etLoginEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter Email Address Or Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (etLoginEmail.getText().toString().endsWith("@gmail.com") ||
                    etLoginEmail.getText().toString().endsWith("@openinnovationslab.com")) {
                if (!isEmailValid("" + etLoginEmail.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (etLoginEmail.getText().toString().trim().matches(regexStr)) {
                if (etLoginEmail.getText().toString().length() < 10) {
                    Toast.makeText(LoginActivity.this, "Enter 10 digit", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(LoginActivity.this, "Enter valid Email or Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (etLoginPassword.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            sendLoginRequest();

        });

        tvb_goToRegister.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);

        });

        // temporarily
        buySimFeature = findViewById(R.id.new_feature);
        buySimFeature.setOnClickListener(view -> {
            // Buy sim feature
            view.setAlpha(fadeValue);
            startActivity(new Intent(this, Products.class));
            finish();
        });
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void sendLoginRequest() {

        showProgressBar("Please wait");
        Log.e("LOGIN_URL", "" + APIs.LOGIN_URL);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.LOGIN_URL,
                response -> {
                    progressDialog.cancel();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e(" login", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            sessionManager.addString("mobile", "" + etLoginEmail);
                            sessionManager.addString("password", "" + etLoginPassword);
                            sessionManager.addString("email", "" + etLoginEmail.getText().toString());
                            sessionManager.addString("passworde", "" + etLoginPassword.getText().toString());

                            String user_id = jsonObject.getString("user_id");
                            String user_email = jsonObject.getString("email");
                            String user_name = jsonObject.getString("name");
                            String user_mobile = jsonObject.getString("mobile");
                            String cart_total = jsonObject.getString("total_cart_items");

                            sessionManager.addString("userid", "" + user_id);
                            sessionManager.addString("nmbr", "" + "1");
                            sessionManager.addString("username", "" + user_name);
                            sessionManager.addString("useremail", "" + user_email);
                            sessionManager.addString("usermobile", "" + user_mobile);
                            SharedPreferences sp = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString("carttotal", "" + cart_total);
                            ed.apply();

                            //sessionManager.addString("carttotal", ""+cart_total);

                            if (isNewNum) {
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Log.e("token", "" + device_token);

                                Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                                finish();

                            }

                            if (etLoginEmail.getText().toString().endsWith("@gmail.com") ||
                                    etLoginEmail.getText().toString().endsWith("@openinnovationslab.com")) {
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Log.e("token", "" + device_token);
                                Log.e("cartT", "" + cart_total);
                                Log.e("session cart:", "" + sessionManager.getString("carttotal"));
                                startActivity(intent);
                                finish();

                            } else if (etLoginEmail.getText().toString().trim().matches(regexStr)) {
                                Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                                intent.putExtra("mobile", etLoginEmail.getText().toString());
                                intent.putExtra("pswrd", etLoginPassword.getText().toString());
                                Log.e("mobileT", "=" + etLoginEmail.getText());
                                Log.e("token", "" + device_token);
                                startActivity(intent);
                                Log.e("session mobile:", "" + sessionManager.getString("mobile"));
                                Log.e("session pswrd:", "" + sessionManager.getString("password"));
                                Log.e("cartT", "" + cart_total);
                                Log.e("session cart:", "" + sessionManager.getString("carttotal"));
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.cancel();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnLogin.setAlpha(brightenValue);
                        Log.e("resp my login", "=1");
                    }
                },
                error -> {
                    btnLogin.setAlpha(brightenValue);
                    progressDialog.cancel();
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            protected Map<String, String> getParams() {

                HashMap<String, String> params = new HashMap<>();
                if (etLoginEmail.getText().toString().trim().matches(regexStr)) {
                    params.put("mobile", "" + etLoginEmail.getText().toString());
                }
                //if (et_login_email.getText().toString().endsWith("@gmail.com")) {
                if (etLoginEmail.getText().toString().endsWith("@gmail.com") ||
                        etLoginEmail.getText().toString().endsWith("@openinnovationslab.com")) {
                    params.put("email", "" + etLoginEmail.getText().toString());
                }

                params.put("password", "" + etLoginPassword.getText().toString());
                params.put("fcm_token", "" + device_token);

                Log.e("session email1:", "" + sessionManager.getString("email"));
                Log.e("session pswrde1:", "" + sessionManager.getString("passworde"));
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

    private void showProgressBar(String message) {

        if (progressDialog != null) {
            if (!progressDialog.isShowing()) progressDialog.show();
            return;
        }

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);

        if (!message.equals("")) textView.setText(message);
        else textView.setVisibility(View.GONE);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setCancelable(false);
        progressDialog = alertBuilder.create();
        progressDialog.setView(view);
        Window window = progressDialog.getWindow();
        if (window != null) window.setBackgroundDrawableResource(R.color.transparent);
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvb_goToRegister.setAlpha(brightenValue);
        tvbGotoForget.setAlpha(brightenValue);
        btnLogin.setAlpha(brightenValue);
        buySimFeature.setAlpha(brightenValue);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null) progressDialog.cancel();
    }
}
