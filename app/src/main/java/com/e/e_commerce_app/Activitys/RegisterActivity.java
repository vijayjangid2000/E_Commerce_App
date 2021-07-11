package com.e.e_commerce_app.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    TextView tvbBackToLogin;
    EditText etName, etMobileNumber, etEmail, etPassword, etConfirmPassword;
    String deviceToken;
    AlertDialog progressDialog;

    float fadeValue = 0.4f, brightenValue = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.rgisterbtn);
        etName = findViewById(R.id.et_register_name);
        etMobileNumber = findViewById(R.id.et_register_monilenmbr);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_pswrd);
        etConfirmPassword = findViewById(R.id.et_register_cnfrm_pswrd);

        tvbBackToLogin = findViewById(R.id.backlogin);
        deviceToken = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();


        btnRegister.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            if (etName.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etMobileNumber.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etMobileNumber.getText().toString().length() < 10) {
                Toast.makeText(RegisterActivity.this, "Enter 10 Digit Number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etEmail.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Email Adress", Toast.LENGTH_SHORT).show();
                return;
            } else if (!isEmailValid("" + etEmail.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etPassword.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etConfirmPassword.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Confirm Password ", Toast.LENGTH_SHORT).show();
                return;
            } else if (!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "Password not matching", Toast.LENGTH_SHORT).show();
                return;
            }

            Register();
        });
        tvbBackToLogin.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void Register() {
        showProgressBar("Please wait");
        Log.e("REGISTER_URL", "" + APIs.REGISTER_URL);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(" login", "=" + jsonObject.toString());
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Log.e("token", "=" + deviceToken);
                                Toast.makeText(RegisterActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                            Log.e("resp my register", "=1");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();

                    }
                }) {
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", etName.getText().toString());
                params.put("email", "" + etEmail.getText().toString());
                params.put("password", "" + etPassword.getText().toString());
                params.put("mobile", "" + etMobileNumber.getText().toString());
                params.put("confirm_password", "" + etConfirmPassword.getText().toString());
                params.put("fcm_token", "" + deviceToken);
                Log.e("param register", params.toString());
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
        tvbBackToLogin.setAlpha(brightenValue);
        btnRegister.setAlpha(brightenValue);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog != null) progressDialog.cancel();
    }
}
