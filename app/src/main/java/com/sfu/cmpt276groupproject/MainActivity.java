package com.sfu.cmpt276groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sfu.cmpt276groupproject.UI.MainPagerAdapter;
import com.sfu.cmpt276groupproject.UI.MapFragment;
import com.sfu.cmpt276groupproject.UI.RestaurantListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UI for the map and list screens
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ViewPager pager;
    FragmentPagerAdapter adapter;
    BottomNavigationView navigationView;
    int selectIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleIntent();
        setupBottomNavigation();
        setupViewPager();
        SharedPreferences sharedPreferences = getSharedPreferences(WelcomeActivity.LAST_UPDATED_DATE, 0);
        WelcomeActivity.counter = sharedPreferences.getInt("UpdatedCounter", 0);

        // initialize navigationView to the map fragment
        if (savedInstanceState == null) {
            navigationView.setSelectedItemId(R.id.restaurant_map);
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        selectIndex = intent.getIntExtra("RESTAURANT_INDEX", -1);
        //int restaurantIndex = intent.getIntExtra("RESTAURANT_INDEX", -1);
        //if (restaurantIndex != -1) {    // clicked GPS coordinates
        //    MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(0);
        //    mapFragment.selectMarker(restaurantIndex);
        //}
    }
    private void setupViewPager() {
        // Set up view pager and adapter
        List<Fragment> fragments = new ArrayList<>(
                Arrays.asList(new MapFragment(selectIndex), new RestaurantListFragment()));
        pager = findViewById(R.id.view_pager);
        adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments, fragments.size());
        pager.setAdapter(adapter);

    }

    private void setupBottomNavigation() {
        // Set up bottom navigation
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

    }

    public static Intent makeMainIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
    public static Intent makeMainIntent(Context context, int restaurantIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("RESTAURANT_INDEX", restaurantIndex);
        return intent;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.restaurant_map:
                pager.setCurrentItem(0);
                return true;
            case R.id.restaurant_list:
                pager.setCurrentItem(1);
                return true;
        }
        return false;
    }
}