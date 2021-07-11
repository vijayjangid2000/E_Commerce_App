package com.e.e_commerce_app.Activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.Model.ProfileAdressModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;

    CircleImageView ivProUserImg;
    TextView tvProUserName, tvProUserEmail;
    String mobileNumber;
    RelativeLayout rlHomeLayout, rlCategoryLayout, rlMyOrderLayout, rlLogLayout, rlRewardLayout,
            rlOfferLayout, rlChangePassword, rlWishlistLayout, rlHelpLayout;
    Dialog dialogConfirmPassword;
    EditText etPrePassword, etNewPassword, etConfirmPassword;
    TextView tv;
    LinearLayout llProfile_main_layout, llProfile_error;

    int temp_badge = 0;
    float fadeValue = 0.4f, brightenValue = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(ProfileActivity.this);
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_profile);

        ivProUserImg = findViewById(R.id.proimg);
        tvProUserName = findViewById(R.id.prousername);
        tvProUserEmail = findViewById(R.id.prouseremail);
        rlHomeLayout = findViewById(R.id.homelayout);
        rlCategoryLayout = findViewById(R.id.categorylayout);
        rlMyOrderLayout = findViewById(R.id.myodrlayout);
        rlLogLayout = findViewById(R.id.loglayout);
        rlRewardLayout = findViewById(R.id.rewardlay);
        rlOfferLayout = findViewById(R.id.offerlayout);
        rlChangePassword = findViewById(R.id.chng_pswrd_layout);
        rlWishlistLayout = findViewById(R.id.wishlist_layout);
        rlHelpLayout = findViewById(R.id.helplayout);
        llProfile_main_layout = findViewById(R.id.main_profile_layout);
        llProfile_error = findViewById(R.id.profile_error);
        requestGetProfile();

        rlHomeLayout.setOnClickListener(view -> {
            view.setAlpha(fadeValue);

            if (mobileNumber == null) mobileNumber = "N/A";

            Intent intent = new Intent(ProfileActivity.this, YourProfileActivity.class);
            intent.putExtra("username", "" + tvProUserName.getText().toString());
            intent.putExtra("useremail", "" + tvProUserEmail.getText().toString());
            intent.putExtra("usermobile", "" + mobileNumber.toString());
            startActivity(intent);
        });

        rlCategoryLayout.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(ProfileActivity.this, CategoriesActivity.class);
            startActivity(intent);
        });

        rlMyOrderLayout.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(ProfileActivity.this, MyOrdersActivity.class);
            startActivity(intent);
        });

        rlRewardLayout.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(ProfileActivity.this, RewardActivity.class);
            startActivity(intent);
        });

        rlOfferLayout.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(ProfileActivity.this, Offer_Activity.class);
            startActivity(intent);
        });

        rlWishlistLayout.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(ProfileActivity.this, WishList_Activity.class);
            startActivity(intent);
        });

        rlHelpLayout.setOnClickListener(view -> {
            view.setAlpha(fadeValue);
            Intent intent = new Intent(ProfileActivity.this, HelpActivity.class);
            if (mobileNumber == null) mobileNumber = "N/A";
            String temp = tvProUserEmail.getText().toString();
            intent.putExtra("hc_name", "" + temp);
            intent.putExtra("hc_contact", "" + mobileNumber);
            startActivity(intent);
        });

        rlChangePassword.setOnClickListener(view -> ConfirmPassword());

        rlLogLayout.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
            dialog.setTitle("Logout");
            dialog.setMessage("Do you want to logout?");
            dialog.setNegativeButton("no", (dialog12, which) -> dialog12.dismiss());
            dialog.setPositiveButton("logout", (dialog1, which) -> requestLogout());
            dialog.show();
        });
    }

    public void requestLogout() {
        llProfile_main_layout.setVisibility(View.GONE);
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("logout", "" + APIs.LOGOUT);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.LOGOUT,
                response -> {
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e(" logout", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            sessionManager.clear();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Toast.makeText(ProfileActivity.this, "logout", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        llProfile_main_layout.setVisibility(View.GONE);
                        llProfile_error.setVisibility(View.VISIBLE);
                        Log.e("resp my logout", "=1");
                    }


                },
                error -> progressDialog.dismiss()) {
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", "" + sessionManager.getString("userid"));

                Log.e("param logout", params.toString());
                return params;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void ConfirmPassword() {
        dialogConfirmPassword = new Dialog(ProfileActivity.this);
        dialogConfirmPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogConfirmPassword.setCancelable(true);

        dialogConfirmPassword.setContentView(R.layout.chng_pswrd_popup);
        etPrePassword = dialogConfirmPassword.findViewById(R.id.prepswrd);
        etNewPassword = dialogConfirmPassword.findViewById(R.id.newpswd);
        etConfirmPassword = dialogConfirmPassword.findViewById(R.id.newpswdcnfrm);
        TextView Cancelbtn = dialogConfirmPassword.findViewById(R.id.cnclbtn);
        TextView Okbtn = dialogConfirmPassword.findViewById(R.id.okbtn);

        Window window = dialogConfirmPassword.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Cancelbtn.setOnClickListener(v -> dialogConfirmPassword.dismiss());

        Okbtn.setOnClickListener(v -> {
            if (etPrePassword.getText().toString().equals("")) {
                Toast.makeText(ProfileActivity.this, "Enter old Password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etNewPassword.getText().toString().equals("")) {
                Toast.makeText(ProfileActivity.this, "Enter New Password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etConfirmPassword.getText().toString().equals("")) {
                Toast.makeText(ProfileActivity.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                return;
            } else if (!etConfirmPassword.getText().toString().equals(etNewPassword.getText().toString())) {
                Toast.makeText(ProfileActivity.this, "Password not matching", Toast.LENGTH_SHORT).show();
                return;
            }
            requestUpdatePassword();
        });
        dialogConfirmPassword.show();

    }

    public void requestGetProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("profileInfo_URL", "" + APIs.GET_PROFILE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_PROFILE,
                response -> {
                    ArrayList<ProfileAdressModel> adrslist = new ArrayList<>();
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e(" getProfile", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            if (jsonObject.getString("image").equals("null") || jsonObject.getString("image").equals("")) {
                                ivProUserImg.setImageResource(R.drawable.no_image);

                            } else {
                                Picasso.with(ProfileActivity.this).load("http://foodnjoy.tk/" + jsonObject.getString("image")).error(R.drawable.no_image).into(ivProUserImg);
                            }


                            if (jsonObject.getString("name").equals("null") || jsonObject.getString("name").equals("")) {
                                tvProUserName.setText("N/A");
                            } else {
                                tvProUserName.setText(jsonObject.getString("name"));

                            }
                            if (jsonObject.getString("email").equals("null") || jsonObject.getString("email").equals("")) {
                                tvProUserEmail.setText("N/A");
                            } else {
                                tvProUserEmail.setText(jsonObject.getString("email"));

                            }
                            if (jsonObject.getString("mobile").equals("null") || jsonObject.getString("mobile").equals("")) {
                                mobileNumber = "N/A";
                            } else {
                                mobileNumber = (jsonObject.getString("mobile"));
                            }

                        } else {

                            Toast.makeText(ProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Log.e("resp my profileInfo", "=1");
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
                Log.e("param proInfo", params.toString());
                return params;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void requestUpdatePassword() {

        String userid = sessionManager.getString("userid");
        String prepswrd = String.valueOf(etPrePassword.getText());
        String newpswrd = String.valueOf(etNewPassword.getText());
        String cnfrmpswd = String.valueOf(etConfirmPassword.getText());

        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("Update_pswrd", "" + APIs.UPDATE_PASSWORD + "user_id=" + userid + "&password=" + prepswrd + "&newPassword=" + newpswrd + "&newPassword_confirmation=" + cnfrmpswd);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.UPDATE_PASSWORD + "user_id=" + userid + "&password=" + prepswrd + "&newPassword=" + newpswrd + "&newPassword_confirmation=" + cnfrmpswd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(" updatepssword", "=" + jsonObject.toString());
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equalsIgnoreCase("1")) {

                                Toast.makeText(ProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                                dialogConfirmPassword.dismiss();

                            } else {
                                Toast.makeText(ProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my uppswrd", "=1");
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


                Log.e("param updatepswrd", params.toString());
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
                Intent intent2 = new Intent(ProfileActivity.this, MyCartActivity.class);
                startActivity(intent2);
                return true;
        }
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rlHomeLayout.setAlpha(brightenValue);
        rlCategoryLayout.setAlpha(brightenValue);
        rlMyOrderLayout.setAlpha(brightenValue);
        rlRewardLayout.setAlpha(brightenValue);
        rlOfferLayout.setAlpha(brightenValue);
        rlWishlistLayout.setAlpha(brightenValue);
        rlHelpLayout.setAlpha(brightenValue);
    }
}
