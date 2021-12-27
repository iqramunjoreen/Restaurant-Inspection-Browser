package com.sfu.cmpt276groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.sfu.cmpt276groupproject.Model.DateCalculator;
import com.sfu.cmpt276groupproject.Model.Inspection;
import com.sfu.cmpt276groupproject.Model.Restaurant;
import com.sfu.cmpt276groupproject.Model.RestaurantManager;
import com.sfu.cmpt276groupproject.Model.SearchDB;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * UI of searching feature
 */
public class SearchActivity extends AppCompatActivity {

    SearchDB searchDB;
    RestaurantManager manager;
    public static boolean checker = true;
    public static List<Restaurant> searchResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            manager = RestaurantManager.getInstance(this.getApplicationContext());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        searchDB = SearchDB.getInstance(this.getApplicationContext());
        if (checker) {
            try {
                initializeDB();
                checker = false;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        setupHazardRadioBtn();
        setupGreaterOrLessButtons();
        setupSearchButton();
        setupCancelButton();
    }

    private void setupGreaterOrLessButtons() {
        RadioGroup group = findViewById(R.id.greaterOrLess);
        RadioButton greaterBtn = new RadioButton(SearchActivity.this);
        greaterBtn.setText(R.string.greater);
        group.addView(greaterBtn);
        RadioButton lessBtn = new RadioButton(SearchActivity.this);
        lessBtn.setText(R.string.less);
        group.addView(lessBtn);
    }

    private void setupHazardRadioBtn() {
        final boolean[] isSelected = new boolean[] {false, false, false};
        final RadioButton lowBtn = findViewById(R.id.low);
        lowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected[0]) {
                    lowBtn.setChecked(false);
                    isSelected[0] = false;
                } else {
                    lowBtn.setChecked(true);
                    isSelected[0] = true;
                }
            }
        });
        final RadioButton moderateBtn = findViewById(R.id.moderate);
        moderateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected[1]) {
                    moderateBtn.setChecked(false);
                    isSelected[1] = false;
                } else {
                    moderateBtn.setChecked(true);
                    isSelected[1] = true;
                }
            }
        });
        final RadioButton highBtn = findViewById(R.id.high);
        highBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected[2]) {
                    highBtn.setChecked(false);
                    isSelected[2] = false;
                } else {
                    highBtn.setChecked(true);
                    isSelected[2] = true;
                }
            }
        });
    }

    private void setupCancelButton() {
        final Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                searchDB.close();
                finish();
            }
        });
    }

    private void setupSearchButton() {
        final Button searchBtn = findViewById(R.id.OKBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText enteredName = findViewById(R.id.enteredName);
                String enteredKeyword = enteredName.getText().toString().trim();

                RadioButton lowBtn = findViewById(R.id.low);
                RadioButton moderateBtn = findViewById(R.id.moderate);
                RadioButton highBtn = findViewById(R.id.high);
                String selectedHazard = "";
                if ((lowBtn.isChecked() == moderateBtn.isChecked()) && (moderateBtn.isChecked() == highBtn.isChecked())) {
                    selectedHazard = "";
                } else {
                    if (lowBtn.isChecked()) {
                        selectedHazard = selectedHazard + "L";
                    }
                    if (moderateBtn.isChecked()) {
                        selectedHazard = selectedHazard + "M";
                    }
                    if (highBtn.isChecked()) {
                        selectedHazard = selectedHazard + "H";
                    }
                }

                RadioGroup group = findViewById(R.id.greaterOrLess);
                int id = group.getCheckedRadioButtonId();
                RadioButton btn = findViewById(id);
                String greaterOrLess;
                if (btn == null) {
                    greaterOrLess = "";
                } else {
                     greaterOrLess = btn.getText().toString().trim();
                }

                EditText enteredNumber = findViewById(R.id.enteredNum);
                String tempNumber = enteredNumber.getText().toString().trim();
                boolean flag = true;
                int criticalNumber = 0;
                if (tempNumber.equals(getString(R.string.ex_0_1))) {
                    criticalNumber = -1;
                } else {
                    try {
                        criticalNumber = Integer.parseInt(tempNumber);
                        if (criticalNumber < 0) {
                            Toast.makeText(SearchActivity.this, getString(R.string.error_toast_1),
                                    Toast.LENGTH_SHORT).show();
                            flag = false;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(SearchActivity.this,
                                getString(R.string.error_toast_2),
                                Toast.LENGTH_SHORT).show();
                        flag = false;
                    }
                }


                Switch favouriteBtn = findViewById(R.id.searchFavourite);
                boolean favourite = favouriteBtn.isChecked();

                // Todo: Need to display the search result on map and list
                // Todo: Need to change the content in the if-else case
                if (flag) {
                    searchOnDB(enteredKeyword, selectedHazard, greaterOrLess, criticalNumber, favourite);
                    // For test
                    for (int i = 0; i < searchResult.size(); i++) {
                        Log.i("Result", searchResult.get(i).getName());
                    }

                    if (searchResult.size() > 0) {
                        // need to change
                        Toast.makeText(SearchActivity.this,
                                R.string.search_successful,
                                Toast.LENGTH_SHORT).show();
                        manager.setSearchList(searchResult);
                        // Update search list and Go to MainActivity
                        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SearchActivity.this, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                    }
                    // need to change
                    Toast.makeText(SearchActivity.this,
                            getString(R.string.search_success),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // need to change
                    Toast.makeText(SearchActivity.this,
                            getString(R.string.wrong_arguement),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchOnDB(String enteredKeyword, String selectedHazard, String greaterOrLess,
                            int criticalNumber, boolean favourite) {
        Cursor c;
        if (!selectedHazard.equals("")) {
            List<Cursor> cursorList = new ArrayList<>();
            if (selectedHazard.contains("L")) {
                Cursor cursor = searchDB.getSearchResult(enteredKeyword, getString(R.string.hazard_low_text),
                        criticalNumber, greaterOrLess);
                cursorList.add(cursor);
            }
            if (selectedHazard.contains("M")) {
                Cursor cursor = searchDB.getSearchResult(enteredKeyword, getString(R.string.hazard_moderate_text),
                        criticalNumber, greaterOrLess);
                cursorList.add(cursor);
            }
            if (selectedHazard.contains("H")) {
                Cursor cursor = searchDB.getSearchResult(enteredKeyword, getString(R.string.hazard_high_text),
                        criticalNumber, greaterOrLess);
                cursorList.add(cursor);
            }
            int size = cursorList.size();
            c = new MergeCursor(cursorList.toArray(new Cursor[size]));
        } else {
            c = searchDB.getSearchResult(enteredKeyword, selectedHazard, criticalNumber, greaterOrLess);
        }

        if (c != null) {
            if (searchResult.size() != 0) {
                searchResult.clear();
            }
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                int trackingNumCol = c.getColumnIndex(SearchDB.KEY_TRACKINGNUM);
                String trackingNumber = c.getString(trackingNumCol);
                searchResult.add(manager.findRestaurant(trackingNumber));
            }
            c.close();
        }
        if (favourite) {
            List<Restaurant> temp = new ArrayList<>();
            for (int i = 0; i < searchResult.size(); i++) {
                if (searchResult.get(i).isFavorite()) {
                    temp.add(searchResult.get(i));
                }
            }
            searchResult.clear();
            searchResult.addAll(temp);
        }
    }

    private void initializeDB() throws ParseException {
        searchDB.open();
        searchDB.deleteAll();
        for (int i = 0; i < manager.getRestaurants().size(); i++) {
            Restaurant targetRestaurant = manager.getRestaurant(i);
            String trackingNumber = targetRestaurant.getTrackingNumber();
            String name = targetRestaurant.getName();
            String hazard;
            int criticalNumber;
            if (targetRestaurant.getInspections().size() != 0) {
                hazard = targetRestaurant.getInspections().get(0).getHazard().getHazardString(this.getApplicationContext());
                criticalNumber = 0;
                for (int j = 0; j < targetRestaurant.getInspections().size(); j++) {
                    Inspection targetInspection = targetRestaurant.getInspections().get(j);
                    int res = DateCalculator.getDayInterval(targetInspection);
                    if (res < 365) {
                        criticalNumber = criticalNumber + targetInspection.getNumberOfCritical();
                    }
                }
            } else {
                hazard = "";
                criticalNumber = -1;
            }
            searchDB.insertRow(trackingNumber, name, hazard, criticalNumber);
        }
    }

}