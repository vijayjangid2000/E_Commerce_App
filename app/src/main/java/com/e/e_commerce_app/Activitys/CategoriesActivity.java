package com.e.e_commerce_app.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.e_commerce_app.Adapters.CategoryAdapter;
import com.e.e_commerce_app.R;
import com.e.e_commerce_app.Utility.SessionManager;

public class CategoriesActivity extends AppCompatActivity {

    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    RecyclerView catRecycleView;
    CategoryAdapter categoryAdapter;
    ImageView ivCategoryImage;
    TextView tv;
    int temp_badge = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionManager = new SessionManager(CategoriesActivity.this);
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        catRecycleView = findViewById(R.id.rv_categories);
        ivCategoryImage = findViewById(R.id.cat_img);
        categoryAdapter = new CategoryAdapter(CategoriesActivity.this, DashboardActivity.listCategory, new CategoryAdapter.SelectCategoryListener() {
            @Override
            public void categorySelected(String category) {

            }
        });
        RecyclerView.LayoutManager manager = new GridLayoutManager(CategoriesActivity.this, 2);
        catRecycleView.setHasFixedSize(false);
        catRecycleView.setLayoutManager(manager);
        catRecycleView.setItemAnimator(new DefaultItemAnimator());
        catRecycleView.setAdapter(categoryAdapter);

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
            setupBadge();
            tv.setText("" + sharedPreferences.getString("carttotal", null));
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
                return true;


            case R.id.cart_icon:
                Intent intent2 = new Intent(CategoriesActivity.this, MyCartActivity.class);
                startActivity(intent2);
                return true;

        }
        return true;
    }
}
