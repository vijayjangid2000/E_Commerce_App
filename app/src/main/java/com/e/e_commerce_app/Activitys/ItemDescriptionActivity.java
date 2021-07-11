package com.e.e_commerce_app.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.e.e_commerce_app.Adapters.ProductImage_Slider;
import com.e.e_commerce_app.ApplicationClass;
import com.e.e_commerce_app.Model.CartModel;
import com.e.e_commerce_app.Model.ItemListModel;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.APIs;
import com.e.e_commerce_app.Utility.NetworkChecking;
import com.e.e_commerce_app.Utility.RequestHandler;
import com.e.e_commerce_app.Utility.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
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

import static android.graphics.Color.WHITE;

public class ItemDescriptionActivity extends AppCompatActivity {

    LinearLayout rlMainItemDescLayout;
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ProductImage_Slider slider_productImage;

    ArrayList<ItemListModel> listItemDesc;
    ArrayList<String> listSize;

    ArrayAdapter<String> adapterSpinner;
    SliderView sliderView;
    ImageView ivbHeart, ivbPlus, ivbMinus;

    TextView tvQuantityNumber, tvItemStock, tvAddToCart, tvBuyNow, tvSizeSpin, tvBadge,
            tvProPrice, tvProName, tvProColor, tvItemDesc;

    Boolean clicked = true;

    String productId, color = "", size, cartDiscount, proImage, inc,
            cartId, cardShipCharge, itemPrice = "0", cartItemTotal = "0", discountType,
            actionType = "";

    LinearLayout llCartBy;
    int cartBuyNum = 0, temp_badge = 0, mInteger = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item__description_);
        sessionManager = new SessionManager(ItemDescriptionActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rlMainItemDescLayout = findViewById(R.id.mainItemDesLay);
        tvProPrice = findViewById(R.id.pro_des_price);
        tvProName = findViewById(R.id.tv_pro_d_name);
        tvProColor = findViewById(R.id.pro_dsc_color);
        tvSizeSpin = findViewById(R.id.sizespinner);
        ivbHeart = findViewById(R.id.heartbtn);
        ivbPlus = findViewById(R.id.plusbtn);
        ivbMinus = findViewById(R.id.minusbtn);
        tvQuantityNumber = findViewById(R.id.quntity_nmbr);
        tvItemDesc = findViewById(R.id.itemDesc);
        tvItemStock = findViewById(R.id.itemStock);
        tvAddToCart = findViewById(R.id.addcart);
        tvBuyNow = findViewById(R.id.bynow);
        sliderView = findViewById(R.id.pro_item_pager);
        llCartBy = findViewById(R.id.cartbybtn);
        rlMainItemDescLayout.setVisibility(View.INVISIBLE);

        if (getIntent().getExtras() != null) {
            productId = (getIntent().getExtras().getString("Id"));
        }
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();

        listSize = new ArrayList<>();

        getCategoryProductDetail();
        getavailablestock();
        ivbHeart.setOnClickListener(view -> {
            if (!NetworkChecking.isConnectedNetwork(ItemDescriptionActivity.this)) {
                //internet is connected do something
                Toast.makeText(ItemDescriptionActivity.this, "Internet Not Available", Toast.LENGTH_SHORT).show();
                return;

            }
            if (sessionManager.getString("email").equalsIgnoreCase("") || sessionManager.getString("email")
                    .isEmpty() && sessionManager.getString("password").equalsIgnoreCase("") ||
                    sessionManager.getString("password").isEmpty()) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.addcart), "You are not login ",
                        Snackbar.LENGTH_LONG).setAction("Go Login", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ItemDescriptionActivity.this, LoginActivity.class);
                        intent.putExtra("Item_to_login", "" + productId);
                        intent.putExtra("ItemtologNmbr", "1");
                        startActivity(intent);
                        finish();

                    }
                });
                snackbar.show();
                return;
            }

            addwishlist();
        });

        ivbMinus.setOnClickListener(view -> {
            if (mInteger == 1) {
                tvQuantityNumber.setText("1");
            } else {
                mInteger = mInteger - 1;
                tvQuantityNumber.setText("" + mInteger);
            }
        });

        ivbPlus.setOnClickListener(view -> {
            if (cartBuyNum == 3) {
                Toast.makeText(ItemDescriptionActivity.this, "Stock not available", Toast.LENGTH_SHORT).show();
            } else {
                mInteger = mInteger + 1;
                tvQuantityNumber.setText("" + mInteger);
            }
        });

        tvAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkChecking.isConnectedNetwork(ItemDescriptionActivity.this)) {
                    //internet is connected do something
                    Toast.makeText(ItemDescriptionActivity.this, "Internet Not Available", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (sessionManager.getString("email").equalsIgnoreCase("") || sessionManager.getString("email").isEmpty() && sessionManager.getString("password").equalsIgnoreCase("") || sessionManager.getString("password").isEmpty()) {

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.addcart), "You are not login ",
                            Snackbar.LENGTH_LONG).setAction("Go Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ItemDescriptionActivity.this, LoginActivity.class);
                            intent.putExtra("Item_to_login", "" + productId);
                            intent.putExtra("ItemtologNmbr", "1");
                            startActivity(intent);
                            finish();

                        }
                    });
                    snackbar.show();
                } else if (cartBuyNum == 3) {
                    Toast.makeText(ItemDescriptionActivity.this, "Stock not available", Toast.LENGTH_SHORT).show();
                } else {
                    cartBuyNum = 1;
                    addCart();

                }
            }
        });

        tvBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkChecking.isConnectedNetwork(ItemDescriptionActivity.this)) {
                    //internet is connected do something

                    Toast.makeText(ItemDescriptionActivity.this, "Internet Not Available", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (sessionManager.getString("email").equalsIgnoreCase("") || sessionManager.getString("email").isEmpty() && sessionManager.getString("password").equalsIgnoreCase("") || sessionManager.getString("password").isEmpty()) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.addcart), "You are not login ",
                            Snackbar.LENGTH_LONG).setAction("Go Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ItemDescriptionActivity.this, LoginActivity.class);
                            intent.putExtra("Item_to_login", "" + productId);
                            intent.putExtra("ItemtologNmbr", "1");
                            startActivity(intent);
                            finish();

                        }
                    });
                    snackbar.show();
                } else if (cartBuyNum == 3) {
                    Toast.makeText(ItemDescriptionActivity.this, "Stock not available", Toast.LENGTH_SHORT).show();
                } else {
                    cartBuyNum = 2;
                    addCart();
                }

            }
        });

        tvSizeSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SizeDialog();
            }
        });

    }

    public void getCategoryProductDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(ItemDescriptionActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e(" categor_prdct_url", "=" + APIs.GET_PRODUCT_DETAILS);

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.GET_PRODUCT_DETAILS,
                response -> {
                    listItemDesc = new ArrayList<>();

                    Log.e(" get_pro", "=" + response);
                    progressDialog.dismiss();
                    rlMainItemDescLayout.setVisibility(View.VISIBLE);
                    String sizes;

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data_product");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                if (jsonObject1.getString("product_name").equals("null") || jsonObject1.getString("product_name").equalsIgnoreCase("")) {
                                    tvProName.setText("N/A");
                                } else {
                                    tvProName.setText(jsonObject1.getString("product_name"));
                                }
                                if (jsonObject1.getString("color").equals("null") || jsonObject1.getString("color").equalsIgnoreCase("")) {
                                    tvProColor.setText("N/A");
                                } else {
                                    tvProColor.setText(jsonObject1.getString("color"));

                                }
                                if (jsonObject1.getString("price").equals("null") || jsonObject1.getString("price").equalsIgnoreCase("")) {
                                    tvProPrice.setText("N/A");
                                    itemPrice = "0";
                                } else {
                                    itemPrice = jsonObject1.getString("price");
                                    tvProPrice.setText("₹" + jsonObject1.getString("price"));

                                }
                                if (jsonObject1.getString("is_product_wishlisted").equals("yes")) {
                                    ivbHeart.setImageResource(R.drawable.heart);

                                } else {
                                    ivbHeart.setImageResource(R.drawable.likeheart);
                                }
                                if (jsonObject1.getString("description").equals("null") || jsonObject1.getString("description").equalsIgnoreCase("")) {
                                    tvItemDesc.setText("N/A");
                                } else {
                                    tvItemDesc.setText(Html.fromHtml(jsonObject1.getString("description")));
                                }
                                color = (String) tvProColor.getText();
                                proImage = jsonObject1.getString("image");


                                JSONArray jsonArray2 = jsonObject1.getJSONArray("related_image");
                                Log.e("arraylng", "=" + jsonArray2.length());
                                if (jsonArray2.length() == 0) {
                                    ItemListModel imgmodel4 = new ItemListModel();
                                    imgmodel4.setRelatedImg("" + R.drawable.no_image);
                                    listItemDesc.add(imgmodel4);
                                } else {
                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
                                        ItemListModel imgmodel4 = new ItemListModel();
                                        imgmodel4.setRelatedImg(jsonObject2.getString("image_name"));
                                        listItemDesc.add(imgmodel4);
                                    }
                                }
                                slider_productImage = new ProductImage_Slider(ItemDescriptionActivity.this, listItemDesc);
                                sliderView.setSliderAdapter(slider_productImage);
                                sliderView.setIndicatorAnimation(IndicatorAnimationType.SWAP); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                                sliderView.setIndicatorSelectedColor(WHITE);
                                sliderView.setIndicatorUnselectedColor(Color.BLACK);
                                sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :\

                                JSONArray jsonArray3 = jsonObject1.getJSONArray("product_attributes");
                                if (jsonArray3.length() <= 0) {
                                    tvSizeSpin.setText("N/A");
                                    listSize.add("N/A");
                                    discountType = "0";
                                } else {
                                    for (int k = 0; k < jsonArray3.length(); k++) {
                                        JSONObject jsonObject3 = jsonArray3.getJSONObject(k);
                                        sizes = (jsonObject3.getString("size"));
                                        discountType = jsonObject3.getString("discount_type");
                                        listSize.add(sizes);
                                    }
                                    tvSizeSpin.setText("" + listSize.get(0).toString());
                                    adapterSpinner = new ArrayAdapter<>(ItemDescriptionActivity.this, android.R.layout.simple_list_item_1, listSize);
                                }
                            }
                        } else {
                            Toast.makeText(ItemDescriptionActivity.this, "" + message, Toast.LENGTH_SHORT).show();//
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.hide();
                        Log.e("categoryproductlist", "=1");
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
                params.put("user_id", "" + sessionManager.getString("userid"));
                params.put("product_id", "" + productId);
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

    public void getavailablestock() {
        final ProgressDialog progressDialog = new ProgressDialog(ItemDescriptionActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("Stock url", "" + APIs.STOCK_AVAILABLE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.STOCK_AVAILABLE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(" stock response", "=" + jsonObject.toString());
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                // itemStock.setText(""+jsonObject.get("total_products")+" in stock");
                                tvItemStock.setText("In Stock");
                                tvItemStock.setTextColor(ItemDescriptionActivity.this.getResources().getColor(R.color.green));

                            } else {
                                tvItemStock.setText("Out Of Stock");
                                tvItemStock.setTextColor(ItemDescriptionActivity.this.getResources().getColor(R.color.red));
                                cartBuyNum = 3;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my stock", "=1");
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
                params.put("product_id", "" + productId);
                Log.e("param stock", params.toString());
                return params;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(40),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void addCart() {
        size = tvSizeSpin.getText().toString();
        final ProgressDialog progressDialog = new ProgressDialog(ItemDescriptionActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("addtocart_url", "" + APIs.ADD_CART);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.ADD_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" add_cart", "=" + response);

                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                cartId = (jsonObject.getString("cart_id"));
                                if (jsonObject.getString("shipping_charges").equals("null") || jsonObject.getString("shipping_charges").equals("")) {
                                    cardShipCharge = "0";

                                } else {
                                    cardShipCharge = (jsonObject.getString("shipping_charges"));
                                }
                                if (jsonObject.getString("discount").equals("null") || jsonObject.getString("discount").equals("")) {
                                    cartDiscount = "0";
                                } else {
                                    cartDiscount = (jsonObject.getString("discount"));
                                }
                                if (jsonObject.getString("item_count").equals("null") || jsonObject.getString("item_count").equals("")) {
                                    inc = "0";
                                } else {
                                    inc = jsonObject.getString("item_count");
                                }

                                tvBadge.setText(jsonObject.getString("item_count"));
                                editor.putString("carttotal", "" + inc);
                                editor.commit();
                                finish();
                                startActivity(getIntent());
                                if (cartBuyNum == 1) {
                                    Toast.makeText(ItemDescriptionActivity.this, "" + message, Toast.LENGTH_SHORT).show();

                                } else {
                                    CartModel cartModel = new CartModel();
                                    cartModel.setCartItemName(tvProName.getText().toString());
                                    cartModel.setCartQuantity(tvQuantityNumber.getText().toString());
                                    cartModel.setCartId(cartId);
                                    cartModel.setCartItemShipngChrg(cardShipCharge);
                                    cartModel.setCartItemImg(proImage);
                                    cartModel.setCartItemPrice(itemPrice);
                                    cartModel.setCartItemDiscnt(cartDiscount);
                                    if (discountType == "Rs") {
                                        cartModel.setCartItemDiscntType("Rs");
                                    } else if (discountType == "%") {
                                        double p = Double.parseDouble(itemPrice);
                                        double d = Double.parseDouble(cartDiscount);
                                        Double s = 100 - d;
                                        Double res = (s * p) / 100;
                                        cartModel.setCartItemDiscntType("Rs");
                                    }
                                    ApplicationClass.cartModelslist.add(cartModel);
                                    Log.e("cartp", "" + cartModel.getCartItemPrice());
                                    Log.e("daats", "" + cartModel.getCartItemShipngChrg());
                                    Log.e("daatq", "" + cartModel.getCartQuantity());
                                    Intent intent = new Intent(ItemDescriptionActivity.this, OrderProcessActivity.class);
                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(ItemDescriptionActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my cart", "=1");
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
                params.put("products_id", "" + productId);
                params.put("size", "" + size);
                params.put("quantity", "" + tvQuantityNumber.getText());
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

    public void addwishlist() {
        final String msg;
        if (clicked) {
            ivbHeart.setImageResource(R.drawable.heart);
            actionType = "add";
            msg = "added wishlist";
            clicked = false;
        } else {
            clicked = true;
            ivbHeart.setImageResource(R.drawable.likeheart);
            msg = "removed wishlist";
            actionType = "delete";

        }
        final ProgressDialog progressDialog = new ProgressDialog(ItemDescriptionActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e("addwlurl", "" + APIs.ADD_WISH_LIST);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.ADD_WISH_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" add_wisjlist", "=" + response);

                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equalsIgnoreCase("1")) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.heartbtn), "" + msg, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                Toast.makeText(ItemDescriptionActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("resp my wishlist", "=1");
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
                params.put("product_id", "" + productId);
                params.put("action_type", "" + actionType);
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
        if (sessionManager.getString("email").equalsIgnoreCase("") || sessionManager.getString("email").isEmpty() && sessionManager.getString("password").equalsIgnoreCase("") || sessionManager.getString("password").isEmpty()) {
            MenuInflater inflater = getMenuInflater();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return true;
        } else {

            MenuInflater inflater = getMenuInflater();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            inflater.inflate(R.menu.cart_menu, menu);
            final MenuItem item = menu.findItem(R.id.cart_icon);
            MenuItemCompat.setActionView(item, R.layout.cart_count_layout);
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

            tvBadge = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
            tvBadge.setText("" + sharedPreferences.getString("carttotal", null));
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
        temp_badge = Integer.parseInt(tvBadge.getText().toString());
        if (tvBadge != null) {
            if (tvBadge.getText().equals("0") || tvBadge.getText().equals("null") || temp_badge < 0) {
                if (tvBadge.getVisibility() != View.GONE) {
                    tvBadge.setVisibility(View.GONE);
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
                Intent intent2 = new Intent(ItemDescriptionActivity.this, MyCartActivity.class);
                startActivity(intent2);
                return true;
        }
        return true;
    }

    public void SizeDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(ItemDescriptionActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        //   dialog.getWindow().setLayout(500, 500);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText editText = dialog.findViewById(R.id.dialog_edittext);
        ListView listview = dialog.findViewById(R.id.dialog_listview);
        TextView dialog_tittle = dialog.findViewById(R.id.dialog_tittle);
        editText.setVisibility(View.GONE);
        dialog_tittle.setTextSize(16);
        dialog_tittle.setText("choose size");
        final ImageView imageView = dialog.findViewById(R.id.close_dialog);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        adapterSpinner = new ArrayAdapter<>(ItemDescriptionActivity.this, android.R.layout.simple_list_item_1, listSize);
        listview.setAdapter(adapterSpinner);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long b) {
                tvSizeSpin.setText(adapterSpinner.getItem(position));
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }


}
