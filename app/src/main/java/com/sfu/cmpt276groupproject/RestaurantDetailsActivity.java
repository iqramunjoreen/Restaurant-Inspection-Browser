package com.sfu.cmpt276groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.cmpt276groupproject.Model.*;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * UI for the Restaurant Details screen
 */
public class RestaurantDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String RESTAURANT_INDEX = "Restaurant index";

    RestaurantManager manager;
    Restaurant restaurant;
    FavoriteDB favoriteList;
    int index;
    boolean isNoIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "RestDetails onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        handleIntent();
        setupRestaurantInfo();
        setUpFavoriteButton();

        if(isNoIssues){
            displayNotification();
        }
        else {
            populateListView();
            //Switch to Inspection Details
            registerClickCallbacks();
        }
    }

    private void setUpFavoriteButton() {
        favoriteList = new FavoriteDB(RestaurantDetailsActivity.this);
        final Button btn = findViewById(R.id.favoriteButton);

        if(restaurant.isFavorite()){ btn.setText(R.string.unfavorite); }
        else{ btn.setText(R.string.favorite); }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText(R.string.unfavorite);
                if(!restaurant.isFavorite()){
                    boolean result = favoriteList.addFavorite(restaurant.getName(), restaurant.getTrackingNumber());
                    restaurant.setFavorite(true);
                    btn.setText(R.string.unfavorite);
                }
                else{
                    boolean result = favoriteList.deleteFavorite((restaurant.getTrackingNumber()));
                    restaurant.setFavorite(false);
                    btn.setText(R.string.favorite);
                }
            }
        });
    }

    private void handleIntent() {
        Intent intent = this.getIntent();
        index = intent.getIntExtra(RESTAURANT_INDEX, -1);
        try {
            manager = RestaurantManager.getInstance(this.getApplicationContext());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        restaurant = manager.getRestaurant(index);

        isNoIssues = restaurant.getInspections().size() == 0;
    }

    public static Intent makeRestaurantIntent(Context context, int RestaurantIndex) {
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(RESTAURANT_INDEX, RestaurantIndex);
        return intent;
    }

    private void registerClickCallbacks() {
        ListView list = findViewById(R.id.Inspection_List);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = InspectionDetailsActivity.makeInspectionIntent(RestaurantDetailsActivity.this, position, index);
                startActivity(intent);
            }
        });
    }


    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new ListAdapter();
        ListView list = findViewById(R.id.Inspection_List);
        list.setAdapter(adapter);
    }

    @SuppressLint("StringFormatInvalid")
    private void setupRestaurantInfo() {
        TextView restaurantName = findViewById(R.id.Name);
        TextView restaurantAddress = findViewById(R.id.Address);
        TextView restaurantCoordinate = findViewById(R.id.Coords);
        restaurantCoordinate.setOnClickListener(this);

        restaurantName.setText(String.format("%s", restaurant.getName()));
        restaurantAddress.setText(String.format(getString(R.string.address), restaurant.getAddress(), restaurant.getCity()));
        restaurantCoordinate.setText(String.format(getString(R.string.coordinate), restaurant.getLatitude(), restaurant.getLongitude()));

        List<Inspection> InspectionList = manager.getInspections(restaurant);
    }

    private void displayNotification(){
            TextView issueText = findViewById(R.id.Issue_Notation);
            issueText.setText(R.string.issue_notation);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Coords) {
            Intent intent = MainActivity.makeMainIntent(RestaurantDetailsActivity.this, index);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private class ListAdapter extends ArrayAdapter<Inspection> {
        public ListAdapter(){
            super(RestaurantDetailsActivity.this, R.layout.inspection_item, manager.getInspections(restaurant));
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(convertView == null){
                itemView = getLayoutInflater().inflate(R.layout.inspection_item, parent, false);
            }

            List<Inspection> InspectionList = manager.getInspections(restaurant);
            //If restaurant has inspections

            //Set Last Update
            Inspection currentInspection = InspectionList.get(position);
            String date = null;
            try {
                date = DateCalculator.changeDateToString(RestaurantDetailsActivity.this, currentInspection);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView dateText = itemView.findViewById(R.id.Update);
            // Change the date to other languages
            assert date != null;
            if (!date.contains(getString(R.string.days_ago))) {
                if (date.contains("January")) {
                    date = date.replace("January", getString(R.string.january));
                } else if (date.contains("February")) {
                    date = date.replace("February", getString(R.string.february));
                } else if (date.contains("March")) {
                    date = date.replace("March", getString(R.string.march));
                } else if (date.contains("April")) {
                    date = date.replace("April", getString(R.string.april));
                } else if (date.contains("May")) {
                    date = date.replace("May", getString(R.string.may));
                } else if (date.contains("June")) {
                    date = date.replace("June", getString(R.string.june));
                } else if (date.contains("July")) {
                    date = date.replace("July", getString(R.string.july));
                } else if (date.contains("August")) {
                    date = date.replace("August", getString(R.string.august));
                } else if (date.contains("September")) {
                    date = date.replace("September", getString(R.string.september));
                } else if (date.contains("October")) {
                    date = date.replace("October", getString(R.string.october));
                } else if (date.contains("November")) {
                    date = date.replace("November", getString(R.string.november));
                } else if (date.contains("December")) {
                    date = date.replace("December", getString(R.string.december));
                }
            }

            dateText.setText(String.format(getString(R.string.inspection_date), date));

            //Set Hazard Icon & text
            ImageView hazardImageView = itemView.findViewById(R.id.Hazard_image);
            TextView levelView = itemView.findViewById(R.id.Hazard_level);
            Hazard currentHazard = currentInspection.getHazard();
            hazardImageView.setImageResource(currentHazard.getHazardImageResource());
            levelView.setText(currentHazard.getHazardString(RestaurantDetailsActivity.this));
            levelView.setTextColor(currentHazard.getHazardColor(RestaurantDetailsActivity.this));

            //Critical Issues Count
            TextView criticalIssueText = itemView.findViewById(R.id.Critical_issue);
            TextView nonCriticalIssueText = itemView.findViewById(R.id.Non_critical_issue);
            int counterCritical = 0;
            int counterNonCritical = 0;
            counterCritical = currentInspection.getNumberOfCritical();
            counterNonCritical = currentInspection.getNumberOfNonCritical();
            criticalIssueText.setText(String.format(getString(R.string.critical_issues), counterCritical));
            nonCriticalIssueText.setText(String.format(getString(R.string.non_critical_issues), counterNonCritical));

            return itemView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
