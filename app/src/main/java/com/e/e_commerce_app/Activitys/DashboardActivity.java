package com.e.e_commerce_app.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.e.e_commerce_app.Adapters.CategoryAdapter;
import com.e.e_commerce_app.Adapters.ImageSliderAdapter;
import com.e.e_commerce_app.Adapters.ItemListAdapter;
import com.e.e_commerce_app.Model.CategoryModel;
import com.e.e_commerce_app.Model.ItemListModel;
import com.e.e_commerce_app.Model.PagerModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.NetworkChecking;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class DashboardActivity extends AppCompatActivity {

    static final String SHOWCASE_ID = "Simple Showcase";
    SessionManager prefSessionManager, sessionManager;
    SharedPreferences sharedPref;

    static ArrayList<CategoryModel> listCategory;
    ArrayList<PagerModel> listSliderImageItems;
    static ArrayList<ItemListModel> listItems;

    LinearLayout llMainDash, llDashError;
    TextView tvbViewAllCategory, tvbViewAllProducts, tvErrorMsg, tvBadgeCount;
    RecyclerView rvImageList;
    RecyclerView rvCategory;
    SliderView sliderView;
    AlertDialog progressDialog;

    ItemListAdapter adapterItemList;
    ImageSliderAdapter adapterImageSlider;
    CategoryAdapter adapterCategory;

    int tempBadgeCount = 0;
    float fadeValue = 0.4f, brightenValue = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        llDashError = findViewById(R.id.dashError);
        tvErrorMsg = findViewById(R.id.dashErrormsg);
        llMainDash = findViewById(R.id.mainLay);
        tvbViewAllCategory = findViewById(R.id.tv_cat_viewAll);
        tvbViewAllProducts = findViewById(R.id.tv_products_all);
        sliderView = findViewById(R.id.slider_PagerImage);
        rvImageList = findViewById(R.id.rv_products);
        rvCategory = findViewById(R.id.rv_categories);

        sessionManager = new SessionManager(DashboardActivity.this);
        sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        prefSessionManager = new SessionManager(this);
        listCategory = new ArrayList<>();

        tvbViewAllCategory.setOnClickListener(v -> {
            v.setAlpha(fadeValue);
            Intent intent = new Intent(DashboardActivity.this, CategoriesActivity.class);
            startActivity(intent);
        });

        tvbViewAllProducts.setOnClickListener(v -> {
            v.setAlpha(fadeValue);
            Intent intent = new Intent(DashboardActivity.this, ProductsActivity.class);
            intent.putExtra("parsecatId", "0");
            startActivity(intent);
        });

        if (!NetworkChecking.isConnectedNetwork(DashboardActivity.this)) {
            toast("Internet Not Available");
            tvErrorMsg.setText("Internet Not Available ");
            llDashError.setVisibility(View.VISIBLE);
            llMainDash.setVisibility(View.GONE);
        }

        requestGetSlider();
        requestGetProductList();
        requestGetCategories();

        if (prefSessionManager.isFirstTimeLaunch()) showTutorSequence(0);
    }

    private void requestGetSlider() {

        showProgressBar("Please wait");
        Log.e("Slider_URL", "" + APIs.GET_SLIDER);

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_SLIDER,
                response -> {
                    listSliderImageItems = new ArrayList<>();
                    progressDialog.cancel();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e(" slider", "=" + jsonObject.toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                PagerModel imgmodel4 = new PagerModel();
                                imgmodel4.setSliderImage(jsonObject1.getString("slider_image"));

                                if (jsonObject1.getString("title").equals("null") || jsonObject1.getString("title").equalsIgnoreCase(""))
                                    imgmodel4.setSliderTitle("N/A");
                                else
                                    imgmodel4.setSliderTitle(jsonObject1.getString("title"));

                                if (jsonObject1.getString("description").equals("null") || jsonObject1.getString("description").equalsIgnoreCase(""))
                                    imgmodel4.setSliderDesc("N/A");
                                else
                                    imgmodel4.setSliderDesc(jsonObject1.getString("description"));


                                listSliderImageItems.add(imgmodel4);

                            }

                            adapterImageSlider = new ImageSliderAdapter(DashboardActivity.this, listSliderImageItems);
                            sliderView.setSliderAdapter(adapterImageSlider);
                            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                            sliderView.setIndicatorSelectedColor(Color.WHITE);
                            sliderView.setIndicatorUnselectedColor(Color.BLACK);
                            sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                            sliderView.startAutoCycle();

                        } else {
                            progressDialog.cancel();
                            toast(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.cancel();
                        Log.e("resp my slider", "=1");
                    }
                },
                error -> progressDialog.cancel()) {

            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                Log.e("param slider", params.toString());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void requestGetProductList() {
        showProgressBar("Please wait");
        llMainDash.setVisibility(View.GONE);
        Log.e("productListUrl", "" + APIs.PRODUCT_LIST);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.PRODUCT_LIST,
                response -> {
                    Log.e(" get_products", "=" + response);
                    progressDialog.cancel();
                    llMainDash.setVisibility(View.VISIBLE);

                    listItems = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data_product");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                ItemListModel itemListModel1 = new ItemListModel();
                                itemListModel1.setListimg(jsonObject1.getString("image"));
                                itemListModel1.setPro_Name(jsonObject1.getString("product_name"));
                                itemListModel1.setPro_Price(jsonObject1.getInt("price"));
                                itemListModel1.setID(jsonObject1.getInt("id"));
                                listItems.add(itemListModel1);
                            }

                            adapterItemList = new ItemListAdapter(DashboardActivity.this, listItems,
                                    new ItemListAdapter.SelectProductListener() {
                                        @Override
                                        public void productListener(String product) {

                                        }
                                    });
                            final LinearLayoutManager mLayoutManager;
                            mLayoutManager = new LinearLayoutManager(DashboardActivity.this,
                                    LinearLayoutManager.HORIZONTAL, false);
                            rvImageList.setHasFixedSize(false);
                            rvImageList.setLayoutManager(mLayoutManager);
                            rvImageList.setItemAnimator(new DefaultItemAnimator());
                            rvImageList.setAdapter(adapterItemList);
                        } else {
                            progressDialog.cancel();
                            llMainDash.setVisibility(View.GONE);
                            tvErrorMsg.setText("" + message);
                            llDashError.setVisibility(View.VISIBLE);
                            toast(message);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.cancel();
                        Log.e("resp my productlist", "=1");
                    }


                },
                error -> {
                    progressDialog.cancel();
                    error.printStackTrace();
                }) {

            protected Map<String, String> getParams() {

                HashMap<String, String> params = new HashMap<>();
                params.put("category_id", "95");
                params.put("min_price", "0");
                params.put("max_price", "0");
                params.put("discount", "0");

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

    private void requestGetCategories() {

        showProgressBar("Please wait");
        llMainDash.setVisibility(View.GONE);

        Log.e("category_url", "" + APIs.GET_CATEGORY);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_CATEGORY,
                response -> {
                    Log.e(" get_categories", "=" + response);

                    progressDialog.cancel();
                    llMainDash.setVisibility(View.VISIBLE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {


                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject jsonObject1;
                            for (int i = 0; i < jsonArray.length(); i++) {

                                jsonObject1 = jsonArray.getJSONObject(i);
                                CategoryModel categoryModel1 = new CategoryModel();
                                categoryModel1.setCatName(jsonObject1.getString("name"));
                                categoryModel1.setCatId(jsonObject1.getString("id"));
                                categoryModel1.setCatImg(jsonObject1.getString("image"));
                                listCategory.add(categoryModel1);

                            }

                            adapterCategory = new CategoryAdapter(DashboardActivity.this, listCategory, new CategoryAdapter.SelectCategoryListener() {
                                @Override
                                public void categorySelected(String category) {

                                }
                            });

                            final LinearLayoutManager manager;
                            manager = new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            rvCategory.setHasFixedSize(false);
                            rvCategory.setLayoutManager(manager);
                            rvCategory.setItemAnimator(new DefaultItemAnimator());
                            rvCategory.setAdapter(adapterCategory);

                        } else {
                            progressDialog.cancel();
                            llMainDash.setVisibility(View.GONE);
                            tvErrorMsg.setText("" + message);
                            tvErrorMsg.setVisibility(View.VISIBLE);
                            toast(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.cancel();
                        Log.e("resp my categorylist", "=1");
                    }
                },
                error -> progressDialog.cancel()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (
                ((sessionManager.getString("mobile").equalsIgnoreCase("") || sessionManager.getString("mobile").isEmpty())
                        && (sessionManager.getString("password").equalsIgnoreCase("") || sessionManager.getString("password").isEmpty()))
                        &&
                        ((sessionManager.getString("email").equalsIgnoreCase("") || sessionManager.getString("email").isEmpty())
                                && (sessionManager.getString("passworde").equalsIgnoreCase("") || sessionManager.getString("passworde").isEmpty())
                        )
        ) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.dashboard_menu2, menu);
            return true;
        }

//        if  (sessionManager.getString("email").equalsIgnoreCase("") || sessionManager.getString("email").isEmpty()
//                &&
//                sessionManager.getString("passworde").equalsIgnoreCase("") || sessionManager.getString("passworde").isEmpty()) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.dashboard_menu2, menu);
//             Log.e("session mobile:", "" + sessionManager.getString("email"));
//             Log.e("session pswrd:", "" + sessionManager.getString("passworde"));
//            return true;
//        }
        else {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.dashboard_menu, menu);
            final MenuItem item = menu.findItem(R.id.cart_icon);
            MenuItemCompat.setActionView(item, R.layout.cart_count_layout);
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

            tvBadgeCount = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
            tvBadgeCount.setText("" + sharedPref.getString("carttotal", null));

            setupBadge();
            notifCount.setOnClickListener(v -> onOptionsItemSelected(item));

            return super.onCreateOptionsMenu(menu);
        }
    }

    private void showTutorSequence(int millis) {

        ShowcaseConfig config = new ShowcaseConfig(); //create the showcase config
        config.setDelay(millis); //set the delay of each sequence using millis variable

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID); // create the material showcase sequence


        sequence.setConfig(config); //set the showcase config to the sequence.

        sequence.addSequenceItem(tvbViewAllProducts, "show all products", "GOT IT"); // add view for the first sequence, in this case it is a button.

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(tvbViewAllCategory)
                        .setDismissText("OK")
                        .setContentText("show all categories")
                        .withCircleShape()
                        .build()
        ); // add view for the second sequence, in this case it is a textview.

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(sliderView)
                        .setDismissText("UNDERSTAND")
                        .setContentText("Sliders")
                        .withCircleShape()
                        .build()
        ); // add view for the third sequence, in this case it is a checkbox.

        sequence.start(); //start the sequence showcase

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = null;

        switch (item.getItemId()) {

            case R.id.user_icon:
                intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                break;

            case R.id.cart_icon:
                intent = new Intent(DashboardActivity.this, MyCartActivity.class);
                break;

            case R.id.noti_icon:
                intent = new Intent(DashboardActivity.this, NotificationActivity.class);
                break;

            case R.id.login_icon:
                intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.putExtra("skip_id", 12);
                break;
        }

        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        tempBadgeCount = Integer.parseInt(tvBadgeCount.getText().toString());
        if (tvBadgeCount != null) {
            if (tvBadgeCount.getText().equals("0") || tvBadgeCount.getText().equals("null") || tempBadgeCount < 0) {
                if (tvBadgeCount.getVisibility() != View.GONE) {
                    tvBadgeCount.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        tvbViewAllCategory.setAlpha(brightenValue);
        tvbViewAllProducts.setAlpha(brightenValue);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null) progressDialog.cancel();
    }
}
