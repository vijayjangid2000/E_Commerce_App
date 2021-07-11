package com.e.e_commerce_app.Activitys;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.Adapters.AdressAdapter;
import com.e.e_commerce_app.Model.AdressModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.e.e_commerce_app.Utility.Utils;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    SessionManager sessionManager;

    LinearLayout lLEditProData, lLAddAddress;
    EditText etUserName, etUserEmail, etUserMobile;
    ImageView ivAddAddress, ivUserImage, ivChangeImageIcon;
    RecyclerView rvAddress;
    Button btnUpdate;

    AdressAdapter adapterAddress;
    ArrayList<AdressModel> listAddress;
    ArrayList<String> listCountry, stateList, cityList;
    JSONArray jsonArray = new JSONArray();
    Bitmap bitmapImage;
    AlertDialog progressDialog;

    int chooseType;
    float fadeValue = 0.4f, brightenValue = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Edit profile");

        sessionManager = new SessionManager(EditProfileActivity.this);
        lLEditProData = findViewById(R.id.editProLayout);
        rvAddress = findViewById(R.id.adreslist);
        ivUserImage = findViewById(R.id.ep_userimage);
        ivChangeImageIcon = findViewById(R.id.et_chngimg);
        ivAddAddress = findViewById(R.id.addAdrs);
        etUserName = findViewById(R.id.username);
        etUserEmail = findViewById(R.id.useremail);
        etUserMobile = findViewById(R.id.userphone);
        btnUpdate = findViewById(R.id.updatebtn);
        lLAddAddress = findViewById(R.id.add_adrsLayout);

        listAddress = new ArrayList<>();
        listCountry = new ArrayList<>();
        stateList = new ArrayList<>();
        cityList = new ArrayList<>();

        //cntryList.add("choose country");
//        stateList.add("choose first country");
//        cityList.add("choose firts state");
        getCountry();


        final AdressModel adressModel2 = new AdressModel();
        adressModel2.setAdrs("");
        adressModel2.setCountrylist(listCountry);
        adressModel2.setStatelist(stateList);
        adressModel2.setCitylist(cityList);
        adressModel2.setPincode("");

        ivAddAddress.setOnClickListener(view -> {
            if (adapterAddress.getItemCount() < 3) {
                listAddress.add(adressModel2);
                adapterAddress.notifyDataSetChanged();
            } else {
                Toast.makeText(EditProfileActivity.this, "You can add only three address",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ivChangeImageIcon.setOnClickListener(view -> SelectImage());

        btnUpdate.setOnClickListener(view -> {
            if (etUserEmail.getText().length() > 0) {
                if (!validEmail("" + etUserEmail.getText().toString())) {

                    Toast.makeText(EditProfileActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (etUserMobile.getText().length() > 0) {

                if (etUserMobile.getText().toString().trim().length() != 10) {
                    Toast.makeText(EditProfileActivity.this, "Enter 10 Digit number", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            for (int i = 0; i < listAddress.size(); i++) {
                String adrs_id = listAddress.get(i).getAdd_id();
                EditText etadrs = rvAddress.getChildAt(i).findViewById(R.id.useradres);

                TextView etcntry = rvAddress.getChildAt(i).findViewById(R.id.usercountry);
                // String cntryname = adrslist.get(i).getCountrylist().get(etcntry.getSelectedItemPosition()).toString();

                TextView etstate = rvAddress.getChildAt(i).findViewById(R.id.userstate);
                //String statename = adrslist.get(i).getStatelist().get(etstate.getSelectedItemPosition()).toString();


                TextView etcity = rvAddress.getChildAt(i).findViewById(R.id.usercity);
                // String cityname = adrslist.get(i).getCitylist().get(etcity.getSelectedItemPosition()).toString();


                EditText etpincode = rvAddress.getChildAt(i).findViewById(R.id.userpincode);
                if (etadrs.getText().length() < 0) {
                    Toast.makeText(EditProfileActivity.this, " enter adress ", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (etpincode.getText().length() < 0) {
                    Toast.makeText(EditProfileActivity.this, " enter pin code ", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject obj = new JSONObject();
                try {
                    obj.put("address", "" + etadrs.getText());
                    obj.put("address_id", "" + adrs_id);
                    obj.put("city", "" + etcity.getText().toString());
                    obj.put("state", "" + etstate.getText().toString());
                    obj.put("country", "" + etcntry.getText().toString());
                    obj.put("pincode", "" + etpincode.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(obj);

            }
            updateProfile();
            sessionManager.addString("nmbr", "" + "2");
            sessionManager.addString("username", "" + etUserName.getText().toString());
        });
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void getCountry() {
        showProgressBar("Loading");
        Log.e("cntryurl", "" + APIs.GET_COUNTRY);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_COUNTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" cntrys", "=" + response);
                        progressDialog.dismiss();
                        String Resourc_Name;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("country");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Resourc_Name = jsonObject1.getString("country_name");
                                    listCountry.add(Resourc_Name);
                                }

                            } else {
                                Toast.makeText(EditProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                            getProfile();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my cntrys", "=1");
                        }
                    }
                },
                error -> progressDialog.dismiss()) {
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
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

    public void getProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("profileInfo_URL", "" + APIs.GET_PROFILE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_PROFILE,

                response -> {
                    listAddress = new ArrayList<>();
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e(" getProfile", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            if (jsonObject.getString("image").equals("null") || jsonObject.getString("image").equals("")) {
                                ivUserImage.setImageResource(R.drawable.no_image);

                            } else {
                                Picasso.with(EditProfileActivity.this).load("http://foodnjoy.tk/" + jsonObject.getString("image")).error(R.drawable.no_image).into(ivUserImage);
                            }
                            if (jsonObject.getString("name").equals("null") || jsonObject.getString("name").equals("")) {
                                etUserName.setText("N/A");
                            } else {
                                etUserName.setText(jsonObject.getString("name"));

                            }
                            if (jsonObject.getString("email").equals("null") || jsonObject.getString("email").equals("")) {
                                etUserEmail.setText("N/A");
                            } else {
                                etUserEmail.setText(jsonObject.getString("email"));

                            }
                            if (jsonObject.getString("mobile").equals("null") || jsonObject.getString("mobile").equals("")) {
                                etUserMobile.setText("N/A");
                            } else {
                                etUserMobile.setText(jsonObject.getString("mobile"));
                            }
                            JSONArray jsonArray = jsonObject.getJSONArray("user_address");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonArray.length() >= 3) {
                                    lLAddAddress.setVisibility(View.GONE);
                                }

                                AdressModel adressModel1 = new AdressModel();
                                if (jsonObject1.getString("address_id").equals(null) || jsonObject1.getString("address_id").equals("")) {
                                    adressModel1.setAdd_id("0");
                                } else {
                                    adressModel1.setAdd_id(jsonObject1.getString("address_id"));

                                }
                                if (jsonObject1.getString("address").equals(null) || jsonObject1.getString("address").equals("")) {
                                    adressModel1.setAdrs("N/A");
                                } else {
                                    adressModel1.setAdrs(jsonObject1.getString("address"));
                                }
                                if (jsonObject1.getString("pincode").equals(null) || jsonObject1.getString("pincode").equals("")) {
                                    adressModel1.setPincode("N/A");
                                } else {
                                    adressModel1.setPincode(jsonObject1.getString("pincode"));
                                }


                                adressModel1.setCountrylist(listCountry);
                                adressModel1.setStatelist(stateList);
                                adressModel1.setCitylist(cityList);
                                if (jsonObject1.getString("country").equals(null) || jsonObject1.getString("country").equals("")) {
                                    adressModel1.setCountry("N/A");
                                } else {
                                    adressModel1.setCountry(jsonObject1.getString("country"));
                                }
                                if (jsonObject1.getString("state").equals(null) || jsonObject1.getString("state").equals("")) {
                                    adressModel1.setState("N/A");
                                } else {
                                    adressModel1.setState(jsonObject1.getString("state"));
                                }
                                if (jsonObject1.getString("city").equals(null) || jsonObject1.getString("city").equals("")) {
                                    adressModel1.setCity("N/A");
                                } else {
                                    adressModel1.setCity(jsonObject1.getString("city"));
                                }
                                listAddress.add(adressModel1);
                            }
                            adapterAddress = new AdressAdapter(EditProfileActivity.this, listAddress);
                            final LinearLayoutManager linearLayoutManager;
                            linearLayoutManager = new LinearLayoutManager(EditProfileActivity.this, LinearLayoutManager.VERTICAL, false);
                            rvAddress.setHasFixedSize(false);
                            rvAddress.setLayoutManager(linearLayoutManager);
                            rvAddress.setItemAnimator(new DefaultItemAnimator());
                            rvAddress.setAdapter(adapterAddress);
                        } else {

                            Toast.makeText(EditProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
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

    public void updateProfile() {

        final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("updateprofile_URL", "" + APIs.UPDATE_PROFILE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.UPDATE_PROFILE,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(" updateprofile", "=" + jsonObject.toString());
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {

                                Toast.makeText(EditProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my updatepro", "=1");
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
                params.put("name", "" + etUserName.getText().toString());
                params.put("email", "" + etUserEmail.getText().toString());
                params.put("mobile", "" + etUserMobile.getText().toString());
                params.put("user_address", jsonArray.toString());
                String string_image = "";
                if (bitmapImage != null)
                    string_image = Utils.getStringImage(bitmapImage);
                params.put("profile_picture", "" + string_image);


                Log.e("param updatepro", params.toString());
                return params;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image!");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {

                    chooseProfilePicFromCamera();

                } else if (items[i].equals("Gallery")) {

                    chooseProfilePicFromGallery();
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    private static final String IMAGE_DIRECTORY_NAME = "";
    private Uri imageUri, outputUri;

    //private Uri imageUri = Utils.getOutputMediaFileUri(EditProfileActivity.this, IMAGE_DIRECTORY_NAME), outputUri;

    private void chooseProfilePicFromCamera() {
        chooseType = 1;

        if (ActivityCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }//if*/

      /*  Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = getOutputMediaFileUri();
        Log.e("result", "image==========="+imageUri);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, 100);*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Utils.getOutputMediaFileUri(EditProfileActivity.this, IMAGE_DIRECTORY_NAME);
        Log.e("result", "image===========" + imageUri);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 100);
        }
        Log.e("result", "start===========" + imageUri);

    }

    private void chooseProfilePicFromGallery() {
        chooseType = 2;


        if (ActivityCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            return;
        }//if

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
        Log.e("result", "start gal===========");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK && requestCode == 200) {
            outputUri = Utils.getOutputMediaFileUri(EditProfileActivity.this, "");

            try {

                Bitmap loadedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                Log.e("width = " + loadedBitmap.getWidth(), "height = " + loadedBitmap.getHeight() + outputUri);

                if (loadedBitmap.getWidth() > 1000 || loadedBitmap.getHeight() > 1000) {
                    loadedBitmap = Utils.resizeImageForImageView(loadedBitmap);
                }
                imageUri = data.getData();
                String picturePath = Utils.getPath(EditProfileActivity.this, imageUri);

                Log.e("image uri", "on imageuri===========" + picturePath);
                ExifInterface exif = null;
                try {
                    File pictureFile = new File(picturePath);
                    exif = new ExifInterface(pictureFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int orientation = ExifInterface.ORIENTATION_NORMAL;

                if (exif != null)
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        loadedBitmap = Utils.rotateBitmap(loadedBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        loadedBitmap = Utils.rotateBitmap(loadedBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        loadedBitmap = Utils.rotateBitmap(loadedBitmap, 270);
                        break;
                }
                File file = new File(picturePath);
                OutputStream outStream = null;

                try {
                    outStream = new FileOutputStream(picturePath);
                    loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outStream);
                    outStream.flush();
                    outStream.close();

                    if (loadedBitmap != null) {
                        bitmapImage = loadedBitmap;
                        ivUserImage.setImageBitmap(loadedBitmap);

                    }
                    loadedBitmap = null;


                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }//try
        }//Gallery

        else {
            Log.e("result", "false resultttttttttttt===========");
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            outputUri = Utils.getOutputMediaFileUri(EditProfileActivity.this, "");

            Log.e("uri", "output" + outputUri + "input" + imageUri);

            try {

                Bitmap loadedBitmap = null;
                String uri_temp = imageUri.getPath();
                uri_temp.replace("%20", " ");
                File imageFile = new File(uri_temp);
                loadedBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                if (loadedBitmap.getWidth() > 1000 || loadedBitmap.getHeight() > 1000) {
                    loadedBitmap = Utils.resizeImageForImageView(loadedBitmap);
                }
                ExifInterface exif = null;
                try {
                    File pictureFile = imageFile;
                    exif = new ExifInterface(pictureFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int orientation = ExifInterface.ORIENTATION_NORMAL;

                if (exif != null)
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        loadedBitmap = Utils.rotateBitmap(loadedBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        loadedBitmap = Utils.rotateBitmap(loadedBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        loadedBitmap = Utils.rotateBitmap(loadedBitmap, 270);
                        break;
                }

                File file = new File(uri_temp);
                OutputStream outStream = null;

                try {
                    outStream = new FileOutputStream(file);
                    loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outStream);
                    outStream.flush();
                    outStream.close();

                    if (loadedBitmap != null) {

                        bitmapImage = loadedBitmap;
                        ivUserImage.setImageBitmap(loadedBitmap);

                    }

                } catch (Exception ex) {
                    Log.e("onActivi....." +
                            "0.tyResult", ex.toString());
                }


                // loadedBitmap = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void showProgressBar(String message) {

        if (progressDialog != null) {
            // other wise we loose the reference and it never hide
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
}
